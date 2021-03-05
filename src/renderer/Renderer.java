package renderer;

import com.github.jordanpottruff.jgml.Vec3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Renderer {

    private static final int COLOR_TYPE = BufferedImage.TYPE_INT_RGB;
    private final BufferedImage image;

    public Renderer(int width, int height) {
        this.image = new BufferedImage(width, height, COLOR_TYPE);
    }

    public void setColor(int x, int y, Vec3 rgb) {
        int r = (int) Math.round(rgb.x()*255);
        int g = (int) Math.round(rgb.y()*255);
        int b = (int) Math.round(rgb.z()*255);

        int color = ((r&0x0ff)<<16)|((g&0x0ff)<<8)|(b&0x0ff);

        image.setRGB(x, y, color);
    }

    public Vec3 getColor(int x, int y) {
        int color = image.getRGB(x, y);

        int r = (color >> 16) & 0xFF;
        int g = (color << 8) & 0xFF;
        int b = color & 0xFF;

        return new Vec3(r/255.0, g/255.0, b/255.0);
    }

    public void savePNG(String filename) {
        File file = new File(filename);
        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
