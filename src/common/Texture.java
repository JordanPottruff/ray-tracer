package common;

import com.github.jordanpottruff.jgml.Vec2;
import com.github.jordanpottruff.jgml.Vec3;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Texture {

    private final BufferedImage image;

    public Texture(String file) {
        BufferedImage fromFile;
        try {
            fromFile = ImageIO.read(new File(file));
        } catch (Exception e) {
            fromFile = null;
        }
        this.image = fromFile;
    }

    public Vec3 getColor(Vec2 uv) {
        return getColor(uv.x(), uv.y());
    }

    public Vec3 getColor(double u, double v) {
        int x = (int) (u * image.getWidth());
        int y = (int) (v * image.getHeight());

        Color color = new Color(image.getRGB(x, y));

        return new Vec3(color.getRed()/255.0, color.getGreen()/255.0, color.getBlue()/255.0);
    }
}
