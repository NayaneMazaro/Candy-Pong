package cena;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;

import java.awt.Dimension;
import java.awt.Toolkit;

public class Renderer {
    public static GLWindow window = null;

    public static float unitsWide = 10;

    private Cena cena;
    private TelaInicial telaInicial;
    public TelaFinal telaFinal;


    //Cria a janela de rendeziração do JOGL
    public static void init() {
        GLProfile.initSingleton();
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities caps = new GLCapabilities(profile);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();

        window = GLWindow.create(caps);
        window.setSize(screenWidth, screenHeight);
        window.setResizable(true);
        window.setFullscreen(true);


        Renderer renderer = new Renderer();
        renderer.cena = new Cena(renderer);
        renderer.telaInicial = new TelaInicial(screenWidth, screenHeight);
        renderer.telaFinal = new TelaFinal(screenWidth, screenHeight);


        window.addGLEventListener(renderer.telaInicial);
        window.addKeyListener(new KeyBoard(renderer.cena, renderer.telaInicial, renderer.telaFinal, renderer));



        FPSAnimator animator = new FPSAnimator(window, 60);
        animator.start(); //inicia o loop de animação

        //encerrar a aplicacao adequadamente
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyNotify(WindowEvent e) {
                animator.stop();
                System.exit(0);
            }
        });

        window.setVisible(true);
    }

    public static int getWindowWidth () {
        return window.getWidth();
    }
    public static int getWindowHeight () {
        return window.getHeight();
    }

    public void toggleFullscreen() {
        if (window.isFullscreen()) {
            window.setVisible(false);
            window.setFullscreen(false);
            window.setVisible(true);
        } else {
            window.setFullscreen(true);
        }
    }

    public void switchToCena () {
        window.removeGLEventListener(telaInicial);
        window.addGLEventListener(cena);
        window.removeKeyListener(window.getKeyListeners()[0]);
        KeyBoard keyBoard = new KeyBoard(cena);
        window.addKeyListener(keyBoard);
    }

    public void switchToTelaFinal (int pontos) {
        telaFinal.pontos = pontos;
        window.removeGLEventListener(cena);
        window.addGLEventListener(telaFinal);
        window.removeKeyListener(window.getKeyListeners()[0]);
        KeyBoard keyBoard = new KeyBoard(telaFinal);
        window.addKeyListener(keyBoard);
    }


    public static void main (String[]args){
        init();
    }
}
