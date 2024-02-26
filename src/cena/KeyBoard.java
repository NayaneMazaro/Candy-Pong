package cena;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

public class KeyBoard implements KeyListener{

    private final Cena cena;
    private final TelaInicial telaInicial;
    public final TelaFinal telaFinal;
    private final Renderer renderer;
    private float moviment = 0.05f;
    public boolean pause = false;


    // Construtor para a Cena
    public KeyBoard(Cena cena){
        this.cena = cena;
        this.telaFinal = null;
        this.telaInicial = null;
        this.renderer = new Renderer();
    }

    // Construtor para a telaFinal
    public KeyBoard(TelaFinal telaFinal) {
        this.cena = null;
        this.telaInicial = null;
        this.telaFinal = telaFinal;
        this.renderer = new Renderer();
    }

    // Construtor para Cena e telaInicial e fim de cena
    public KeyBoard(Cena cena, TelaInicial telaInicial, TelaFinal telaFinal, Renderer renderer) {
        this.cena = cena;
        this.telaFinal = telaFinal;
        this.telaInicial = telaInicial;
        this.renderer = renderer;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("Key pressed: " + e.getKeyCode());

        if (telaInicial != null) {
            switch (e.getKeyCode()){
                case KeyEvent.VK_SPACE:
                    if(telaInicial.menu == 1 || telaInicial.menu == 2){
                        renderer.switchToCena();
                    }
                    break;
                case KeyEvent.VK_C:
                    telaInicial.menu = 2;
                    break;
                case KeyEvent.VK_BACK_SPACE: // Apagar
                    telaInicial.menu = 1;
                    break;
            }
        }

        if (cena != null && telaInicial == null) {
            switch (e.getKeyCode()){
                case KeyEvent.VK_BACK_SPACE: // Apagar
                    cena.telaFinal();
                    break;

                case KeyEvent.VK_P:
                    this.pause = !pause;
                    cena.pausar();

                    if (pause){
                        this.moviment = 0f;
                    }
                    else{
                        this.moviment = 0.05f;
                    }
                    break;

                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                    if(cena.pxPositivo <= 1f){
                        cena.pxPositivo += moviment;
                        cena.pxNegativo += moviment;
                    }
                    break;

                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                    if(cena.pxNegativo >= -1f){
                        cena.pxPositivo -= moviment;
                        cena.pxNegativo -= moviment;
                    }
                    break;
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {// Esc
            renderer.toggleFullscreen(); // Troca entre fullscreen e janela
        }
    }


    @Override
    public void keyReleased(KeyEvent e) { }

}