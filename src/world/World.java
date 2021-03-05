package world;

import com.github.jordanpottruff.jgml.Vec3;
import common.Face;
import common.LightSource;
import common.Model;
import common.Vertex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class World {

    private final Set<Model> models;
    private final Set<LightSource> lights;

    public World(Set<Model> models, Set<LightSource> lights) {
        this.models = models;
        this.lights = lights;
    }

    public Set<Model> models() {
        return new HashSet<>(this.models);
    }

    public Set<LightSource> lights() {
        return new HashSet<>(this.lights);
    }

    public static World createFromFile(String filename) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        Set<Model> models = new HashSet<>();
        Set<LightSource> lights = new HashSet<>();

        String line = reader.readLine().trim();
        if (!line.equals("WORLD")) {
            throw new Exception("FILE_ERROR: Expected keyword WORLD, got \n" + line);
        }
        do {
            line = reader.readLine().trim();
            if(line.equals("MODEL")) {
                models.add(extractModel(reader));
            } else if (line.equals("LIGHT")) {
                lights.add(extractLight(reader));
            }
        } while(!line.equals("END_WORLD"));

        return new World(models, lights);
    }

    private static Model extractModel(BufferedReader input) throws Exception {
        String line = input.readLine().trim();
        Set<Face> faces = new HashSet<Face>();
        do {
            if (!line.equals("FACE")) {
                throw new Exception("FILE_ERROR: Expected keyword FACE, got \n" + line);
            }
            Vertex v1 = extractVertex(input);
            Vertex v2 = extractVertex(input);
            Vertex v3 = extractVertex(input);
            faces.add(new Face(v1, v2, v3));
            line = input.readLine().trim();
        } while (!line.equals("END_MODEL"));
        return new Model(faces);
    }

    private static Vertex extractVertex(BufferedReader input) throws Exception {
        String line = input.readLine().trim();
        String decimal = "(\\d+\\.\\d+)";
        String sep = "\\s*,\\s*";
        String vector = "\\[\\s*" + decimal + sep + decimal + sep + decimal + "\\s*]";

        Pattern pattern = Pattern.compile(vector + sep + vector + sep + vector + sep + decimal + sep + decimal + ";");
        Matcher matcher = pattern.matcher(line);

        if(!matcher.matches()) {
            throw new Exception("FILE_ERROR: Expected a vertex, got \n" + line);
        }

        Vec3 position = new Vec3(getValue(matcher, 1), getValue(matcher, 2), getValue(matcher, 3));
        Vec3 normal = new Vec3(getValue(matcher, 4), getValue(matcher, 5), getValue(matcher, 6));
        Vec3 color = new Vec3(getValue(matcher, 7), getValue(matcher, 8), getValue(matcher, 9));
        double opacity = getValue(matcher, 10);
        double reflectance = getValue(matcher, 11);

        return new Vertex(position, normal, color, opacity, reflectance);
    }

    private static LightSource extractLight(BufferedReader input) throws Exception {
        String line = input.readLine().trim();
        String decimal = "(\\d+\\.\\d+)";
        String sep = "\\s*,\\s*";
        String vector = "\\[\\s*" + decimal + sep + decimal + sep + decimal + "\\s*]";

        Pattern pattern = Pattern.compile(vector + sep + vector + sep + decimal + ";");
        Matcher matcher = pattern.matcher(line);

        if(!matcher.matches()) {
            throw new Exception("FILE_ERROR: Expected keyword LIGHT, got \n" + line);
        }

        line = input.readLine().trim();
        if(!line.equals("END_LIGHT")) {
            throw new Exception("FILE_ERROR: Expected keyword END_LIGHT, got \n" + line);
        }

        Vec3 position = new Vec3(getValue(matcher, 1), getValue(matcher, 2), getValue(matcher, 3));
        Vec3 color = new Vec3(getValue(matcher, 4), getValue(matcher, 5), getValue(matcher, 6));
        double intensity = getValue(matcher, 7);

        return new LightSource(position, color, intensity);
    }

    private static double getValue(Matcher matcher, int group) {
        return Double.parseDouble(matcher.group(group));
    }
}
