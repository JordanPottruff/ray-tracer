package common;

import com.github.jordanpottruff.jgml.*;

import static common.Util.vecToString;

public class Vertex {

    private final Vec3 position;
    private final Vec3 normal;
    private final Vec3 color;
    private final double opacity;
    private final double reflectance;
    private final Vec2 textureUV;

    public Vertex(Vec3 position, Vec3 normal, Vec3 color) {
        this(position, normal, color, 1.0, 0.0);
    }

    public Vertex(Vec3 position, Vec3 normal, Vec3 color, double opacity, double reflectance) {
        this(position, normal, color, opacity, reflectance, new Vec2(0, 0));
    }

    public Vertex(Vec3 position, Vec3 normal, Vec3 color, double opacity, double reflectance, Vec2 textureUV) {
        this.position = position;
        this.normal = normal.normalize();
        this.color = color;
        this.opacity = opacity;
        this.reflectance = reflectance;
        this.textureUV = textureUV;
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

    public Vec2 textureUV() {
        return this.textureUV;
    }

    public Vertex transform(Mat4 transformation) {
        // Transform position.
        Vec4 curPosition = new Vec4(position, 1);
        Vec3 newPosition = new Vec3(transformation.multiply(curPosition));
        // Transform normal.
        Vec4 curNormal = new Vec4(normal, 0);
        Vec3 newNormal = new Vec3(transformation.multiply(curNormal)).normalize();

        return new Vertex(newPosition, newNormal, color, opacity, reflectance);
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
