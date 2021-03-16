package tracer;

import com.github.jordanpottruff.jgml.Vec3;
import common.Face;
import common.Model;

public class Intersection {
    private final Vec3 point;
    private final Ray ray;
    private final Model model;
    private final Face face;
    private final Vec3 uvw;
    private final double t;

    public Intersection(Vec3 point, Ray ray, Model model, Face face, Vec3 uvw, double t) {
        this.point = new Vec3(point);
        this.ray = ray;
        this.model = model;
        this.face = face;
        this.uvw = new Vec3(uvw);
        this.t = t;
    }

    public Vec3 point() {
        return this.point;
    }

    public Ray ray() { return this.ray; }

    public Model model() { return this.model; }

    public Face face() {
        return this.face;
    }

    public Vec3 uvw() {
        return this.uvw;
    }

    public double t() {
        return this.t;
    }

    public Vec3 normal() {
        return this.face.normal(this.uvw.x(), this.uvw.y());
    }

    public Vec3 color() {
        return this.face.color(this.uvw.x(), this.uvw.y());
    }

    public double reflectance() {
        return this.face.reflectance(this.uvw.x(), this.uvw.y());
    }
}
