package ure.areas.gen.shapers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ure.areas.UArea;
import ure.areas.gen.Layer;
import ure.areas.gen.Shape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Growdungeon extends Shaper {

    public static final String TYPE = "Growdungeon";

    public Growdungeon() { super(TYPE); }


    @Override
    public void setupParams() {
        addParamI("iterations", 1, 500, 1000);
        addParamI("roomSizeMin", 1, 3, 15);
        addParamI("roomSizeMax", 2, 6, 30);
        addParamI("chamberSizeMin", 2, 9, 30);
        addParamI("chamberSizeMax", 2, 15, 40);
        addParamI("hallLengthMin", 2, 8, 20);
        addParamI("hallLengthMax", 3, 25, 50);
        addParamI("hallWidth", 1, 1, 5);
        addParamF("roomChance", 0f, 0.9f, 1f);
        addParamF("chamberChance", 0f, 0.01f, 1f);
        addParamF("hallChance", 0f, 0.75f, 1f);
        addParamI("earlyIterations", 0, 30, 200);
        addParamF("earlyRoomChance", 0f, 0.02f, 1f);
        addParamF("earlyChamberChance", 0f, 0.2f, 1f);
        addParamI("earlyHallWidth", 1, 2, 5);
        addParamF("roundedChance", 0f, 0f, 1f);
        addParamF("openChance", 0f, 0.06f, 1f);
    }

    @Override
    public void build(Layer previousLayer, UArea area) {
        buildGrowdungeon(getParamI("iterations"), getParamI("roomSizeMin"),getParamI("roomSizeMax"),getParamI("chamberSizeMin"),getParamI("chamberSizeMax"),getParamI("hallLengthMin"),getParamI("hallLengthMax"),getParamI("hallWidth"),getParamF("roomChance"),getParamF("chamberChance"),getParamF("hallChance"),getParamI("earlyIterations"),getParamF("earlyRoomChance"),getParamF("earlyChamberChance"),getParamI("earlyHallWidth"),getParamF("roundedChance"),getParamF("openChance"));
    }

    public void buildGrowdungeon(int iterations,
                       int roomSizeMin, int roomSizeMax, int chamberSizeMin, int chamberSizeMax,
                       int hallLengthMin, int hallLengthMax, int hallWidth,
                       float roomChance, float chamberChance, float hallChance,
                       int earlyIterations, float earlyRoomChance, float earlyChamberChance, int earlyHallWidth,
                       float roundedChance, float openChance) {
        clear();
        Room firstroom = new Room(xsize/2-5, ysize/2-5, random.i(chamberSizeMin,chamberSizeMax), random.i(chamberSizeMin,chamberSizeMax));
        firstroom.print(this);
        addRoom(firstroom);
        ArrayList<Face> faces = new ArrayList<>();
        for (Face face : firstroom.faces())
            faces.add(face);
        int iter = 0;
        while (!faces.isEmpty() && iter < iterations) {
            Room newroom = randomRoom((iter < earlyIterations) ? earlyRoomChance : roomChance, roomSizeMin, roomSizeMax,
                    (iter < earlyIterations) ? earlyChamberChance : chamberChance, chamberSizeMin, chamberSizeMax,
                    hallChance, hallLengthMin, hallLengthMax, hallWidth, (iter < earlyIterations) ? earlyHallWidth : hallWidth);
            Face face = (Face) random.member((List) faces);
            if (face.addRoom(newroom, this) != null) {
                newroom.print(this, random.f() < roundedChance);
                addRoom(newroom);
                if (random.f() < 0.5f)
                    newroom.punchDoors(this, random.f() < openChance);
                else
                    face.punchDoors(this, random.f() < openChance);
                //faces.remove(face);
                for (Face newface : newroom.faces()) {
                    faces.add(newface);
                    if (newroom.isHallway() && newface.length == Math.min(newroom.width,newroom.height))
                        newface.punchDoors(this, true);
                }
            } else {
                if (iter > earlyIterations && random.f() < openChance)
                    faces.remove(face);
            }
            iter++;
        }
    }
}
