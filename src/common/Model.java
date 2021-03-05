package common;

import java.util.HashSet;
import java.util.Set;

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
}
