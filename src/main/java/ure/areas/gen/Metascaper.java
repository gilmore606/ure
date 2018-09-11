package ure.areas.gen;

import ure.areas.UArea;
import ure.areas.gen.shapers.Shaper;
import ure.ui.ULight;

import java.util.ArrayList;

public class Metascaper extends ULandscaper {

    public static final String TYPE = "meta";
    Shaper shaper;
    String wallTerrain, floorTerrain, doorTerrain;
    boolean pruneDeadEnds, wipeSmallRegions, roundCorners;
    float doorChance, lightChance;
    ArrayList<ULight> roomLights;

    public Metascaper() {
        super(TYPE);
    }

    public void buildArea(UArea area, int level, String[] tags) {
        area.wipe(wallTerrain);
        if (pruneDeadEnds)
            shaper.pruneDeadEnds();
        if (wipeSmallRegions)
            shaper.wipeSmallRegions();
        if (roundCorners)
            shaper.roundCorners();
        shaper.writeTerrain(area, floorTerrain, 1, 1);
        shaper.pruneRooms();
        if (doorChance > 0f)
            addDoors(area);
        if (lightChance > 0f)
            addRoomLights(area);
    }

    public void setup(Shaper shaper, String wallTerrain, String floorTerrain, boolean pruneDeadEnds, boolean wipeSmallRegions, boolean roundCorners, String doorTerrain, float doorChance, float lightChance, ArrayList<ULight> roomLights) {
        this.shaper = shaper;
        this.wallTerrain = wallTerrain;
        this.floorTerrain = floorTerrain;
        this.pruneDeadEnds = pruneDeadEnds;
        this.wipeSmallRegions = wipeSmallRegions;
        this.roundCorners = roundCorners;
        this.doorTerrain = doorTerrain;
        this.doorChance = doorChance;
        this.lightChance = lightChance;
        this.roomLights = roomLights;
    }


    void addDoors(UArea area) {
        ArrayList<Boolean[][]> patterns = new ArrayList<>();
        patterns.add(new Boolean[][]{{true,false,true},{true,true,true},{true,false,true}});
        patterns.add(new Boolean[][]{{true,false,true},{true,true,true},{false,false,true}});
        patterns.add(new Boolean[][]{{true,false,true},{true,true,true},{false,false,false}});
        patterns.add(new Boolean[][]{{false,false,true},{true,true,true},{false,false,true}});
        patterns.add(new Boolean[][]{{true,false,true},{true,true,true},{true,false,false}});
        for (int x=0;x<area.xsize;x++) {
            for (int y=0;y<area.ysize;y++) {
                if (shaper.value(x,y)) {
                    if (canDoor(area, x, y, patterns) && random.f() < doorChance) {
                        area.setTerrain(x+1, y+1, doorTerrain);
                    }
                }
            }
        }
    }

    boolean canDoor(UArea area, int x, int y, ArrayList<Boolean[][]> patterns) {
        for (Boolean[][] p : patterns) {
            if (shaper.matchNeighbors(x,y,p))
                return true;
        }
        return false;
    }

    void addRoomLights(UArea area) {
        for (Shaper.Room r : shaper.rooms) {
            if (!r.isHallway() && r.isOpen(area)) {
                if (random.f() < lightChance) {
                    ULight l = roomLights.get(random.i(roomLights.size())).clone();
                    if (l.type == ULight.AMBIENT) {
                        l.makeAmbient(r.width, r.height);
                        l.moveTo(area, r.x + 1, r.y + 1);
                    } else {
                        l.setRange(Math.max(r.width,r.height)+2);
                        l.setFalloff(l.getRange()/2);
                        l.moveTo(area,r.x+(r.width/2),r.y+(r.height/2));
                    }
                }
            }
        }
    }
}
