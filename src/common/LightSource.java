package common;

import com.github.jordanpottruff.jgml.Vec3;

import static common.Util.vecToString;

public class LightSource {

    private final Vec3 position;
    private final Vec3 color;
    private final double intensity;

    public LightSource(Vec3 position, Vec3 color, double intensity) {
        this.position = position;
        this.color = color;
        this.intensity = intensity;
    }

    public Vec3 position() {
        return this.position;
    }

    public Vec3 color() {
        return this.color;
    }

    public double intensity() {
        return this.intensity;
    }

    public String toString() {
        return toString(0);
    }

    public String toString(int spaces) {
        String spacing = " ".repeat(spaces);
        return spacing + "Light\n" +
                spacing + "--position: " + vecToString(this.position) + "\n" +
                spacing + "--color: " + vecToString(this.color) + "\n" +
                spacing + "--intensity: " + this.intensity + "\n";
    }
}
