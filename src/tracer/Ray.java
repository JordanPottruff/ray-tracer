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
}
