package ure.examplegame;

import ure.areas.gen.Shape;
import ure.areas.UArea;
import ure.areas.gen.ULandscaper;

import java.util.ArrayList;
import java.util.List;

public class ExampleComplexScaper extends ULandscaper {

    public static final String TYPE = "complexscaper";

    public ExampleComplexScaper() {
        super(TYPE);
    }

    @Override
    public void buildArea(UArea area, int level, String[] tags) {
        ArrayList<Room> rooms = new ArrayList<>();
        fillRect(area, "wall", 0, 0, area.xsize - 1, area.ysize - 1);
        Shape space = new Shape(area.xsize, area.ysize);
        Room firstroom = new Room(20, 20, 10, 10);
        firstroom.print(space);
        rooms.add(firstroom);
        ArrayList<Face> faces = new ArrayList<>();
        for (Face face : firstroom.faces())
            faces.add(face);
        int iter = 0;
        while (!faces.isEmpty() && iter < 100000) {
            Room newroom = randomRoom((iter < 500) ? 0.02f : 0.9f, 3, 5,
                    (iter < 500) ? 0.2f : 0.01f, 8, 15,
                    0.75f, 10, 24, 1, (iter < 500) ? 2 : 1);
            Face face = (Face) random.member((List) faces);
            if (face.addRoom(newroom, space) != null) {
                newroom.print(space, random.f() < 0.1f);
                rooms.add(newroom);
                if (random.f() < 0.5f)
                    newroom.punchDoors(space, random.f() < 0.01f);
                else
                    face.punchDoors(space, random.f() < 0.01f);
                //faces.remove(face);
                for (Face newface : newroom.faces()) {
                    faces.add(newface);
                    if (newroom.isHallway() && newface.length == Math.min(newroom.width,newroom.height))
                        newface.punchDoors(space, true);
                }
            } else {
                if (iter > 500 && random.f() < 0.01f)
                    faces.remove(face);
            }
            iter++;
        }
        space.pruneDeadEnds().writeTerrain(area, "floor", 0, 0);
        addDoors(area, "door", new String[]{"rock", "door"}, 0.1f);
        for (Room room : rooms) {
            if (!room.isHallway())
                DecorateRoom(area, new int[]{room.x,room.y,room.width,room.height});
        }

        int lakes = rand(4) + 1;
        Shape lakemask = new Shape(area.xsize + 4, area.ysize + 4);
        for (int i = 0;i < lakes;i++) {
            int lakew = 4 + rand(12);
            int lakeh = 4 + rand(12);
            Shape lake = shapeBlob(lakew, lakeh);
            int x = rand(area.xsize);
            int y = rand(area.ysize);
            lake.writeTerrain(area, "still water", x, y);
            lakemask.maskWith(lake, Shape.MASK_OR, x, y);
            if (randf() < 0.8f) {
                Shape mud = lake.copy().grow(1 + rand(4)).erode(0.6f, 1 + rand(3)).maskWith(lake, Shape.MASK_NOT);
                mud.writeTerrain(area, "floormoss", x, y, 0.9f);
            }
            lake.shrink(rand(4)).smooth(7, 1 + rand(3)).writeTerrain(area, "deep water", x, y);
            //scatterActorsByTags(area, x-1,y-1,x+lakew,y+lakeh, new String[]{"forest"}, 4, 2);
        }

        // Run some rivers through the map.  Save them all in one mask for later.
        Shape rivermask = new Shape(area.xsize + 4, area.ysize + 4);
        if (random.f() < 0.4f) {
            Shape river = shapeRoad(area.xsize + 4, area.ysize + 4, 2 + random.i(3), 1.4f, 1.6f);
            river.writeTerrain(area, "water", -2, -2);
            rivermask.maskWith(river, Shape.MASK_OR);
            river.shrink(2).writeTerrain(area, "deep water", -2, -2, 0.8f);
        }
        scatterThingsByTags(area, 0, 0, area.xsize-1, area.ysize-1, new String[]{"complex"}, 1, 30);
        scatterActorsByTags(area, 0, 0, area.xsize-1, area.ysize-1, new String[]{"complex"}, 1, 20);
    }

}
