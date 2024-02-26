package cena;

import com.jogamp.opengl.GL2;

public class Objeto {
    private float x, y;
    public void CriarObstaculo(GL2 gl){
        gl.glBegin(GL2.GL_POLYGON);
        gl.glVertex2f(x + 0.06f, y -0.06f);
        gl.glVertex2f(x + 0.06f, y -0.2f);
        gl.glVertex2f(x -0.06f, y -0.2f);
        gl.glVertex2f(x -0.06f, y -0.06f);
        gl.glEnd();
    }

    public void GenerateNewRandom(){
        this.x = (float)(-0.87 + (Math.random() * 0.87));
        this.y = (float)(0 + (Math.random() * 0.87));
    }

    public float getX(){
        return this.x;
    }

    public float getY(){
        return this.y;
    }
}
