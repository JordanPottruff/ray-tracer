import com.github.jordanpottruff.jgml.Vec3;
import com.github.jordanpottruff.jgml.VecN;
import common.LightSource;
import common.Model;
import renderer.Renderer;
import world.World;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        printWorld("assets\\pyramid.txt");
        createImage("out/images/blank.png");
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
}
