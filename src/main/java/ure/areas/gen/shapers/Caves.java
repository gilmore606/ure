package ure.areas.gen.shapers;

import ure.areas.UArea;
import ure.areas.gen.Layer;
import ure.areas.gen.Shape;

public class Caves extends Shaper {

    public Caves(int xsize, int ysize) {
        super(xsize, ysize);
        name = "Caves";
    }

    @Override
    public void setupParams() {
        addParamF("initialDensity", 0.3f, 0.45f, 0.6f);
        addParamI("jumblePasses", 1, 5, 10);
        addParamI("jumbleDensity", 1, 2, 5);
        addParamI("smoothPasses", 1, 3, 6);
    }

    @Override
    public void build(Layer previousLayer, UArea area) {
        buildCaves(getParamF("initialDensity"),getParamI("jumblePasses"),getParamI("jumbleDensity"),getParamI("smoothPasses"));
    }

    public void buildCaves(float initialDensity, int jumblePasses, int jumbleDensity, int smoothPasses) {
        clear();
        float fillratio = -1f;
        int tries = 0;
        while ((fillratio < 0.25f) && (tries < 8)) {
            tries++;

            // Fill with initial noise, minus a horizontal gap (to promote connectedness later)
            noiseWipe(initialDensity);
            int gapY = random.i(ysize/2) + ysize/3;
            for (int x=0;x<xsize;x++) { clear(x,gapY); clear(x,gapY+1); clear(x,gapY-1); }

            jumble(5, jumbleDensity, jumblePasses);
            smooth(5, smoothPasses);

            // Check if we made enough space
            int[] point = randomCell(false);
            int spacecount = floodCount(point[0],point[1],false);
            fillratio = (float)spacecount / (float)(xsize*ysize);
        }
        invert();
    }
}
