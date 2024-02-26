package cena;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Textura {
    public static Texture loadTexture(GL2 gl, String path) {
        try {
            // Obtém o recurso do classpath usando ClassLoader
            InputStream stream = Textura.class.getClassLoader().getResourceAsStream("imagens/" + path);
            if (stream == null) {
                throw new FileNotFoundException(path);
            }

            TextureData data = TextureIO.newTextureData(gl.getGLProfile(), stream, false, "png");
            return TextureIO.newTexture(data);
        } catch (IOException | GLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
