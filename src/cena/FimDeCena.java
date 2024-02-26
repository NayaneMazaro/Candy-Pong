package cena;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;

import java.awt.*;

public class FimDeCena implements GLEventListener {
    private TextRenderer textRendererTitulo;
    private TextRenderer textRendererTexto;


    // Preenchimento

    private MyKeyListener keyListener;
    public int mode = GL2.GL_FILL;
    private float xMin, xMax, yMin, yMax, zMin, zMax;

    GLU glu;

    // Largura e altura da tela
    private int screenWidth;
    private int screenHeight;

    public KeyListener getKeyListener() {
        return keyListener;
    }

    private static class MyKeyListener implements KeyListener {
        @Override
        public void keyPressed(KeyEvent e) {
            // Lógica para lidar com pressionamento de tecla na cena de início
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // Lógica para lidar com liberação de tecla na cena de início
        }
    }

    // Construtor que recebe largura e altura da tela
    public FimDeCena(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.keyListener = new MyKeyListener();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        // obtem o contexto Opengl
        GL2 gl = drawable.getGL().getGL2();
        // define a cor da janela (R, G, G, alpha)
        gl.glClearColor(1, 1, 1, 1);
        // limpa a janela com a cor especificada
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        gl.glLoadIdentity(); // lê a matriz identidade

        // Largura e altura do retângulo
        float largura = 0.8f;  // Ajuste conforme necessário
        float altura = 1.5f;   // Ajuste conforme necessário

        // Coordenadas do centro da tela
        float centerX = 0.0f;
        float centerY = 0.0f;

        // Calcula as coordenadas dos vértices
        float xRight = centerX + largura / 2;
        float xLeft = centerX - largura / 2;
        float yTop = centerY + altura / 2;
        float yBottom = centerY - altura / 2;

        // Cor do retângulo
        gl.glColor3f(0.684f, 0.684f, 0.684f);

        // Desenha o retângulo
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex2f(xRight, yTop);
        gl.glVertex2f(xRight, yBottom);
        gl.glVertex2f(xLeft, yBottom);
        gl.glVertex2f(xLeft, yTop);
        gl.glEnd();

        desenhaTitulo(gl, 0, 710, Color.BLACK, "Fim de Jogo");
        desenhaTexto(gl, "Pontuação: \n");

        gl.glFlush();
    }

    public void desenhaTitulo(GL2 gl, int xPosicao, int yPosicao, Color cor, String frase) {
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

        // Obtém a largura do texto
        int textWidth = (int) textRendererTitulo.getBounds(frase).getWidth();

        // Calcula a posição x para centralizar o texto
        xPosicao = (screenWidth - textWidth) / 2;

        // Inicia o desenho do texto
        textRendererTitulo.beginRendering(screenWidth, screenHeight);
        textRendererTitulo.setColor(cor);
        textRendererTitulo.draw(frase, xPosicao, yPosicao);
        textRendererTitulo.endRendering();

        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, mode);
    }

    public void desenhaTexto(GL2 gl, String texto) {
        // Configurações do texto
        textRendererTexto.setColor(Color.BLACK);
        textRendererTexto.beginRendering(screenWidth, screenHeight);

        // Quebra o texto em linhas usando "\n"
        String[] linhas = texto.split("\n");

        // Calcula a altura total do texto
        float alturaTotal = 0;
        for (String linha : linhas) {
            alturaTotal += (float) textRendererTexto.getBounds(linha).getHeight();
        }

        // Inicia o desenho do texto
        float y = (float) screenHeight / 2 + alturaTotal / 2; // Posiciona no meio verticalmente
        for (String linha : linhas) {
            textRendererTexto.draw(linha, (int) ((screenWidth - textRendererTexto.getBounds(linha).getWidth()) / 2.0), (int) y);
            y -= (float) textRendererTexto.getBounds(linha).getHeight(); // Move para a próxima linha
        }

        // Finaliza o desenho do texto
        textRendererTexto.endRendering();
    }


    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        glu = new GLU();
        xMin = yMin = zMin = -1;
        xMax = yMax = zMax = 1;

        textRendererTitulo = new TextRenderer(new Font("Comic Sans MS Negrito", Font.BOLD, 30));
        textRendererTexto = new TextRenderer(new Font("Comic Sans MS", Font.ITALIC, 30));

        // Habilita o buffer de profundidade
        // gl.glEnable(GL2.GL_DEPTH_TEST);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        // obtem o contexto grafico Opengl
        GL2 gl = drawable.getGL().getGL2();

        // evita a divisão por zero
        if (height == 0)
            height = 1;
        // calcula a proporção da janela (aspect ratio) da nova janela
        float aspect = (float) width / height;

        // seta o viewport para abranger a janela inteira
        gl.glViewport(0, 0, width, height);

        // ativa a matriz de projeção
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity(); // lê a matriz identidade

        // ativa a matriz de modelagem
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity(); // lê a matriz identidade
        System.out.println("Reshape: " + width + ", " + height);
    }
}
