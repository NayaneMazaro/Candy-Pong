package cena;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;

import java.awt.*;

public class Cena implements GLEventListener {

    //Tamanho da Tela e configurações do GL
    // Preenchimento
    public int mode = GL2.GL_FILL;
    public GLU glu;
    private float xMin, xMax, yMin, yMax, zMin, zMax;


    //Configurações de textura
    private Texture txtPlFundo1; // Textura plano de fundo 1
    public Texture txtPlFundo2; // Textura plano de fundo 2
    public Texture txtPlacarPont; // Textura placar de pontuação
    private Texture txtRaquete; // Textura da raquete
    private Texture txtEsfera; // Textura da esfera (bolinha)
    private Texture txtObstaculo; // Textura do obstáculo

    //Configurações da iluminação
    public int tonalizacao = GL2.GL_SMOOTH;
    public boolean liga = true;

    //Renderer para troca de tela e TextRenderer para uso de Textos
    private final Renderer renderer;
    private TextRenderer textRenderer;

    private float cloud1X = -0.80f;
    private float cloud2X = -0.67f;
    private float cloud3X = -0.54f;
    private float cloudSpeed = 0.0001f;
    private float sphereSpeed = 0.010f;
    private float sphereX = 0f, sphereY = 0f;
    public float sphereSpeedX = sphereSpeed, sphereSpeedY = sphereSpeed;
    public float sphereSpeedAuxX, sphereSpeedAuxY;
    public float rotationAngle = 1.0f;

    //Controle de tamanho
    public float pxPositivo = 0.15f, pxNegativo = -0.15f; // Tamanho horizontal
    private final float pyBaseRaquete = -0.98f;
    private final float pyCimaRaquete = -0.88f; // Tamanho vertical

    //Pontuação e Vidas
    public int vidas = 5;
    public int pontos = 0;

    // Esfera
    int slices = 15;
    int stacks = 15;

    // Vértices do obstáculo
    float verticeXEsq = 0.4f;
    float verticeXDir = 0.6f;
    float verticeYSup = 0.3f;
    float verticeYInf = -0.15f;

