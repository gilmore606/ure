package ure;

import ure.terrain.URETerrainCzar;
import ure.things.UREThingCzar;

public class ExampleForestScaper extends URELandscaper {

    public ExampleForestScaper(URETerrainCzar theTerrainCzar, UREThingCzar theThingCzar) {
        super(theTerrainCzar, theThingCzar);
    }

    @Override
    public void buildArea(UREArea area) {
        fillRect(area, "grass", 0,0,area.xsize-1,area.ysize-1);
        simplexScatterTerrain(area, "tree", new String[]{"grass"}, 0.4f, 0.7f, new float[]{2f,7f,13f});
        digCaves(area, "grass", 0,0,area.xsize-1,area.ysize-1, 0.41f, 5, 3, 1);
        simplexScatterTerrain(area, "tree", new String[]{"grass"}, 0.3f, 0.1f, new float[]{4f,6f,12f});
        digRiver(area, "dirt", 0,0,area.xsize-1,area.ysize-1, 2f, 2f, 2f);
    }
}
