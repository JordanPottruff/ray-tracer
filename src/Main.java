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
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        //printWorld("assets\\pyramid.txt");
        //createImage("out/images/blank.png");
        testTracer("assets\\pyramid.txt", "out\\images\\test.png");
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
}
