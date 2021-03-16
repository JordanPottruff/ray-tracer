package tracer;

import com.github.jordanpottruff.jgml.Vec2;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Sampler {

    public List<Vec2> jitter(double minX, double maxX, double minY, double maxY, int numSamples) {
        int size = (int) Math.sqrt(numSamples);
        double width = maxX - minX;
        double height = maxY - minY;
        double unitWidth = width/size;
        double unitHeight = height/size;

        Random random = new Random();
        List<Vec2> samples = new ArrayList<>();
        for(int gridX=0; gridX<size; gridX++) {
            for(int gridY=0; gridY<size; gridY++) {
                double x = minX + gridX * unitWidth + random.nextDouble()*unitWidth;
                double y = minY + gridY * unitHeight + random.nextDouble()*unitHeight;
                samples.add(new Vec2(x, y));
            }
        }
        return samples;
    }

    public PixelSample jitterPixel(int x, int y, int numSamples) {
        return new PixelSample(x, y, jitter(x, x+1, y, y+1, numSamples));
    }

    public static class PixelSample {
        private final int pixelX;
        private final int pixelY;
        private final List<Vec2> points;

        public PixelSample(int pixelX, int pixelY, List<Vec2> points) {
            this.pixelX = pixelX;
            this.pixelY = pixelY;
            this.points = points;
        }

        public int pixelX() {
            return this.pixelX;
        }

        public int pixelY() {
            return this.pixelY;
        }

        public List<Vec2> points() {
            return this.points;
        }
    }
}
