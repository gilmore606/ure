package ure.examplegame;

import ure.areas.*;
import ure.terrain.Stairs;
import ure.terrain.UTerrainCzar;
import ure.things.UThingCzar;

public class ExampleForestScaper extends ULandscaper {

    @Override
    public void buildArea(UArea area, int level, String[] tags) {
        fillRect(area, "grass", 0,0,area.xsize-1,area.ysize-1);
        simplexScatterTerrain(area, "tree", new String[]{"grass"}, 0.4f, 0.7f, new float[]{2f,7f,13f});
        digCaves(area, "grass", 0,0,area.xsize-1,area.ysize-1, 0.41f, 5, 3, 1);
        simplexScatterTerrain(area, "sapling", new String[]{"grass"}, 0.35f + randf(0.3f), 0.04f + randf(0.1f), new float[]{4f,6f,12f});
        simplexScatterTerrain(area, "grass", new String[]{"tree"}, 0.1f + randf(0.4f), randf(0.25f), new float[]{5f,10f,38f});
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
            scatterActorsByTags(area, lakeloc.x-1,lakeloc.y-1,lakeloc.x+lakew,lakeloc.y+lakeh, new String[]{"forest"}, 4, 3);
        }
        for (int i=0;i<rand(7)+5;i++) {
            System.out.println("building rock formation");
            int x1 = 10 + rand(area.xsize - 20);
            int y1 = 10 + rand(area.ysize - 20);
            int w = 7+rand(9);
            int h = 7+rand(9);
            digCaves(area, "rock", x1, y1, x1+w,y1+h,0.38f + randf(0.1f), 4+rand(3), 5, 3+rand(3));
            System.out.println("built rock formation!");
            if (randf() < 0.9f) {
                System.out.println("digging cave entrance");
                UCell doorcell = findAnextToB(area, "rock", "grass", x1, y1, x1 + w, y1 + h);
                if (doorcell != null) {
                    String[] names = new String[]{"Caverns of pain", "Terror cave", "Mystery cave", "Caverns of Night", "Horror cave"};
                    String name = names[rand(names.length)];
                    String id = "cavern-" + Integer.toString(doorcell.x*1000+doorcell.y);
                    URegion region = new URegion(id, name,
                            new ULandscaper[]{new ExampleCaveScaper()}, new String[]{"cave"}, 50, 50,
                            rand(5)+3, "cave entrance", "cave exit");
                    linkRegionAt(area, doorcell.x, doorcell.y, "cave entrance", region, 1, "cave exit");
                }
            }
            scatterActorsByTags(area, x1-3, y1-3,x1+w+3, y1+h+3, new String[]{"rock"}, 1, 1+rand(4));
            scatterThingsByTags(area, x1-3, y1-3,x1+w+3, y1+h+3, new String[]{"rock"}, 1, 1+rand(5));
        }
        for (int i=0;i<rand(4);i++) {
            int ruinw = 20+rand(20); int ruinh = 20+rand(20);
            UCell ruinloc = findAreaWithout(area, 1, 1, area.xsize - ruinw, area.ysize - ruinh, ruinw-1, ruinh-1, new String[]{"wall"});
            if (ruinloc != null) {
                buildComplex(area, ruinloc.x, ruinloc.y, ruinloc.x + ruinw, ruinloc.y + ruinh, "floor", "wall",
                        new String[]{"tree", "sapling", "grass", "water"}, 5, 9+rand(5),
                        0.3f, 3, 4+rand(30), 8+rand(6));
                thinTrees(area, ruinloc.x - 1, ruinloc.y - 1, ruinloc.x + ruinw + 2, ruinloc.y + ruinh + 2);
                System.out.println("digging trapdoor to dungeon");
                UCell doorcell = randomCell(area, "floor", ruinloc.x,ruinloc.y,ruinloc.x+ruinw,ruinloc.y+ruinh);
                if (doorcell != null) {
                    String id = "dungeon-" + Integer.toString(doorcell.x*1000+doorcell.y);
                    URegion region = new URegion(id, "Basement", new ULandscaper[]{new ExampleDungeonScaper()},
                            new String[]{"basement"}, 60, 60, rand(4)+2, "trapdoor", "ladder");
                    linkRegionAt(area, doorcell.x, doorcell.y, "trapdoor", region, 1, "ladder");
                }
                scatterActorsByTags(area, ruinloc.x,ruinloc.y,ruinloc.x+ruinw,ruinloc.y+ruinh,
                        new String[]{"complex"}, 1, 2 + random.nextInt(8));
                scatterThingsByTags(area, ruinloc.x,ruinloc.y,ruinloc.x+ruinw, ruinloc.y+ruinh,
                        new String[]{"complex"}, 1, 3 + rand(12));
            }
        }
        //UCell townloc = findAreaWithout(area, 1,1,area.xsize, area.ysize, 20, 20, new String[]{"water", "wall"});

        scatterThingsByTags(area, 0, 0, area.xsize-1, area.ysize-1, new String[]{"forest"}, 1, 60);
        scatterActorsByTags(area, 0, 0, area.xsize-1, area.ysize-1, new String[]{"forest"}, 1, 40);
    }

    public void thinTrees(UArea area, int x1, int y1, int x2, int y2) {
        for (int x=x1;x<=x2;x++) {
            for (int y=y1;y<=y2;y++) {
                if (area.hasTerrainAt(x,y,"tree")) {
                    area.setTerrain(x,y,"sapling");
                }
            }
        }
    }

}