    // Construtor da cena
    public Cena(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void display(GLAutoDrawable drawable) {

        //obtem o contexto Opengl
        GL2 gl = drawable.getGL().getGL2();
        //objeto para desenho 3D
        GLUT glut = new GLUT();

        //define a cor da janela (R, G, G, alpha)
        gl.glClearColor(1, 1, 1, 1);
        //limpa a janela com a cor especificada
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity(); //lê a matriz identidade


        // Desativa o Z-buffer ao desenhar o plano de fundo
        gl.glDisable(GL2.GL_DEPTH_TEST);

        criarPlanoDeFundo(gl, txtPlFundo1);

        //Pontuação
        criarLocalDePontuacao(gl);

        // Ativa o Z-buffer novamente para o resto da cena
        gl.glEnable(GL2.GL_DEPTH_TEST);

        // Raquete
        criarRaquete(gl);

        // Movimentação da esfera
        sphereX += sphereSpeedX;
        sphereY += sphereSpeedY;

        // Desenha a esfera/bolinha na posição especificada
        gl.glEnable(GL2.GL_TEXTURE_2D);
        txtEsfera.enable(gl);
        txtEsfera.bind(gl);
        //Define a Esfera (bolinha), suas posições e velocidade
        float sphereRadius = 0.045f;
        criarEsfera(glu, gl, sphereX, sphereY, sphereRadius, slices, stacks);
        txtEsfera.disable(gl);
        gl.glDisable(GL2.GL_TEXTURE_2D);

        // Verifica as bordas da tela e inverte a direção de objeto se necessário
        //Bateu na parede
        if (sphereX + sphereRadius >= 1.0f || sphereX - sphereRadius <= -1.0f) {
            sphereSpeedX = -sphereSpeedX; // Inverte a direção horizontal
        }
        //Bateu no "teto"
        if (sphereY + sphereRadius >= 0.8f) {
            sphereSpeedY = -sphereSpeedY; // Inverte a direção vertical
        }


        //Interação Bolinha e Raquete
        //Bolinha caiu (perdeu vida)
        if (sphereY - sphereRadius <= -1.0f) {
            vidas -= 1;
            sphereY = -0f;
            sphereX = -0f;
            sphereSpeedY = -sphereSpeedY;
        }

        // Verifica colisão da bolinha com a requete
        if (sphereX + sphereRadius >= pxNegativo && sphereX - sphereRadius <= pxPositivo &&
                sphereY + sphereRadius >= pyBaseRaquete && sphereY - sphereRadius <= pyCimaRaquete) {

            // Antes de mover a esfera, calcule a distância até a borda da raquete
            float distanceToRaqueteY = Math.abs(sphereY - pyCimaRaquete) - sphereRadius;
            float distanceToRaqueteXNegativo = Math.abs(sphereX - pxNegativo) - sphereRadius;
            float distanceToRaqueteXPositivo = Math.abs(sphereX - pxPositivo) - sphereRadius;

            // Se houver interpenetração, ajuste a posição da esfera
            if (distanceToRaqueteY < 0) {
                sphereY -= distanceToRaqueteY; // Ajusta a posição para evitar interpenetração
            } else if (distanceToRaqueteXNegativo <= 0) {
                sphereX -= distanceToRaqueteXNegativo;
            } else if (distanceToRaqueteXPositivo <= 0) {
                sphereX -= distanceToRaqueteXPositivo;
            }

            // Inverte a direção vertical
            sphereSpeedY = -sphereSpeedY;

            if (sphereY >= -0.9f) {
                pontos += 50;
            }
            if (pontos >= 200) {
                addSphereSpeed();
            }
        }

        // Desenha corações com base no número atual de vidas
        float xHeart = 0.9f; //Posição X em relação a tela
        float yHeart = 0.9f; //Posição Y em relação a tela
        float espacoEntreCoracoes = 0.1f;

        for (int i = 0; i < vidas; i++) {
            // Desenha o polígono
            gl.glColor3f(1.0f, 0.0f, 0.0f);
            gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex2f(xHeart, yHeart);
            gl.glVertex2f(xHeart + 0.029f/18, yHeart - 0.15f/18);
            gl.glVertex2f(xHeart + 0.125f/18, yHeart - 0.29f/18);
            gl.glVertex2f(xHeart + 0.6f/18, yHeart - 0.7f/18);
            gl.glVertex2f(xHeart + 1.08f/18, yHeart - 0.29f/18);
            gl.glVertex2f(xHeart + 1.175f/18, yHeart - 0.152f/18);
            gl.glVertex2f(xHeart + 1.2f/18, yHeart);
            gl.glEnd();
            gl.glFlush();

            // Desenha o primeiro semicírculo
            double limite = Math.PI;
            double j, cX, cY, rX, rY;

            cX = xHeart + 0.3/18;
            cY = yHeart;
            rX = 0.3f/18;
            rY = 0.3f/18;

            gl.glColor3f(1.0f, 0.0f, 0.0f);
            gl.glBegin(GL2.GL_POLYGON);
            for (j = 0; j < limite; j += 0.01) {
                gl.glVertex2d(cX + rX * Math.cos(j), cY + rY * Math.sin(j));
            }
            gl.glEnd();

            // Desenha o segundo semicírculo
            cX = xHeart + 0.9/18;

            gl.glBegin(GL2.GL_POLYGON);
            for (j = 0; j < limite; j += 0.01) {
                gl.glVertex2d(cX + rX * Math.cos(j), cY + rY * Math.sin(j));
            }
            gl.glEnd();

            xHeart -= espacoEntreCoracoes;
        }

        if (vidas <= 0) {
            telaFinal();
        }

        int posicaoTxtoPontX = 100;
        int posicaoTxtoPontY = Renderer.getWindowHeight() - (Renderer.getWindowHeight() / 16);

        desenhaTexto(gl, posicaoTxtoPontX, posicaoTxtoPontY, Color.BLACK, "Pontos: " + pontos);

        // Ativa iluminação antes de desenhar as nuvens
        if (liga) {
            iluminacaoAmbiente(gl);
            ligaLuz(gl);
        }
        // Desenha nuvens
        desenhaNuvens(gl, glut);

        // Desativa a iluminação após desenhar as nuvens
        if (liga) {
            gl.glDisable(GL2.GL_LIGHTING);
        }
        gl.glFlush();

        //Fase 2
        if (pontos >= 200){
            txtPlFundo1 = txtPlFundo2;
            desenharObstaculo(gl);

            // Verifica colisão com o obstáculo
            if (sphereX + sphereRadius >= verticeXEsq && sphereX - sphereRadius <= verticeXDir &&
                    sphereY + sphereRadius >= verticeYInf && sphereY - sphereRadius <= verticeYSup) {

                // Calcula a distância até a borda do objeto
                float distanceToYSup = Math.abs(sphereY - verticeYSup);
                float distanceToYInf = Math.abs(sphereY - verticeYInf);

                float distanceToXEsq = Math.abs(sphereX - verticeXEsq);
                float distanceToXDir = Math.abs(sphereX - verticeXDir);

                // Verifica qual borda do obstáculo a esfera está mais próxima
                float minDistance = Math.min(Math.min(distanceToYSup, distanceToYInf), Math.min(distanceToXEsq, distanceToXDir));

                // Define a área de tolerância ao redor das vértices
                float tolerance = 0.05f;

                if (minDistance == distanceToYSup || minDistance == distanceToYInf) {
                    sphereSpeedY = -sphereSpeedY; // Inverte a direção vertical
                } else if (minDistance == distanceToXEsq || minDistance == distanceToXDir) {
                    sphereSpeedX = -sphereSpeedX; // Inverte a direção horizontal
                }

                // Verifica se a esfera está colidindo com uma vértice dentro da área de tolerância
                if ((distanceToXEsq < tolerance && distanceToYSup < tolerance) ||
                        (distanceToXEsq < tolerance && distanceToYInf < tolerance) ||
                        (distanceToXDir < tolerance && distanceToYSup < tolerance) ||
                        (distanceToXDir < tolerance && distanceToYInf < tolerance)) {
                    sphereSpeedX = -sphereSpeedX; // Inverte a direção horizontal
                    sphereSpeedY = -sphereSpeedY; // Inverte a direção vertical
                }
            }
        }
    }

    //Metodos
    public void criarEsfera(GLU glu, GL2 gl, float sphereX, float sphereY, float sphereRadius,int slices, int stacks){
        gl.glPushMatrix();
        GLUquadric quad = glu.gluNewQuadric();

        glu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
        glu.gluQuadricNormals(quad, GLU.GLU_SMOOTH);
        glu.gluQuadricTexture(quad, true); // Habilita textura
        glu.gluQuadricNormals(quad, GLU.GLU_SMOOTH);
        gl.glTranslatef(sphereX, sphereY, 0f); // Define a posição da esfera

        // Adiciona a rotação em graus ao redor do eixo x y
        gl.glRotatef(rotationAngle, 1.0f, 1.0f, 0.0f);

        glu.gluSphere(quad, sphereRadius, slices, stacks);
        glu.gluDeleteQuadric(quad);
        gl.glPopMatrix();

        // Incrementa o ângulo de rotação
        rotationAngle += 0.5f;
    }

    public void criarRaquete(GL2 gl){
        gl.glPushMatrix();
        gl.glEnable(GL2.GL_TEXTURE_2D);
        txtRaquete.enable(gl);
        txtRaquete.bind(gl);

        gl.glColor3f(1, 1, 1);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(pxPositivo, pyCimaRaquete);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(pxPositivo, pyBaseRaquete);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(pxNegativo, pyBaseRaquete);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(pxNegativo, pyCimaRaquete);
        gl.glEnd();

        txtRaquete.disable(gl);
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glPopMatrix();
    }

    public void criarLocalDePontuacao(GL2 gl){
        gl.glEnable(GL2.GL_TEXTURE_2D);
        txtPlacarPont.enable(gl);
        txtPlacarPont.bind(gl);

        gl.glPushMatrix();
        gl.glColor4f(0.50196f, 0f, 0.50196f, 0.2f);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(-1.0f, 1.0f);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(1.0f, 1.0f);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(1.0f, 0.8f);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(-1.0f, 0.8f);
        gl.glEnd();

        txtPlacarPont.disable(gl);
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glPopMatrix();
    }

    public void criarPlanoDeFundo(GL2 gl, Texture txtPlFundo1){
        gl.glEnable(GL2.GL_TEXTURE_2D);
        txtPlFundo1.enable(gl);
        txtPlFundo1.bind(gl);

        gl.glPushMatrix();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(-1.0f, 1.0f);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(1.0f, 1.0f);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(-1.0f, -1.0f);
        gl.glEnd();
        gl.glPopMatrix();

        txtPlFundo1.disable(gl);
        gl.glDisable(GL2.GL_TEXTURE_2D);
    }

    public void desenhaNuvens(GL2 gl, GLUT glut){
        // Desenha a primeira esfera da nuvem
        gl.glPushMatrix();
        gl.glColor3f(0.9f, 0.9f, 0.9f);
        float cloudY = 0.63f;
        gl.glTranslated(cloud1X, cloudY, 0);
        desenhaNuvem(glut);
        gl.glPopMatrix();

        // Desenha a segunda esfera da nuvem
        gl.glPushMatrix();
        float cloud2Y = 0.66f;
        gl.glTranslated(cloud2X, cloud2Y, 0);
        desenhaNuvem(glut);
        gl.glPopMatrix();

        // Desenha a terceira esfera da nuvem
        gl.glPushMatrix();
        gl.glTranslated(cloud3X, cloudY, 0);
        desenhaNuvem(glut);
        gl.glPopMatrix();

        // Atualiza as coordenadas das nuvens para criar o movimento
        atualizarMovimentoNuvens();

        gl.glFlush();
    }

    private void desenhaNuvem(GLUT glut) {
        glut.glutSolidSphere(0.10, 25, 25);
    }

    private void atualizarMovimentoNuvens() {
        // Atualiza as coordenadas das nuvens para criar o movimento
        cloud1X += cloudSpeed;
        cloud2X += cloudSpeed;
        cloud3X += cloudSpeed;

        // Verifica se as nuvens saíram do cenário e reinicia a posição
        float limiteCenario = 1.2f; //
        if (cloud1X > limiteCenario) {
            cloud1X = -limiteCenario;
        }
        if (cloud2X > limiteCenario) {
            cloud2X = -limiteCenario;
        }
        if (cloud3X > limiteCenario) {
            cloud3X = -limiteCenario;
        }
    }

    public void desenharObstaculo(GL2 gl) {
        gl.glPushMatrix();
        gl.glEnable(GL2.GL_TEXTURE_2D);
        txtObstaculo.enable(gl);
        txtObstaculo.bind(gl);

        gl.glColor3f(1f, 1f, 1f);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2f(verticeXEsq, verticeYSup); // Ponto superior esquerdo
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2f(verticeXEsq, verticeYInf);// Ponto inferior esquerdo
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2f(verticeXDir, verticeYInf);// Ponto inferior direito
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2f(verticeXDir, verticeYSup);// Ponto superior direito
        gl.glEnd();

        txtObstaculo.disable(gl);
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glPopMatrix();
    }


    public void iluminacaoAmbiente(GL2 gl) {
        float[] luzAmbiente = {0.5f, 0.5f, 0.5f, 0.5f}; //cor
        float[] posicaoLuz = {70.0f, 200.0f, -50.0f, 0.0f};

        // define parametros de luz de n�mero 0 (zero)
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, luzAmbiente, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, posicaoLuz, 0);
    }

