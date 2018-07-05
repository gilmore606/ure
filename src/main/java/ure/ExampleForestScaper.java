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
        for (int i=0;i<rand(8);i++) {
            digRiver(area, "dirt", 0, 0, area.xsize - 1, area.ysize - 1, 1f + randf(2f), 1.5f, 2f);
        }
        for (int i=0;i<rand(7)+3;i++) {
            int x1 = 10 + rand(area.xsize - 20);
            int y1 = 10 + rand(area.ysize - 20);
            int w = 4+rand(12);
            int h = 4+rand(12);
            digCaves(area, "wall", x1, y1, x1+w,y1+h,0.35f, 6, 5, 5);
            if (randf() < 0.6f) {
                UCell doorcell = findAnextToB(area, "wall", "grass", x1, y1, x1 + w, y1 + h);
                if (doorcell != null) {
                    area.setTerrain(doorcell.x, doorcell.y, "cave entrance");
                }
            }
        }
    }
}
