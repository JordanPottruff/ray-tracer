import com.github.jordanpottruff.jgml.VecN;
import common.LightSource;
import common.Model;
import world.World;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        World world = World.createFromFile("assets\\example.txt");

        List<Model> models = new ArrayList<>(world.models());
        List<LightSource> lights = new ArrayList<>(world.lights());

        for(Model model: models) {
            System.out.println(model);
        }
        for(LightSource light: lights) {
            System.out.println(light);
        }
        //BufferedReader reader = new BufferedReader(new FileReader("assets\\example.txt"));

//        while(reader.readLine() != null) {
//            System.out.println("Not empty");
//        }
    }
}
