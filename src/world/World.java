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

    private static final String DECIMAL_REGEX = "(\\d+\\.\\d+)";
    private static final String SEP_REGEX = "\\s*,\\s*";
    private static final String VEC_REGEX = "\\[\\s*" + DECIMAL_REGEX + SEP_REGEX + DECIMAL_REGEX + SEP_REGEX + DECIMAL_REGEX + "\\s*]";

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

        String line = nextLine(reader);
        if (!line.equals("WORLD")) {
            throw new Exception("FILE_ERROR: Expected keyword WORLD, got \n" + line);
        }
        do {
            line = nextLine(reader);
            if(line.equals("MODEL")) {
                models.add(extractModel(reader));
            } else if (line.equals("LIGHT")) {
                lights.add(extractLight(reader));
            }
        } while(!line.equals("END_WORLD"));

        return new World(models, lights);
    }

    private static Model extractModel(BufferedReader reader) throws Exception {
        String line = nextLine(reader);
        Set<Face> faces = new HashSet<Face>();
        do {
            if (!line.equals("FACE")) {
                throw new Exception("FILE_ERROR: Expected keyword FACE, got \n" + line);
            }
            faces.add(extractFace(reader));
            line = nextLine(reader);
        } while (!line.equals("END_MODEL"));
        return new Model(faces);
    }

    private static Face extractFace(BufferedReader reader) throws Exception {
        Vertex v1 = extractVertex(reader);
        Vertex v2 = extractVertex(reader);
        Vertex v3 = extractVertex(reader);
        return new Face(v1, v2, v3);
    }

    private static Vertex extractVertex(BufferedReader reader) throws Exception {
        String line = nextLine(reader);

        Pattern pattern = Pattern.compile(VEC_REGEX + SEP_REGEX + VEC_REGEX + SEP_REGEX + VEC_REGEX + SEP_REGEX +
                DECIMAL_REGEX + SEP_REGEX + DECIMAL_REGEX + ";");
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

    private static LightSource extractLight(BufferedReader reader) throws Exception {
        String line = nextLine(reader);

        Pattern pattern = Pattern.compile(VEC_REGEX + SEP_REGEX + VEC_REGEX + SEP_REGEX + DECIMAL_REGEX + ";");
        Matcher matcher = pattern.matcher(line);

        if(!matcher.matches()) {
            throw new Exception("FILE_ERROR: Expected keyword LIGHT, got \n" + line);
        }

        line = nextLine(reader);
        if(!line.equals("END_LIGHT")) {
            throw new Exception("FILE_ERROR: Expected keyword END_LIGHT, got \n" + line);
        }

        Vec3 position = new Vec3(getValue(matcher, 1), getValue(matcher, 2), getValue(matcher, 3));
        Vec3 color = new Vec3(getValue(matcher, 4), getValue(matcher, 5), getValue(matcher, 6));
        double intensity = getValue(matcher, 7);

        return new LightSource(position, color, intensity);
    }

    private static String nextLine(BufferedReader reader) {
        String line = "";
        try {
            line = reader.readLine();
        } catch(Exception ignored){}
        return line.replaceAll("#.*", "").trim();
    }

    private static double getValue(Matcher matcher, int group) {
        return Double.parseDouble(matcher.group(group));
    }
}
