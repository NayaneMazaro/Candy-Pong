package cena;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;
import java.awt.*;

public class TelaInicial implements GLEventListener {
    public int mode = GL2.GL_FILL;
    public GLU glu;
    private final int screenWidth;
    private final int screenHeight;
    public int menu = 1;

    //Variáveis de texturas
    private Texture txtInicial; // Textura plano de fundo inicial

    //Variáveis para Texto e Título
    private TextRenderer textRendererTitulo;
    private TextRenderer textRendererTexto;

    // Construtor da cena de Início
    public TelaInicial(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(1, 1, 1, 1);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

        gl.glLoadIdentity();

        CriarFundoInicial(gl); // Renderiza a textura
        gl.glFlush(); // Garante que a textura seja completamente desenhada e desabilitada
        gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS); // Salva o estado atual do OpenGL
        desenharTitulo(gl, Color.WHITE, "Candy Pong"); // Renderiza o texto

        switch (menu){
            case 1:
                // Menu Principal
                desenharMenuSup(gl,"""
                        Regras do Jogo:
                        
                         -Você começa com 5 vidas e 0 pontos, a cada vida perdida, o doce
                        irá voltar para sua posição original no centro da tela.
                        -Seu Objetivo é controlar a barra de chocolate e impedir que o doce caia.
                        Cada vez que você rebater, ganhará 50 pontos.
                        -Ao atingir 200 pontos, um obstáculo surgirá e o
                        doce ficará gradualmente mais rápido.
                        -O jogo termina quando todas as vidas acabarem
                        OU quando você decidir encerrar o jogo"""
                );
                break;
            case 2:
                desenharMenuSup(gl,"""
                        *  *  *
                        Conheça a criadora:
                        *  *
                         Olá, meu nome é Nayane, estudante de Ciência da Computação.
                         Tenho 23 anos na data em que criei esse jogo
                         e estou contente em finalizá-lo para que mais pessoas o vejam,
                         obrigada.
                        
                        *  *  *
                        
                         Backspace (apagar) - Voltar
                        """
                );
                break;

        }
        // Instruções de como jogar
        desenharMenuInf(gl,"""
                
                Comandos:
                
                 Espaço - Jogar
                (->) ou D -  Move a barra para a Direita
                
                (<-) ou A -  Move o jogador para Esquerda
                
                Tecla "P" -  Pausa o Jogo
                
                Backspace (apagar) -  Encerra o Jogo
                
                ESC - Sair e entrar do Fullscreen
                
                C - Conheça a criadora
                """
        );
        gl.glPopAttrib(); // Restaura o estado do OpenGL
        gl.glFlush(); // Garante que o texto seja completamente renderizado
    }

    // Métodos:
    public void CriarFundoInicial(GL2 gl) {

        gl.glEnable(GL2.GL_TEXTURE_2D);
        txtInicial.enable(gl);
        txtInicial.bind(gl);

        // Desenhar o objeto
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex2f(-1.0f, -1.0f);

        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex2f(1.0f, -1.0f);

        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex2f(1.0f, 1.0f);

        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex2f(-1.0f, 1.0f);
        gl.glEnd();

        // Desativar a textura
        txtInicial.disable(gl);
        gl.glDisable(GL2.GL_TEXTURE_2D);
    }

    public void desenharMenuSup(GL2 gl, String texto){
        float ySup = 0.9f;
        float yInf = 0.15f;
        float xEsq = -0.6f;
        float xDir = 0.6f;
        float espacoLinhas = 5.0f;

        gl.glColor4f(0.50196f, 0f, 0.50196f, 0.2f);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex2f(xEsq, ySup); // canto superior esquerdo
        gl.glVertex2f(xDir, ySup);  // canto superior direito
        gl.glVertex2f(xDir, yInf);   // canto inferior direito
        gl.glVertex2f(xEsq, yInf);  // canto inferior esquerdo
        gl.glEnd();

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

        // Inicia o desenho do texto dentro do retângulo superior
        float y = screenHeight * (ySup + 0.02f); // Posiciona no canto superior
        for (String linha : linhas) {
            float larguraTexto = (float) textRendererTexto.getBounds(linha).getWidth();
            float x = (screenWidth - larguraTexto) / 2.0f; // Centraliza o texto horizontalmente
            textRendererTexto.draw(linha, (int) x, (int) y);

            y -= (float) (textRendererTexto.getBounds(linha).getHeight() + espacoLinhas); // Move para a próxima linha
        }
        // Finaliza o desenho do texto
        textRendererTexto.endRendering();
    }

    public void desenharMenuInf(GL2 gl, String texto){
        float ySup = -0.25f;
        float yInf = -0.9f;
        float xEsq = -0.6f;
        float xDir = 0.6f;
        float espacoLinhas = 3.0f;

        gl.glColor4f(0.50196f, 0f, 0.50196f, 0.2f);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex2f(xEsq, ySup); // canto superior esquerdo
        gl.glVertex2f(xDir, ySup);  // canto superior direito
        gl.glVertex2f(xDir, yInf);   // canto inferior direito
        gl.glVertex2f(xEsq, yInf);  // canto inferior esquerdo
        gl.glEnd();

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

        // Inicia o desenho do texto dentro do retângulo inferior
        float y = screenHeight * (1 + ySup - 0.4f); // Posiciona no canto inferior
        for (String linha : linhas) {
            float larguraTexto = (float) textRendererTexto.getBounds(linha).getWidth();
            float x = (screenWidth - larguraTexto) / 2.0f; // Centraliza o texto horizontalmente
            textRendererTexto.draw(linha, (int) x, (int) y);

            y -= (float) (textRendererTexto.getBounds(linha).getHeight() + espacoLinhas); // Move para a próxima linha
        }
        // Finaliza o desenho do texto
        textRendererTexto.endRendering();
    }

    public void desenharTitulo(GL2 gl, Color cor, String frase) {
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

        // Obtém as dimensões do texto
        int textWidth = (int) textRendererTitulo.getBounds(frase).getWidth();
        int textHeight = (int) textRendererTitulo.getBounds(frase).getHeight();

        // Calcula a posição x e y para centralizar o texto
        int xPosicao = (screenWidth - textWidth) / 2;
        int yPosicao = ((screenHeight - textHeight) / 2) + 12;

        // Adiciona o contorno
        for (int offset = -2; offset <= 6; offset += 1) {
            textRendererTitulo.setColor(new Color(45, 17, 1)); // Cor do contorno
            textRendererTitulo.draw(frase, xPosicao + offset, yPosicao + offset);
            textRendererTitulo.draw(frase, xPosicao - offset, yPosicao - offset);
        }

        // Inicia o desenho do texto
        textRendererTitulo.beginRendering(screenWidth, screenHeight);
        textRendererTitulo.setColor(new Color(213, 162, 125, 255));
        textRendererTitulo.draw(frase, xPosicao, yPosicao);
        textRendererTitulo.endRendering();

        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, mode);
    }
    @Override
    public void init(GLAutoDrawable drawable) {
        glu = new GLU();
        GL2 gl = drawable.getGL().getGL2();
        textRendererTitulo = new TextRenderer(new Font("Jokerman", Font.BOLD, 90));
        textRendererTexto = new TextRenderer(new Font("Cambria", Font.ITALIC, 25));

        // Carrega a textura
        txtInicial = Textura.loadTexture(gl, "Inicio.png");
        if (txtInicial == null) {
            System.err.println("Falha ao carregar a textura Inicio.png");
            // Tratar adequadamente o caso de falha no carregamento
        }
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

        // seta o viewport para abranger a janela inteira
        gl.glViewport(0, 0, width, height);

        // ativa a matriz de projeção
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        // ativa a matriz de modelagem
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        System.out.println("Reshape: " + width + ", " + height);
    }
}
