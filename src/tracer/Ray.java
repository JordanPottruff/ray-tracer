package tracer;

import com.github.jordanpottruff.jgml.Vec3;
import common.Face;
import common.Model;

import java.util.Optional;

public class Ray {

    private final Vec3 origin;
    private final Vec3 direction;

    public Ray(Vec3 origin, Vec3 direction) {
        this.origin = origin;
        this.direction = direction;
    }

    public Vec3 origin() {
        return this.origin;
    }

    public Vec3 direction() {
        return this.direction;
    }

    public Optional<Intersection> getIntersection(Face face, Model model, double epsilon) {
        Vec3 v1 = face.v1().position();
        Vec3 v2 = face.v2().position();
        Vec3 v3 = face.v3().position();

        Vec3 v1v2 = v2.subtract(v1);
        Vec3 v1v3 = v3.subtract(v1);
        Vec3 pvec = direction.cross(v1v3);
        double det = v1v2.dot(pvec);

        if(Math.abs(det) < epsilon) {
            return Optional.empty();
        }

        double invDet = 1.0 / det;

        Vec3 tvec = origin.subtract(v1);
        double u = tvec.dot(pvec) * invDet;
        if(u < 0 || u > 1) {
            return Optional.empty();
        }

        Vec3 qvec = tvec.cross(v1v2);
        double v = direction.dot(qvec) * invDet;
        if(v < 0 || u + v > 1) {
            return Optional.empty();
        }

        double t = v1v3.dot(qvec) * invDet;

        if (t < epsilon) {
            return Optional.empty();
        }

        Vec3 intersection = origin.add(direction.normalize().scale(t));

        Intersection result = new Intersection(intersection, this, model, face, new Vec3(u, v, 1-u-v), t);

        return Optional.of(result);
    }

    public Ray reflect(Vec3 position, Vec3 normal) {
        Vec3 direction = this.direction.subtract(normal.scale(2 * this.direction.dot(normal)));
        return new Ray(position, direction);
    }
}
