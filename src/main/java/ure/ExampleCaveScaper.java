package ure;

import ure.terrain.URETerrainCzar;
import ure.things.UREThingCzar;

public class ExampleCaveScaper extends URELandscaper {

    public ExampleCaveScaper(URETerrainCzar theTerrainCzar, UREThingCzar theThingCzar) {
        super(theTerrainCzar, theThingCzar);
    }

    @Override
    public void buildArea(UREArea area) {
        buildCaves(area, "floor", 0,0,area.xsize-4, area.ysize-4);
    }

    public void buildCaves(UREArea area, String floorTerrain,
                           int x1, int x2, int y1, int y2) {

        digCaves(area, floorTerrain, x1, x2, y1, y2,
                0.38f + randf(0.14f),
                4 + rand(3), 3 + rand(3),
                2 + rand(3));

        if (randf() < 0.7) {
            int rivers = rand(3) + 1;
            if (randf() < 0.1) rivers = rivers + rand(4);
            digRiver(area, "water", x1, x2, y1, y2,
                    2f + randf(4f),
                    0.4f + randf(2f),
                    0.6f + randf(2.5f));
        }

        addDoors(area, "door", new String[]{"wall"},
                0.1f + randf(1.5f));

        int statues = rand(6);
        for (int i = 0;i < statues;i++) {
            int width = rand(5) + 2;
            int height = rand(5) + 2;
            int[] boxloc = locateBox(area, width, height, new String[]{floorTerrain});
            if (boxloc != null) {
                drawRect(area, "carvings", boxloc[0], boxloc[1], boxloc[0] + width, boxloc[1] + height);
                if (randf() < 0.7f) {
                    spawnThingAt(area, boxloc[0] + (width / 2), boxloc[1] + (height / 2), "gold statue");
                    spawnLightAt(area, boxloc[0] + (width / 2), boxloc[1] + (height / 2),
                            new UColor(1f, 1f, 0.7f), (width + height) / 2, 15);
                }
            }
        }

        simplexScatterThings(area, "skull", new String[]{floorTerrain},
                0.6f, 0.15f + randf(0.3f));

        simplexScatterTerrain(area, "floormoss", new String[]{floorTerrain},
                randf(0.3f), randf(0.6f));

        scatterThings(area, new String[]{"trucker hat", "butcher knife", "apple", "rock", "rock"},
                new String[]{floorTerrain}, 10 + rand(40));

    }
}
