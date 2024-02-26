package cena;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class TelaFinal implements GLEventListener {

    //Tamanho da Tela e configurações do GL
    // Preenchimento
    public GLU glu;
    private final int screenWidth;
    private final int screenHeight;

    private Texture txtTelaFinal;
    private TextRenderer textRendererTexto;

    //Variavel de pontuação do jogador
    public int pontos;

    // Construtor que recebe largura e altura da tela
    public TelaFinal(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        //obtem o contexto Opengl
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(1, 1, 1, 1);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        gl.glLoadIdentity(); //lê a matriz identidade

        // Desativa o Z-buffer ao desenhar o plano de fundo
        gl.glDisable(GL2.GL_DEPTH_TEST);

        criarPlanoDeFundo(gl);

        gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS); // Salva o estado atual do OpenGL
        desenharTexto("Obrigada por jogar!\nVocê fez:  " + pontos + " pontos");
        gl.glPopAttrib(); // Restaura o estado do OpenGL
    }

    public void criarPlanoDeFundo(GL2 gl){
        gl.glEnable(GL2.GL_TEXTURE_2D);
        txtTelaFinal.enable(gl);
        txtTelaFinal.bind(gl);

        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(-1.0f, 1.0f);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(1.0f, 1.0f);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(-1.0f, -1.0f);
        gl.glEnd();

        txtTelaFinal.disable(gl);
        gl.glDisable(GL2.GL_TEXTURE_2D);
    }

    public void desenharTexto(String texto){
        // Configurações do texto
        textRendererTexto.beginRendering(screenWidth, screenHeight);

        // Quebra o texto em linhas usando "\n"
        String[] linhas = texto.split("\n");

        // Calcula a altura total do bloco de texto
        float totalTextHeight = 0;
        for (String linha : linhas) {
            Rectangle2D bounds = textRendererTexto.getBounds(linha);
            totalTextHeight += (float) bounds.getHeight();
        }

        // Calcula a posição y inicial para centralizar o bloco de texto
        float yPosicao = ((screenHeight + totalTextHeight) / 2 - 50);

        // Desenha cada linha separadamente
        for (String linha : linhas) {
            Rectangle2D bounds = textRendererTexto.getBounds(linha);
            float textWidth = (float) bounds.getWidth();

            // Calcula a posição x para centralizar a linha
            float xPosicao = (screenWidth - textWidth) / 2;

            // Adiciona o contorno
            for (int offset = -2; offset <= 4; offset += 1) {
                textRendererTexto.setColor(new Color(33, 12, 0)); // Cor do contorno
                textRendererTexto.draw(linha, (int) (xPosicao + offset), (int) (yPosicao + offset));
                textRendererTexto.draw(linha, (int) (xPosicao - offset), (int) (yPosicao - offset));
            }
            // Desenha a linha
            textRendererTexto.setColor(new Color(217, 140, 229, 255));
            textRendererTexto.draw(linha, (int) xPosicao, (int) yPosicao);

            // Atualiza a posição y para a próxima linha
            yPosicao -= (float) bounds.getHeight();
        }
        textRendererTexto.endRendering();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        glu = new GLU();
        GL2 gl = drawable.getGL().getGL2();

        textRendererTexto = new TextRenderer(new Font("Jokerman", Font.BOLD, 60));

        //Habilita o buffer de profundidade
        gl.glEnable(GL2.GL_DEPTH_TEST);

        // Carrega a textura usando a classe Textura
        txtTelaFinal = Textura.loadTexture(gl, "Fim.png");
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
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
