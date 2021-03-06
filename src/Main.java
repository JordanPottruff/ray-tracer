import com.github.jordanpottruff.jgml.Mat4;
import com.github.jordanpottruff.jgml.Vec3;
import com.github.jordanpottruff.jgml.VecN;
import common.LightSource;
import common.Model;
import renderer.Renderer;
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
        //testTracer("assets\\pyramid.txt", "out\\images\\test.png");
        traceSphere("out\\images\\sphere.png");
        //traceCube("out\\images\\cube.png");
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

    public static void testTracer(String worldFilename, String imageFilename) throws Exception {
        World world = World.createFromFile(worldFilename);
        Tracer tracer = new Tracer(world, 1920, 1080);

        Mat4 transformation = new Mat4.TransformBuilder().rotateX(-1.0708).translate(new Vec3(0.0, -3, 0.0)).build();
        Renderer r = tracer.trace(transformation, 100);

        r.savePNG(imageFilename);
    }

    public static void traceSphere(String imageFilename) {
        Vec3 color = new Vec3(1.0, 0.0, 0.0);
        double opacity = 1.0;
        double reflectance = 0.0;
        Model sphere = Model.createSphere(new Vec3(0, 0, 0), 0.5, color, opacity, reflectance, 35);
        LightSource light = new LightSource(new Vec3(10.0, 10.0, -7.0), new Vec3(1.0, 1.0, 1.0), 10);

        HashSet<Model> models = new HashSet<>();
        models.add(sphere);
        HashSet<LightSource> lights = new HashSet<>();
        lights.add(light);
        World world = new World(models, lights);

        Tracer tracer = new Tracer(world, 1920, 1080, new Vec3(0.07, 0.07, 0.07));

        Mat4 transformation = new Mat4.TransformBuilder().translate(0.0, 0.0, -2.0).build();
        Renderer r = tracer.trace(transformation, 100);

        r.savePNG(imageFilename);
    }

    public static void traceCube(String imageFilename) {
        Vec3 color = new Vec3(1.0, 0.0, 0.0);
        Model.VertexConfig config = new Model.VertexConfig(color);
        Model cube1 = Model.createCube(new Vec3(0, 0, 3), 1.0, config);
        Model cube2 = Model.createCube(new Vec3(-2, 0, 3), 1.0, config);
        Model cube3 = Model.createCube(new Vec3(-4, 0, 3), 1.0, config);
        Model cube4 = Model.createCube(new Vec3(2, 0, 3), 1.0, config);
        Model cube5 = Model.createCube(new Vec3(4, 0, 3), 1.0, config);
        LightSource light = new LightSource(new Vec3(10.0, 10.0, -10.0), new Vec3(1.0, 1.0, 1.0), 10);

        HashSet<Model> models = new HashSet<>();
        models.add(cube1);
        models.add(cube2);
        models.add(cube3);
        models.add(cube4);
        models.add(cube5);
        HashSet<LightSource> lights = new HashSet<>();
        lights.add(light);
        World world = new World(models, lights);

        Tracer tracer = new Tracer(world, 1920, 1080, new Vec3(0.37, 0.37, 0.37));

        Mat4 transformation = new Mat4.TransformBuilder().translate(0.0, 0.0, 0.0).build();
        Renderer r = tracer.trace(transformation, 120);

        r.savePNG(imageFilename);
    }
}
