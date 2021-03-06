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
                Vec3 pij = xyz(radius, phiI, thetaJ);
                Vec3 nij = pij.subtract(position).normalize();
                Vertex vij = new Vertex(pij, nij, color, opacity, reflectance);
                // Vertex and normal (i+1,j)
                Vec3 pip1j = xyz(radius, phiIP1, thetaJ);
                Vec3 nip1j = pip1j.subtract(position).normalize();
                Vertex vip1j = new Vertex(pip1j, nip1j, color, opacity, reflectance);
                // Vertex and normal (i, j+1)
                Vec3 pijp1 = xyz(radius, phiI, thetaJP1);
                Vec3 nijp1 = pijp1.subtract(position).normalize();
                Vertex vijp1 = new Vertex(pijp1, nijp1, color, opacity, reflectance);
                // Vertex and normal (i+1, j+1)
                Vec3 pip1jp1 = xyz(radius, phiIP1, thetaJP1);
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
}
