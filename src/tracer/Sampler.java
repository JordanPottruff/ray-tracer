package tracer;

import com.github.jordanpottruff.jgml.Vec2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Sampler {

    public List<Vec2> jitter(double minX, double maxX, double minY, double maxY, int numSamples) {
        int size = (int) Math.sqrt(numSamples);
        double unitWidth = (maxX - minX)/size;
        double unitHeight = (maxY - minY)/size;

        Random random = new Random();
        List<Vec2> samples = new ArrayList<>();
        for(int gridX=0; gridX<size; gridX++) {
            for(int gridY=0; gridY<size; gridY++) {
                double x = minX + gridX * unitWidth + random.nextDouble() * unitWidth;
                double y = minY + gridY * unitHeight + random.nextDouble() * unitHeight;
                samples.add(new Vec2(x, y));
            }
        }
        return samples;
    }

    public PixelSample jitterPixel(int x, int y, int numSamples) {
        return new PixelSample(x, y, jitter(x, x+1, y, y+1, numSamples));
    }

    public List<Vec2> multiJitter(double minX, double maxX, double minY, double maxY, int numSamples) {
        int size = (int) Math.sqrt(numSamples);
        double subWidth = (maxX - minX)/size;
        double subHeight = (maxY - minY)/size;
        double subUnitWidth = subWidth/size;
        double subUnitHeight = subHeight/size;

        Random random = new Random();
        List<List<Vec2>> subSamples = new ArrayList<>();
        for(int subX=0; subX < size; subX++) {
            List<Vec2> subSampleRow = new ArrayList<>();
            for(int subY=0; subY < size; subY++) {
                double x = subX * subUnitWidth + random.nextDouble() * subUnitWidth;
                double y = subY * subUnitHeight + random.nextDouble() * subUnitHeight;
                subSampleRow.add(new Vec2(x, y));
            }
            Collections.shuffle(subSampleRow);
            subSamples.add(subSampleRow);
        }
        Collections.shuffle(subSamples);

        List<Vec2> samples = new ArrayList<>();
        for(int superX = 0; superX < size; superX++) {
            for(int superY = 0; superY < size; superY++) {
                Vec2 subSample = subSamples.get(superY).get(superX);
                double x = minX + superX * subWidth + subSample.x();
                double y = minY + superY * subWidth + subSample.y();
                samples.add(new Vec2(x, y));
            }
        }
        return samples;
    }

    public PixelSample multiJitterPixel(int x, int y, int numSamples) {
        return new PixelSample(x, y, multiJitter(x, x+1, y, y+1, numSamples));
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
