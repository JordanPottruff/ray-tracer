import com.github.jordanpottruff.jgml.Mat4;
import com.github.jordanpottruff.jgml.Vec2;
import com.github.jordanpottruff.jgml.Vec3;
import common.LightSource;
import common.Model;
import common.Texture;
import renderer.Renderer;
import tracer.Sampler;
import tracer.Tracer;
import world.World;

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
        //traceCube("out\\images\\cube.png");
        //traceShadows("out\\images\\shadows.png");
        //traceReflection("out\\images\\reflection.png");
        traceTexturedReflection("out\\images\\reflection-textured.png");
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
        List<Vec2> samples = sampler.multiJitter(0, 1000, 0, 1000, 100);
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
        Model.ModelConfig config = new Model.ModelConfig(red, opacity, reflectance, 10, 0.8, 0.1);
        Model sphere = Model.createSphere(new Vec3(0, 0, -5), 0.5, config, 24);
        //Model moon = Model.createSphere(new Vec3(1.0, 1.0, -3.5), 0.1, green, opacity, reflectance, 24);
        LightSource light = new LightSource(new Vec3(-10, 1.0, 2.0), new Vec3(1.0, 1.0, 1.0), 10);

        HashSet<Model> models = new HashSet<>();
        models.add(sphere);
        //models.add(moon);
        HashSet<LightSource> lights = new HashSet<>();
        lights.add(light);
        World world = new World(models, lights);

        Tracer tracer = new Tracer(world, 3840, 2160, new Vec3(1, 1, 1), new Vec3(0.07, 0.07, 0.07));

        Mat4 transformation = new Mat4.TransformBuilder().translateZ(-1).build();
        Renderer r = tracer.trace(transformation, 90, 1);

        r.savePNG(imageFilename);
    }

    public static void traceCube(String imageFilename) {
        Vec3 red = new Vec3(1.0, 0.0, 0.0);
        Vec3 grey = new Vec3(0.5, 0.5, 0.5);
        Model.ModelConfig config = new Model.ModelConfig(red, 1.0, 0.0, 50, 0.8, 0.1);
        Model cube1 = Model.createCube(new Vec3(0, -1, -3), 1.0, config);
        Model cube2 = Model.createCube(new Vec3(-2, -1, -3), 1.0, config);
        Model cube3 = Model.createCube(new Vec3(-4, -1, -3), 1.0, config);
        Model cube4 = Model.createCube(new Vec3(2, -1, -3), 1.0, config);
        Model cube5 = Model.createCube(new Vec3(4, -1, -3), 1.0, config);
        Model base = Model.createRectPrism(-100, 100, -10, -1.5, -30, 30, new Model.ModelConfig(grey));
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

        Tracer tracer = new Tracer(world, 1920, 1080, new Vec3(0.59, 0.75, 0.82), new Vec3(0.3, 0.3, 0.3));

        Mat4 transformation = new Mat4.TransformBuilder().translate(0.0, 0.0, 0.0).build();
        Renderer r = tracer.trace(transformation, 120, 1);

        r.savePNG(imageFilename);
    }

    public static void traceReflection(String imageFilename) {
        Vec3 white = new Vec3(0.9, 0.9, 0.9);
        Vec3 black = new Vec3(0.2, 0.2, 0.2);
        Vec3 gold = new Vec3(0.828, 0.684, 0.216);

        HashSet<Model> models = new HashSet<>();
        final int size = 8;
        final double xOffset = -size/2.0;
        final double yOffset = -1;
        final double zOffset = -size;
        for(double x=0; x<size; x++) {
            for(double z=0; z<size; z++) {
                Model.ModelConfig config;
                if ((x+z) % 2 == 0) {
                    config = new Model.ModelConfig(white, 1, .25, 10, 0.8, 0.1);
                } else {
                    config = new Model.ModelConfig(black, 1, .25, 10, 0.8, 0.1);
                }
                models.add(Model.createCube(new Vec3(x+0.5+xOffset, yOffset, z+0.5+zOffset), 1.0, config));
            }
        }
        Model.ModelConfig sphereConfig = new Model.ModelConfig(gold, 1.0, 0.15, 30, 0.8, 0.1);
        models.add(Model.createSphere(new Vec3(0, 0.5, -4), 0.5, sphereConfig, 48));

        HashSet<LightSource> lights = new HashSet<>();
        lights.add(new LightSource(new Vec3(-4, 5, 0), white.scale(1.0), 10));
        World world = new World(models, lights);

        Tracer tracer = new Tracer(world, 1920, 1080, new Vec3(0.59, 0.75, 0.82), new Vec3(1, 1, 1));

        Mat4 transformation = new Mat4.TransformBuilder().translateY(2.0).rotateX(-Math.PI/5).build();
        Renderer r = tracer.trace(transformation, 90, 25);

        r.savePNG(imageFilename);
    }

    public static void traceTexturedReflection(String imageFilename) {
        Vec3 white = new Vec3(0.9, 0.9, 0.9);
        Vec3 black = new Vec3(0.2, 0.2, 0.2);
        Vec3 gold = new Vec3(0.828, 0.684, 0.216);
        Vec3 silver = new Vec3(0.769, 0.792, 0.808);
        Vec3 graphite = new Vec3(0.255, 0.255, 0.255);

        HashSet<Model> models = new HashSet<>();
        Texture texture = new Texture("assets\\textures\\marble-2.jpg");
        Model.ModelConfig boardConfig = new Model.ModelConfig(white, 1, .1, 100, 0.6, 0.2, texture, 1.0);
        Model board = Model.createCube(new Vec3(0, -4.5, -4), 8.0, boardConfig);
        models.add(board);

        Model.ModelConfig sphere1Config = new Model.ModelConfig(gold, 1.0, 0.15, 30, 0.8, 0.1);
        models.add(Model.createSphere(new Vec3(0, 0.5, -4), 0.5, sphere1Config, 20));

        Model.ModelConfig sphere2Config = new Model.ModelConfig(silver, 1.0, 0.15, 30, 0.8, 0.1);
        models.add(Model.createSphere(new Vec3(1.5, 0.3, -2.5), 0.4, sphere2Config, 20));

        Model.ModelConfig sphere3Config = new Model.ModelConfig(graphite, 1.0, 0.15, 30, 0.8, 0.1);
        models.add(Model.createSphere(new Vec3(-2.0, 0.4, -2.5), 0.3, sphere3Config, 20));

        HashSet<LightSource> lights = new HashSet<>();
        lights.add(new LightSource(new Vec3(-4, 5, 0), white.scale(1.5), 10));
        //lights.add(new LightSource(new Vec3(-4, 5, -8), white.scale(1.0), 10));
        World world = new World(models, lights);

        Tracer tracer = new Tracer(world, 1920, 1080, new Vec3(0.59, 0.75, 0.82), new Vec3(1, 1, 1));

        Mat4 transformation = new Mat4.TransformBuilder().translateY(2.0).rotateX(-Math.PI/5).build();
        Renderer r = tracer.trace(transformation, 90, 4);

        r.savePNG(imageFilename);
    }
}
