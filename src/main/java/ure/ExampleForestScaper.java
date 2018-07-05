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
        simplexScatterTerrain(area, "grass", new String[]{"tree"}, 0.2f + randf(0.4f), randf(0.2f), new float[]{5f,10f,18f});
        for (int i=0;i<rand(8);i++) {
            digRiver(area, "dirt", 0, 0, area.xsize - 1, area.ysize - 1, 1f + randf(2f), 1.3f, 1.3f);
        }
        for (int i=0;i<rand(3);i++) {
            digRiver(area, "water", 0,0,area.xsize-1,area.ysize-1, 2f + randf(5f), 1.5f, 2f);
        }
        for (int i=0;i<rand(4);i++) {
            System.out.println("digging lake");
            int lakew = 7 + rand(25);
            int lakeh = 7 + rand(25);
            UCell lakeloc = findAreaWithout(area, 1, 1, area.xsize - lakew - 2, area.ysize - lakeh - 2, lakew, lakeh, new String[]{"dirt"});
            if (lakeloc != null) {
                digCaves(area, "water", lakeloc.x, lakeloc.y, lakeloc.x + lakew, lakeloc.y + lakeh, 0.49f, 4, 5, 5);
            }
        }
        for (int i=0;i<rand(7)+3;i++) {
            System.out.println("building rock formation");
            int x1 = 10 + rand(area.xsize - 20);
            int y1 = 10 + rand(area.ysize - 20);
            int w = 4+rand(12);
            int h = 4+rand(12);
            digCaves(area, "wall", x1, y1, x1+w,y1+h,0.35f + randf(0.1f), 4+rand(3), 5, 4+rand(3));
            if (randf() < 0.6f) {
                System.out.println("digging cave entrance");
                UCell doorcell = findAnextToB(area, "wall", "grass", x1, y1, x1 + w, y1 + h);
                if (doorcell != null) {
                    area.setTerrain(doorcell.x, doorcell.y, "cave entrance");
                }
            }
        }
    }
}
