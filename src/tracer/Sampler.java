package tracer;

import com.github.jordanpottruff.jgml.Vec2;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Sampler {

    public List<Vec2> jitter(double minX, double maxX, double minY, double maxY, int samples) {
        int size = (int) Math.sqrt(samples);
        double width = maxX - minX;
        double height = maxY - minY;
        double unitWidth = width/size;
        double unitHeight = height/size;

        Random random = new Random();
        List<Vec2> coords = new ArrayList<>();
        for(int gridX=0; gridX<size; gridX++) {
            for(int gridY=0; gridY<size; gridY++) {
                double x = minX + gridX * unitWidth + random.nextDouble()*unitWidth;
                double y = minY + gridY * unitHeight + random.nextDouble()*unitHeight;
                coords.add(new Vec2(x, y));
            }
        }
        return coords;
    }
}
