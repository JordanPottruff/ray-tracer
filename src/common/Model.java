package common;

import com.github.jordanpottruff.jgml.Mat4;
import com.github.jordanpottruff.jgml.Vec3;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Model {

    private final Set<Face> faces;

    public Model(Set<Face> faces) {
        this.faces = faces;
    }

    public Set<Face> faces() {
        return new HashSet<>(this.faces);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Model\n");
        for (Face face: faces) {
            sb.append(face.toString(2)).append("\n");
        }
        sb.delete(sb.length()-1, sb.length());
        return sb.toString();
    }

    public Model transform(Mat4 transformation) {
        return new Model(faces.stream().map((Face face) -> new Face(
                face.v1().transform(transformation),
                face.v2().transform(transformation),
                face.v3().transform(transformation)
        )).collect(Collectors.toSet()));
    }

    public Model translate(double x, double y, double z) {
        return transform(new Mat4.TransformBuilder().translate(x, y, z).build());
    }

    public static Model createSphere(Vec3 position,  double radius, Vec3 color, double opacity, double reflectance, int n) {
        int nSteps = (n%2==0) ? n : n+1; // n must be an even number.
        double stepSize = 2 * Math.PI / nSteps;

        Set<Face> faces = new HashSet<>();
        for(int i = 0; i < nSteps; i++) {
            for (int j = 0; j < nSteps/2.0; j++) {
                double phiI = i * stepSize;
                double phiIP1 = ((i + 1) % nSteps) * stepSize;
                double thetaJ = j * stepSize;
                double thetaJP1 = ((j + 1) % nSteps) * stepSize;

                // Vertex and normal (i,j)
                Vec3 pij = xyz(radius, phiI, thetaJ).add(position);
                Vec3 nij = pij.subtract(position).normalize();
                Vertex vij = new Vertex(pij, nij, color, opacity, reflectance);
                // Vertex and normal (i+1,j)
                Vec3 pip1j = xyz(radius, phiIP1, thetaJ).add(position);
                Vec3 nip1j = pip1j.subtract(position).normalize();
                Vertex vip1j = new Vertex(pip1j, nip1j, color, opacity, reflectance);
                // Vertex and normal (i, j+1)
                Vec3 pijp1 = xyz(radius, phiI, thetaJP1).add(position);
                Vec3 nijp1 = pijp1.subtract(position).normalize();
                Vertex vijp1 = new Vertex(pijp1, nijp1, color, opacity, reflectance);
                // Vertex and normal (i+1, j+1)
                Vec3 pip1jp1 = xyz(radius, phiIP1, thetaJP1).add(position);
                Vec3 nip1jp1 = pip1jp1.subtract(position).normalize();
                Vertex vip1jp1 = new Vertex(pip1jp1, nip1jp1, color, opacity, reflectance);

                faces.add(new Face(vij, vip1j, vijp1));
                faces.add(new Face(vijp1, vip1jp1, vip1j));
            }
        }
        return new Model(faces);
    }

    private static Vec3 xyz(double r, double phi, double theta) {
        double x = r*Math.cos(theta)*Math.sin(phi);
        double y = r*Math.sin(theta)*Math.sin(phi);
        double z = r*Math.cos(phi);
        return new Vec3(x, y, z);
    }

    public static Model createCube(Vec3 position, double width, VertexConfig config) {
        double half = width / 2;
        double x = position.x();
        double y = position.y();
        double z = position.z();
        return createRectPrism(x-half, x+half, y-half, y+half, z-half, z+half, config);
    }

    public static Model createRectPrism(double x1, double x2, double y1, double y2, double z1, double z2, VertexConfig config) {
        double minX = Math.min(x1, x2);
        double maxX = Math.max(x1, x2);
        double minY = Math.min(y1, y2);
        double maxY = Math.max(y1, y2);
        double minZ = Math.min(z1, z2);
        double maxZ = Math.max(z1, z2);
        Set<Face> faces = new HashSet<>();
        faces.addAll(createRectX(minX, false, minY, maxY, minZ, maxZ, config));
        faces.addAll(createRectX(maxX, true, minY, maxY, minZ, maxZ, config));
        faces.addAll(createRectY(minY, false, minX, maxX, minZ, maxZ, config));
        faces.addAll(createRectY(maxY, true, minX, maxX, minZ, maxZ, config));
        faces.addAll(createRectZ(minZ, false, minX, maxX, minY, maxY, config));
        faces.addAll(createRectZ(maxZ, true, minX, maxX, minY, maxY, config));
        return new Model(faces);
    }

    public static Set<Face> createRectX(double x, boolean posNormal, double y1, double y2, double z1, double z2, VertexConfig config) {
        Vec3 normDir = new Vec3(posNormal ? 1.0 : -1.0, 0, 0);
        Vertex v1 = config.create(new Vec3(x, y1, z1), normDir);
        Vertex v2 = config.create(new Vec3(x, y1, z2), normDir);
        Vertex v3 = config.create(new Vec3(x, y2, z2), normDir);
        Vertex v4 = config.create(new Vec3(x, y2, z1), normDir);

        Set<Face> faces = new HashSet<>();
        faces.add(new Face(v1, v2, v3));
        faces.add(new Face(v3, v4, v1));
        return faces;
    }

    public static Set<Face> createRectY(double y, boolean posNormal, double x1, double x2, double z1, double z2, VertexConfig config) {
        Vec3 normDir = new Vec3(0, posNormal ? 1.0 : -1.0, 0);
        Vertex v1 = config.create(new Vec3(x1, y, z1), normDir);
        Vertex v2 = config.create(new Vec3(x1, y, z2), normDir);
        Vertex v3 = config.create(new Vec3(x2, y, z2), normDir);
        Vertex v4 = config.create(new Vec3(x2, y, z1), normDir);

        Set<Face> faces = new HashSet<>();
        faces.add(new Face(v1, v2, v3));
        faces.add(new Face(v3, v4, v1));
        return faces;
    }

    public static Set<Face> createRectZ(double z, boolean posNormal, double x1, double x2, double y1, double y2, VertexConfig config) {
        Vec3 normDir = new Vec3(0, 0, posNormal ? 1.0 : -1.0);
        Vertex v1 = config.create(new Vec3(x1, y1, z), normDir);
        Vertex v2 = config.create(new Vec3(x1, y2, z), normDir);
        Vertex v3 = config.create(new Vec3(x2, y2, z), normDir);
        Vertex v4 = config.create(new Vec3(x2, y1, z), normDir);

        Set<Face> faces = new HashSet<>();
        faces.add(new Face(v1, v2, v3));
        faces.add(new Face(v3, v4, v1));
        return faces;
    }

    public static class VertexConfig {
        private final Vec3 color;
        private final double opacity;
        private final double reflectance;

        public VertexConfig(Vec3 color) {
            this(color, 1.0, 0.0);
        }

        public VertexConfig(Vec3 color, double opacity, double reflectance) {
            this.color = color;
            this.opacity = opacity;
            this.reflectance = reflectance;
        }

        public Vec3 color() {
            return color;
        }

        public double opacity() {
            return opacity;
        }

        public double reflectance() {
            return reflectance;
        }

        public Vertex create(Vec3 position, Vec3 normal) {
            return new Vertex(position, normal, color, opacity, reflectance);
        }
    }
}
