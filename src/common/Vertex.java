package common;

import com.github.jordanpottruff.jgml.Vec;
import com.github.jordanpottruff.jgml.Vec3;

import static common.Util.vecToString;

public class Vertex {

    private final Vec3 position;
    private final Vec3 normal;
    private final Vec3 color;
    private final double opacity;
    private final double reflectance;

    public Vertex(Vec3 position, Vec3 normal, Vec3 color) {
        this(position, normal, color, 1.0, 0.0);
    }

    public Vertex(Vec3 position, Vec3 normal, Vec3 color, double opacity, double reflectance) {
        this.position = position;
        this.normal = normal.normalize();
        this.color = color;
        this.opacity = opacity;
        this.reflectance = reflectance;
    }

    public Vec3 position() {
        return this.position;
    }

    public Vec3 normal() {
        return this.normal;
    }

    public Vec3 color() {
        return this.color;
    }

    public double opacity() {
        return this.opacity;
    }

    public double reflectance() {
        return this.reflectance;
    }

    public String toString() {
        return toString(0);
    }

    public String toString(int spaces) {
        String spacing = " ".repeat(spaces);
        return spacing + "Vertex\n" +
                spacing + "--position: " + vecToString(this.position) + "\n" +
                spacing + "--normal: " + vecToString(this.normal) + "\n" +
                spacing + "--color: " + vecToString(this.color) + "\n" +
                spacing + "--opacity: " + this.opacity + "\n" +
                spacing + "--reflectance: " + this.reflectance;
    }


}
