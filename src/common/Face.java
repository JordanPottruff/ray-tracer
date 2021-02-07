package common;

public class Face {

    private final Vertex v1;
    private final Vertex v2;
    private final Vertex v3;

    public Face(Vertex v1, Vertex v2, Vertex v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public Vertex v1() {
        return this.v1;
    }

    public Vertex v2() {
        return this.v2;
    }

    public Vertex v3() {
        return this.v3;
    }
}
