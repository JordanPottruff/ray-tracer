package common;

import com.github.jordanpottruff.jgml.Vec3;

public class Vertex {

    private Vec3 position;
    private Vec3 normal;
    private Vec3 color;
    private double opacity;
    private double reflectance;

    public Vertex(Vec3 position, Vec3 normal, Vec3 color) {
        this(position, normal, color, 1.0, 0.0);
    }

    public Vertex(Vec3 position, Vec3 normal, Vec3 color, double opacity, double reflectance) {
        this.position = position;
        this.normal = normal;
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
}
