package ure.examplegame;

import ure.areas.*;

import java.util.ArrayList;

public class ExampleForestScaper extends ULandscaper {

    public static final String TYPE = "forestscaper";

    public ExampleForestScaper() {
        super(TYPE);
    }

    @Override
    public void buildArea(UArea area, int level, String[] tags) {

        fillRect(area, "tree", 0, 0, area.xsize-1, area.ysize-1);

        // Dig a network of big grass spaces.
        Shape grass = shapeCaves(area.xsize-4, area.ysize-4, 0.38f, 4, 2, 3);
        grass.grow(1).writeTerrain(area, "grass", 1, 1);

        // Add some holes in the tree clumps, some random lone trees and saplings, and a fringe of saplings
        grass.copy().invert().edges().writeTerrain(area, "sapling", 1 ,1, 0.5f);
        grass.writeTerrain(area, "tree", 1 ,1, 0.03f);
        grass.copy().edgesThick().writeTerrain(area, "sapling", 1, 1, 0.6f);
        grass.writeTerrain(area, "sapling", 1, 1, 0.05f);

        // Lakes
        int lakes = rand(10)+4;
        Shape lakemask = new Shape(area.xsize+4, area.ysize+4);
        for (int i=0;i<lakes;i++) {
            int lakew = 7+rand(25); int lakeh = 7+rand(25);
            Shape lake = shapeBlob(lakew,lakeh);
            int x = rand(area.xsize);
            int y = rand(area.ysize);
            lake.writeTerrain(area, "water", x, y);
            lakemask.maskWith(lake, Shape.MASK_OR, x, y);
            if (randf() < 0.5f) {
                Shape mud = lake.copy().grow(1 + rand(4)).erode(0.6f, 1 + rand(3)).maskWith(lake, Shape.MASK_NOT);
                mud.writeTerrain(area, "mud", x, y, 0.9f);
            }
            lake.shrink(rand(3)).smooth(7,1+rand(3)).writeTerrain(area, "deep water", x, y);
            scatterActorsByTags(area, x-1,y-1,x+lakew,y+lakeh, new String[]{"forest"}, 4, 2);
        }

        // Run some roads through the map.  Save them all in one mask for later.
        int roads = rand(2)+2;
        Shape roadmask = new Shape(area.xsize+4,area.ysize+4);
        for (int i=0;i<roads;i++) {
            Shape road = shapeRoad(area.xsize +4, area.ysize +4, 2, 1.2f, 1.3f);
            roadmask.maskWith(road, Shape.MASK_OR);
            road.grow(1).edges().writeTerrain(area, "grass", -2, -2, 0.7f);
            road.sparsen(22, 36).writeThings(area, "lamppost", -2, -2);
        }

        // Run some rivers through the map.  Save them all in one mask for later.
        int rivers = rand(3)+1;
        Shape rivermask = new Shape(area.xsize+4, area.ysize+4);
        for (int i=0;i<rivers;i++) {
            Shape river = shapeRoad(area.xsize+4, area.ysize+4, 5, 1.4f, 1.6f);
            river.writeTerrain(area, "water", -2, -2);
            rivermask.maskWith(river, Shape.MASK_OR);
            river.shrink(2).writeTerrain(area, "deep water", -2, -2, 0.8f);
        }

        // Add banks on the edges of rivers.
        Shape banks = rivermask.copy().grow(1).edges().maskWith(lakemask, Shape.MASK_NOT);
        banks.writeTerrain(area, "sand", -2, -2);
        banks.writeTerrain(area, "grass", -2, -2, 0.1f);

        // Rock formations and caves
        int caves = rand(3)+3;
        int rocks = rand(15)+5;
        for (int i=0;i<rocks;i++) {
            Shape rock = shapeOddBlob(10+rand(25),10+rand(25),3, 0.3f);
            int x = rand(area.xsize - 10) + 5;
            int y = rand(area.ysize - 10) + 5;
            rock.writeTerrain(area, "rock", x, y, 1f);
            if (i < caves) {
                UCell caveCell = findAnextToB(area, "rock", "grass", x,y,x+rock.xsize,y+rock.ysize);
                if (caveCell != null) {
                    URegion caveRegion = makeCaveRegion();
                    linkRegionAt(area, caveCell.x, caveCell.y, "cave entrance", caveRegion, 1, "cave exit");
                }
                scatterActorsByTags(area, x-3, y-3,x+rock.xsize+3, y+rock.ysize+3, new String[]{"rock"}, 1, 1+rand(3));
                scatterThingsByTags(area, x-3, y-3,x+rock.xsize+3, y+rock.ysize+3, new String[]{"rock"}, 1, 1+rand(3));
            }
        }

        // Ruins
        int ruins = rand(4)+3;
        int basements = rand(2)+1;
        for (int i=0;i<ruins;i++) {
            int ruinw = 20+rand(20); int ruinh = 20+rand(20);
            UCell ruinloc = findAreaWithout(area,1,1,area.xsize-ruinw,area.ysize-ruinh,ruinw-1,ruinh-1, new String[]{"rock"});
            if (ruinloc != null) {
                ArrayList<Room> rooms = new ArrayList<>();
                buildComplex(area, ruinloc.x, ruinloc.y, ruinloc.x + ruinw, ruinloc.y + ruinh, "floor", "wall",
                        new String[]{"tree","sapling","grass","water"}, 5, 9+rand(5),0.3f,3,4+rand(30),8+rand(6), rooms);
                if (i < basements) {
                    UCell doorcell = randomCell(area, "floor", ruinloc.x, ruinloc.y, ruinloc.x + ruinw, ruinloc.y + ruinh);
                    if (doorcell != null) {
                        URegion basementRegion = makeBasementRegion();
                        linkRegionAt(area, doorcell.x, doorcell.y, "trapdoor", basementRegion, 1, "ladder");
                    }
                }
                scatterActorsByTags(area, ruinloc.x,ruinloc.y,ruinloc.x+ruinw,ruinloc.y+ruinh,
                        new String[]{"complex"}, 1, 2 + random.nextInt(4));
                scatterThingsByTags(area, ruinloc.x,ruinloc.y,ruinloc.x+ruinw, ruinloc.y+ruinh,
                        new String[]{"complex"}, 1, 3 + rand(8));
            }
        }

        // Draw the roads now, after their edges are drawn, so we go over those
        roadmask.writeTerrain(area, "dirt", -2, -2);

        // Combine the roads and rivers to get bridges
        rivermask.maskWith(roadmask, Shape.MASK_AND);
        rivermask.writeTerrain(area, "wooden bridge", -2 ,-2, 0.97f);

        scatterThingsByTags(area, 0, 0, area.xsize-1, area.ysize-1, new String[]{"forest"}, 1, 50);
        scatterActorsByTags(area, 0, 0, area.xsize-1, area.ysize-1, new String[]{"forest"}, 1, 30);
    }

    public URegion makeCaveRegion() {
        String[] names = new String[]{"Caverns of pain", "Terror cave", "Mystery cave", "Caverns of Night", "Horror cave"};
        String name = names[rand(names.length)];
        String id = "cavern-" + Integer.toString(rand(100000));
        URegion region = new URegion(id, name, new ULandscaper[]{new ExampleCaveScaper()},
                        new String[]{"cave"}, 90, 90, rand(5)+3,
                        "cave entrance", "cave exit", "sounds/ultima_dungeon.ogg");
        return region;
    }

    public URegion makeBasementRegion() {
        String name = "Musty basement";
        String id = "basement-" + Integer.toString(rand(1000000));
        URegion region = new URegion(id, name, new ULandscaper[]{new ExampleDungeonScaper()},
                        new String[]{"basement"}, 60, 60, rand(4)+2, "trapdoor", "ladder", "sounds/ultima_dungeon.ogg");
        return region;
    }

}
