package tracer;

import com.github.jordanpottruff.jgml.Mat4;
import com.github.jordanpottruff.jgml.Vec2;
import com.github.jordanpottruff.jgml.Vec3;
import com.github.jordanpottruff.jgml.Vec4;
import common.Face;
import common.LightSource;
import common.Model;
import renderer.Renderer;
import world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Tracer {

    private static final double EPSILON = .0001;
    private static final double REFLECTION_CUTOFF = 0.01;

    private final World world;
    private final int width;
    private final int height;
    private final Vec3 skyColor;
    private final Vec3 ambientColor;
    private final double aspectRatio;
    private int pixelsComplete = 0;
    private double percentComplete = 0;

    public Tracer(World world, int width, int height) {
        this(world, width, height, new Vec3(0, 0, 0), new Vec3(0, 0, 0));
    }

    public Tracer(World world, int width, int height, Vec3 skyColor, Vec3 ambientColor) {
        this.world = world;
        this.width = width;
        this.height = height;
        this.skyColor = skyColor;
        this.ambientColor = ambientColor;
        this.aspectRatio = (double) width / height;
    }

    public Renderer trace(Mat4 transform, double fov, int samples) {
        Set<Model> models = world.models();
        Set<LightSource> lights = world.lights();

        Sampler sampler = new Sampler();
        List<Sampler.PixelSample> pixelSamples = new ArrayList<>();
        for(int x=0; x<width; x++) {
            for(int y=0; y<height; y++) {
                pixelSamples.add(sampler.jitterPixel(x, y, samples));
            }
        }

        ExecutorService executor = Executors.newFixedThreadPool(5);
        Renderer renderer = new Renderer(width, height);
        ProgressTracker tracker = new ProgressTracker(width*height, 5);
        for(final Sampler.PixelSample pixelSample: pixelSamples) {
            executor.execute(() -> {
                Vec3 color = new Vec3(0, 0, 0);
                for(Vec2 sample: pixelSample.points()) {
                    Ray ray = getRay(transform, fov, sample.x(), sample.y());
                    color = color.add(tracePixel(ray, models, lights));
                }
                color = color.scale(1.0 / samples);
                synchronized(renderer) {
                    renderer.setColor(pixelSample.pixelX(), pixelSample.pixelY(), color);
                }

                Optional<Integer> progress = tracker.incrementComplete();
                progress.ifPresent(integer -> System.out.println(integer + "%"));
            });
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(24, TimeUnit.HOURS)) {
                executor.shutdownNow();
            };
        } catch (InterruptedException e) {
            e.printStackTrace();
            executor.shutdownNow();
        }
        return renderer;
    }

    private Vec3 tracePixel(Ray ray, Set<Model> models, Set<LightSource> lights) {
        return tracePixel(ray, models, lights, 1.0);
    }

    private Vec3 tracePixel(Ray ray, Set<Model> models, Set<LightSource> lights, double power) {
        if (power < REFLECTION_CUTOFF) {
            return new Vec3(0, 0, 0);
        }
        Optional<Intersection> closest = getClosest(ray, models, null);
        return closest.map(intersection -> {
            Vec3 surfaceColor = getLight(intersection, models, lights, intersection.model());
            Ray reflectionRay = ray.reflect(intersection.point(), intersection.normal());
            double reflectance = intersection.reflectance();
            Vec3 reflection = tracePixel(reflectionRay, models, lights, power*reflectance);
            return surfaceColor.scale(1-reflectance).add(reflection.scale(reflectance));
        }).orElse(this.skyColor);
    }


    private Optional<Intersection> getClosest(Ray ray, Set<Model> models, Model ignore) {
        Intersection closest = null;
        for(Model model: models) {
            if (model.equals(ignore)) {
                continue;
            }
            for(Face face: model.faces()) {
                Optional<Intersection> intersection = ray.getIntersection(face, model, EPSILON);
                if (intersection.isPresent()) {
                    Intersection inter = intersection.get();
                    closest = closest == null || closest.t() > inter.t() ? inter : closest;
                }
            }
        }
        return closest == null ? Optional.empty() : Optional.of(closest);
    }

    private Vec3 getLight(Intersection intersection, Set<Model> models, Set<LightSource> lights, Model ignore) {
        Vec3 origin = intersection.ray().origin();
        Vec3 point = intersection.point();
        Vec3 normal = intersection.normal();
        Vec3 surfaceColor = intersection.color();
        Face face = intersection.face();

        double specRatio = face.specularRatio();
        double diffRatio = face.diffuseRatio();
        double ambientRatio = Math.max(1.0 - specRatio - diffRatio, 0.0);
        Vec3 colorTotal = this.ambientColor.scale(ambientRatio);
        for (LightSource light: lights) {
            if (hasPathToLight(point, light, models, ignore)) {
                Vec3 diffuse = getDiffuse(point, light, normal);
                Vec3 specular = getSpecular(origin, point, normal, light, face.shine());
                colorTotal = colorTotal.add(diffuse.scale(diffRatio).add(specular.scale(specRatio)));
            }
        }
        double r = Math.min(surfaceColor.x() * colorTotal.x(), 1.0);
        double g = Math.min(surfaceColor.y() * colorTotal.y(), 1.0);
        double b = Math.min(surfaceColor.z() * colorTotal.z(), 1.0);
        return new Vec3(r, g, b);
    }

    private Vec3 getDiffuse(Vec3 point, LightSource light, Vec3 normal) {
        Vec3 lightDir = light.position().subtract(point).normalize();
        double intensity = normal.dot(lightDir);
        if (intensity < 0) {
            return new Vec3(0.0, 0.0, 0.0);
        } else {
            return light.color().scale(intensity);
        }
    }

    private Vec3 getSpecular(Vec3 origin, Vec3 point, Vec3 normal, LightSource light, double shine) {
        Vec3 viewDir = origin.subtract(point).normalize();
        Vec3 incidentDir = light.position().subtract(point).normalize();

        Vec3 r = normal.scale(2 * normal.dot(incidentDir)).subtract(incidentDir);

        double specular = Math.pow(Math.max(0, r.dot(viewDir)), shine);
        return light.color().scale(specular);
    }

    private boolean hasPathToLight(Vec3 point, LightSource light, Set<Model> models, Model ignore) {
        Vec3 path = light.position().subtract(point);
        Ray ray = new Ray(point, path.normalize());

        Optional<Intersection> closest = getClosest(ray, models, ignore);
        if (closest.isPresent()) {
            return closest.get().t() > path.magnitude();
        } else {
            return true;
        }
    }

    private Ray getRay(Mat4 transform, double fov, double x, double y) {
        double pixelX = (2 * (x / width) - 1) * Math.tan(fov / 2 * Math.PI / 180) * aspectRatio;
        double pixelY = (1 - 2 * (y / height)) * Math.tan(fov/ 2 * Math.PI / 180);
        Vec4 pixel = new Vec4(pixelX, pixelY, -1, 1);
        Vec4 origin = new Vec4(0, 0, 0, 1);

        Vec3 pixelWorld = new Vec3(transform.multiply(pixel));
        Vec3 originWorld = new Vec3(transform.multiply(origin));

        Vec3 rayDirection = pixelWorld.subtract(originWorld).normalize();
        return new Ray(originWorld, rayDirection);
    }

    class ProgressTracker {

        private final int total;
        private final int percentInterval;
        private int complete;
        private int lastInterval;

        public ProgressTracker(int total, int percentInterval) {
            this.total = total;
            this.percentInterval = percentInterval;
            this.complete = 0;
            this.lastInterval = 0;
        }

        public synchronized Optional<Integer> incrementComplete() {
            complete++;
            return checkProgress();
        }

        private Optional<Integer> checkProgress() {
            int percentComplete = (int) (100 * (double) complete / total);
            int nextInterval = lastInterval + percentInterval;
            if (percentComplete > nextInterval) {
                lastInterval = nextInterval;
                return Optional.of(nextInterval);
            }
            return Optional.empty();
        }
    }

}