    public void ligaLuz(GL2 gl) {
        // habilita a definição da cor do material a partir da cor corrente
        gl.glEnable(GL2.GL_COLOR_MATERIAL);

        // habilita o uso da iluminação na cena
        gl.glEnable(GL2.GL_LIGHTING);
        // habilita a luz de número 0
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glShadeModel(tonalizacao);
    }

    //Aumenta a Velocidade da bolinha
    public void addSphereSpeed() {
        if (this.sphereSpeed <= 0.015f) {
            this.sphereSpeed += 0.002f;
            this.sphereSpeedY += 0.002f;
            this.sphereSpeedX += 0.002f;
        }

    }

    //Função para controle da velocidade da bolinha ao usar o "Pause"
    public void pausar() {

        if (this.sphereSpeedY != 0 || this.sphereSpeedX != 0) {
            this.sphereSpeedAuxY = this.sphereSpeedY;
            this.sphereSpeedAuxX = this.sphereSpeedX;
            this.sphereSpeedY = 0.0f;
            this.sphereSpeedX = 0.0f;
            rotationAngle += 0.0f;
            cloudSpeed = 0.0f;
            cloud1X += cloudSpeed;
            cloud2X += cloudSpeed;
            cloud3X += cloudSpeed;
        } else {
            this.sphereSpeedY = this.sphereSpeedAuxY;
            this.sphereSpeedX = this.sphereSpeedAuxX;
            rotationAngle += 0.5f;
            cloudSpeed = 0.0001f;
        }
    }

