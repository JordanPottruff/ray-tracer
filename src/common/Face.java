package common;

import com.github.jordanpottruff.jgml.Vec3;

public class Face {

    private final Vertex v1;
    private final Vertex v2;
    private final Vertex v3;

    public Face(Vertex v1, Vertex v2, Vertex v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public Vertex v1() {
        return this.v1;
    }

    public Vertex v2() {
        return this.v2;
    }

    public Vertex v3() {
        return this.v3;
    }

    public Vec3 position(double u, double v) {
        checkUVW(u, v);
        return lerp(u, v, this.v1.position(), this.v2.position(), this.v3.position());
    }

    public Vec3 normal(double u, double v) {
        checkUVW(u, v);
        return lerp(u, v, this.v1.normal(), this.v2.normal(), this.v3.normal());
    }

    public Vec3 color(double u, double v) {
        checkUVW(u, v);
        return lerp(u, v, this.v1.color(), this.v2.color(), this.v3.color());
    }

    public double opacity(double u, double v) {
        checkUVW(u, v);
        return lerp(u, v, v1.opacity(), v2.opacity(), v3.opacity());
    }

    public double reflectance(double u, double v) {
        checkUVW(u, v);
        return lerp(u, v, v1.reflectance(), v2.reflectance(), v3.reflectance());
    }

    private Vec3 lerp(double u, double v, Vec3 v1, Vec3 v2, Vec3 v3) {
        double w = 1 - u - v;
        return v1.scale(w).add(v2.scale(u)).add(v3.scale(v));
    }

    private double lerp(double u, double v, double v1, double v2, double v3) {
        double w = 1 - u - v;
        return w * v1 + u * v2 + v * v3;
    }

    private void checkUVW(double u, double v) {
        if (u + v > 1.0 + Constants.EPSILON) {
            throw new IllegalArgumentException("UVW-coordinates should not sum to more than 1.");
        }
    }

}
