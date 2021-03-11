package tracer;

import com.github.jordanpottruff.jgml.Vec3;

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

    public Ray reflect(Vec3 position, Vec3 normal) {
        Vec3 direction = this.direction.subtract(normal.scale(2 * this.direction.dot(normal)));
        return new Ray(position, direction);
    }
}
