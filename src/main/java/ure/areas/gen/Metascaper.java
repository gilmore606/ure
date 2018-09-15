package ure.areas.gen;

import ure.areas.UArea;
import ure.areas.gen.shapers.Shaper;
import ure.ui.ULight;

import java.util.ArrayList;

public class Metascaper extends ULandscaper {



    public static final String TYPE = "meta";

    public ArrayList<Layer> layers;
    public Layer roomLayer;

    String wallTerrain, doorTerrain, structureTerrain;
    float doorChance, lightChance;
    ArrayList<ULight> roomLights;

    public Metascaper() {
        super(TYPE);
    }

    public void buildArea(UArea area, int level, String[] tags) {
        area.wipe(wallTerrain);
        for (Layer layer : layers) {
            layer.build();
            layer.print(area, 1, 1, structureTerrain);
        }

        roomLayer = layers.get(0);
        for (Layer layer : layers) {
            if (layer.rooms().size() > roomLayer.rooms().size())
                roomLayer = layer;
        }
        if (doorChance > 0f)
            addDoors(area);
        if (lightChance > 0f)
            addRoomLights(area);
    }

    public void setup(ArrayList<Layer> layers, String wallTerrain, String doorTerrain, String structureTerrain, float doorChance, float lightChance, ArrayList<ULight> roomLights) {
        this.layers = layers;
        this.wallTerrain = wallTerrain;
        this.doorTerrain = doorTerrain;
        this.structureTerrain = structureTerrain;
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
                if (roomLayer.shaper.value(x,y)) {
                    if (canDoor(area, x, y, patterns) && random.f() < doorChance) {
                        area.setTerrain(x+1, y+1, doorTerrain);
                    }
                }
            }
        }
    }

    boolean canDoor(UArea area, int x, int y, ArrayList<Boolean[][]> patterns) {
        for (Boolean[][] p : patterns) {
            if (roomLayer.shaper.matchNeighbors(x,y,p))
                return true;
        }
        return false;
    }

    void addRoomLights(UArea area) {
        for (Shaper.Room r : roomLayer.rooms()) {
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
