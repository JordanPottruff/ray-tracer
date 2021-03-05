package tracer;

import com.github.jordanpottruff.jgml.Mat4;
import com.github.jordanpottruff.jgml.Vec3;
import com.github.jordanpottruff.jgml.Vec4;
import common.Face;
import renderer.Renderer;
import world.World;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static common.Util.vecToString;

public class Tracer {

    private static final double EPSILON = .0001;

    private final World world;
    private final int width;
    private final int height;
    private final double aspectRatio;

    public Tracer(World world, int width, int height) {
        this.world = world;
        this.width = width;
        this.height = height;
        this.aspectRatio = (double) width / height;
    }

    public Renderer trace(Mat4 transform, double fov) {
        Set<Face> faces = world.faces();
        Renderer renderer = new Renderer(width, height);
        for(int x=0; x<width; x++) {
            for(int y=0; y<height; y++) {
                Ray ray = getRay(transform, fov, x, y);
                Vec3 color = tracePixel(ray, faces);
                renderer.setColor(x, y, color);
            }
        }
        return renderer;
    }

    private Vec3 tracePixel(Ray ray, Set<Face> faces) {
        Intersection closest = null;
        for (Face face: faces) {
            Optional<Intersection> intersection = getIntersection(ray, face);
            if (intersection.isPresent()) {
                Intersection inter = intersection.get();
                closest = closest == null || closest.t > inter.t ? inter : closest;
            }
        }
        if (closest != null) {
            Face closestFace = closest.face();
            Vec3 uvw = closest.uvw();
            return closestFace.color(uvw.x(), uvw.y());
        } else {
            return new Vec3(0.0, 0.0, 0.0);
        }
    }

    private Optional<Intersection> getIntersection(Ray ray, Face face) {
        Vec3 v1 = face.v1().position();
        Vec3 v2 = face.v2().position();
        Vec3 v3 = face.v3().position();

        Vec3 v1v2 = v2.subtract(v1);
        Vec3 v1v3 = v3.subtract(v1);
        Vec3 pvec = ray.direction().cross(v1v3);
        double det = v1v2.dot(pvec);

        if(Math.abs(det) < EPSILON) {
            return Optional.empty();
        }

        double invDet = 1.0 / det;

        Vec3 tvec = ray.origin().subtract(v1);
        double u = tvec.dot(pvec) * invDet;
        if(u < 0 || u > 1) {
            return Optional.empty();
        }

        Vec3 qvec = tvec.cross(v1v2);
        double v = ray.direction().dot(qvec) * invDet;
        if(v < 0 || u + v > 1) {
            return Optional.empty();
        }

        double t = v1v3.dot(qvec) * invDet;

        Vec3 intersection = ray.origin().add(ray.direction().normalize().scale(t));

        Intersection result = new Intersection(face, intersection, new Vec3(u, v, 1-u-v), t);

        return Optional.of(result);
    }

    private Ray getRay(Mat4 transform, double fov, int x, int y) {
        double pixelX = (2 * ((x + 0.5) / width) - 1) * Math.tan(fov / 2 * Math.PI / 180) * aspectRatio;
        double pixelY = (1 - 2 * ((y + 0.5) / height)) * Math.tan(fov / 2 * Math.PI / 180);
        Vec4 pixel = new Vec4(pixelX, pixelY, -1, 1);
        Vec4 origin = new Vec4(0, 0, 0, 1);

        Vec3 pixelWorld = new Vec3(transform.multiply(pixel));
        Vec3 originWorld = new Vec3(transform.multiply(origin));

        Vec3 rayDirection = pixelWorld.subtract(originWorld).normalize();
        return new Ray(originWorld, rayDirection);
    }

    public static class Intersection {
        private final Face face;
        private final Vec3 point;
        private final Vec3 uvw;
        private final double t;

        public Intersection(Face face, Vec3 point, Vec3 uvw, double t) {
            this.face = face;
            this.point = new Vec3(point);
            this.uvw = new Vec3(uvw);
            this.t = t;
        }

        public Face face() {
            return this.face;
        }

        public Vec3 point() {
            return this.point;
        }

        public Vec3 uvw() {
            return this.uvw;
        }

        public double t() {
            return this.t;
        }
    }
}
