import com.github.jordanpottruff.jgml.Mat4;
import com.github.jordanpottruff.jgml.Vec2;
import com.github.jordanpottruff.jgml.Vec3;
import com.github.jordanpottruff.jgml.VecN;
import common.LightSource;
import common.Model;
import renderer.Renderer;
import tracer.Sampler;
import tracer.Tracer;
import world.World;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        //printWorld("assets\\pyramid.txt");
        //createImage("out/images/blank.png");
        //testSampler("out/images/sampler.png");
        //testTracer("assets\\pyramid.txt", "out\\images\\test.png");
        //traceSphere("out\\images\\sphere.png");
        traceCube("out\\images\\cube.png");
        //traceShadows("out\\images\\shadows.png");
    }

    public static void printWorld(String filename) throws Exception {
        World world = World.createFromFile(filename);

        List<Model> models = new ArrayList<>(world.models());
        List<LightSource> lights = new ArrayList<>(world.lights());

        for(Model model: models) {
            System.out.println(model);
        }
        for(LightSource light: lights) {
            System.out.println(light);
        }
    }

    public static void createImage(String filename) {
        int width = 1000;
        int height = 500;
        Renderer renderer = new Renderer(width, height);
        for(int x=0; x<width; x++) {
            int y = x/2;
            renderer.setColor(x, y, new Vec3(1.0, 0.0, 0.0));
        }
        renderer.savePNG(filename);
    }

    public static void testSampler(String imageFilename) {
        Sampler sampler = new Sampler();
        List<Vec2> samples = sampler.jitter(0, 1000, 0, 1000, 25);
        Renderer renderer = new Renderer(1000, 1000);

        for(Vec2 coord: samples) {
            int x = (int) coord.x();
            int y = (int) coord.y();
            renderer.setColor(x, y, new Vec3(1.0, 1.0, 1.0));
        }

        renderer.savePNG(imageFilename);
    }

    public static void testTracer(String worldFilename, String imageFilename) throws Exception {
        World world = World.createFromFile(worldFilename);
        Tracer tracer = new Tracer(world, 1920, 1080);

        Mat4 transformation = new Mat4.TransformBuilder().rotateX(-1.0708).translate(new Vec3(0.0, -3, 0.0)).build();
        Renderer r = tracer.trace(transformation, 100, 1);

        r.savePNG(imageFilename);
    }

    public static void traceSphere(String imageFilename) {
        Vec3 red = new Vec3(1.0, 0.0, 0.0);
        Vec3 green = new Vec3(0.0, 1.0, 0.0);
        double opacity = 1.0;
        double reflectance = 0.0;
        Model sphere = Model.createSphere(new Vec3(0, 0, -5), 0.5, red, opacity, reflectance, 24);
        Model moon = Model.createSphere(new Vec3(1.0, 1.0, -3.5), 0.1, green, opacity, reflectance, 24);
        LightSource light = new LightSource(new Vec3(2.0, 2, -2.0), new Vec3(1.0, 1.0, 1.0), 10);

        HashSet<Model> models = new HashSet<>();
        models.add(sphere);
        models.add(moon);
        HashSet<LightSource> lights = new HashSet<>();
        lights.add(light);
        World world = new World(models, lights);

        Tracer tracer = new Tracer(world, 3840, 2160, new Vec3(0.07, 0.07, 0.07));

        Renderer r = tracer.trace(Mat4.createIdentityMatrix(), 90, 16);

        r.savePNG(imageFilename);
    }

    public static void traceCube(String imageFilename) {
        Vec3 red = new Vec3(1.0, 0.0, 0.0);
        Vec3 grey = new Vec3(0.5, 0.5, 0.5);
        Model.VertexConfig config = new Model.VertexConfig(red);
        Model cube1 = Model.createCube(new Vec3(0, -1, -3), 1.0, config);
        Model cube2 = Model.createCube(new Vec3(-2, -1, -3), 1.0, config);
        Model cube3 = Model.createCube(new Vec3(-4, -1, -3), 1.0, config);
        Model cube4 = Model.createCube(new Vec3(2, -1, -3), 1.0, config);
        Model cube5 = Model.createCube(new Vec3(4, -1, -3), 1.0, config);
        Model base = Model.createRectPrism(-100, 100, -10, -1.5, -30, 30, new Model.VertexConfig(grey));
        LightSource light = new LightSource(new Vec3(-5.0, 5.0, -1.0), new Vec3(1.0, 1.0, 1.0), 10);

        HashSet<Model> models = new HashSet<>();
        models.add(cube1);
        models.add(cube2);
        models.add(cube3);
        models.add(cube4);
        models.add(cube5);
        models.add(base);
        HashSet<LightSource> lights = new HashSet<>();
        lights.add(light);
        World world = new World(models, lights);

        Tracer tracer = new Tracer(world, 1920, 1080, new Vec3(0.07, 0.07, 0.07));

        Mat4 transformation = new Mat4.TransformBuilder().translate(0.0, 0.0, 0.0).build();
        Renderer r = tracer.trace(transformation, 120, 1);

        r.savePNG(imageFilename);
    }
}