    public void desenhaTexto (GL2 gl,int xPosicao, int yPosicao, Color cor, String frase){
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        //Retorna a largura e altura da janela
        textRenderer.beginRendering(Renderer.getWindowWidth(), Renderer.getWindowHeight());
        textRenderer.setColor(cor);
        textRenderer.draw(frase, xPosicao, yPosicao);
        textRenderer.endRendering();
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, mode);
    }

    public void telaFinal(){
        renderer.switchToTelaFinal(this.pontos);
        Renderer.window.addGLEventListener(renderer.telaFinal);
    }

    @Override
    public void init (GLAutoDrawable drawable){

        //Estabelece as coordenadas do SRU (Sistema de Referencia do Universo)
        xMin = yMin = zMin = -1;
        xMax = yMax = zMax = 1;

        glu = new GLU();
        GL2 gl = drawable.getGL().getGL2();
        textRenderer = new TextRenderer(new Font("Comic Sans MS Negrito", Font.BOLD, 30));
        //Habilita o buffer de profundidade
        gl.glEnable(GL2.GL_DEPTH_TEST);

        // Carrega a textura usando a classe Textura
        txtPlFundo1 = Textura.loadTexture(gl, "Fase1.png");
        txtPlFundo2 = Textura.loadTexture(gl, "Fase2.png");
        txtPlacarPont = Textura.loadTexture(gl, "Pontuação.png");
        txtRaquete = Textura.loadTexture(gl, "Raquete.png");
        txtEsfera = Textura.loadTexture(gl, "Bala.png");
        txtObstaculo = Textura.loadTexture(gl, "Obstaculo.png");
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void dispose (GLAutoDrawable drawable){

    }

    @Override
    public void reshape (GLAutoDrawable drawable,int x, int y, int width, int height){
        //obtem o contexto grafico Opengl
        GL2 gl = drawable.getGL().getGL2();

        //evita a divisão por zero
        if (height == 0) height = 1;
        //calcula a proporção da janela (aspect ratio) da nova janela
        float aspect = (float) width / height;

        //seta o viewport para abranger a janela inteira
        gl.glViewport(0, 0, width, height);

        //ativa a matriz de projeção
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity(); //lê a matriz identidade

        //ativa a matriz de modelagem
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity(); //lê a matriz identidade
        System.out.println("Reshape: " + width + ", " + height);


        //Define a Proporção de tela
        float unitsTall = (float) Renderer.getWindowHeight() / Renderer.getWindowWidth() / Renderer.unitsWide;

        if (width >= height)
            gl.glOrtho(-Renderer.unitsWide / 2, Renderer.unitsWide / 2, unitsTall / 2, -unitsTall / 2, -1, 1);
        else
            gl.glOrtho(xMin, xMax, yMax / aspect, yMin / aspect, zMin, zMax);


    }
}