package ure.areas.gen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ure.areas.UArea;
import ure.areas.UCell;
import ure.areas.gen.shapers.Shaper;
import ure.math.Dimap;
import ure.math.DimapEntity;
import ure.ui.ULight;

import java.util.ArrayList;
import java.util.HashSet;

public class Metascaper extends ULandscaper {



    public static final String TYPE = "meta";

    public ArrayList<Layer> layers;
    public Layer roomLayer;

    String wallTerrain, doorTerrain, entranceTerrain, exitTerrain;
    float doorChance, lightChance;
    ArrayList<ULight> roomLights;
    UVaultSet vaultSet;
    String vaultSetName;
    int exitDistance;

    @JsonIgnore
    ArrayList<Shape.Room> rooms;

    @JsonIgnore
    private Log log = LogFactory.getLog(Metascaper.class);

    public Metascaper() {
        super(TYPE);
    }

    public void buildArea(UArea area, int level, String[] tags) {
        area.wipe(wallTerrain);
        Layer previousLayer = null;
        for (Layer layer : layers) {
            layer.build(previousLayer, area);
            layer.print(area, 0, 0);
            previousLayer = layer;
        }

        roomLayer = layers.get(0);
        for (Layer layer : layers) {
            if (layer.rooms().size() > roomLayer.rooms().size())
                roomLayer = layer;
        }
        rooms = roomLayer.rooms();
        if (doorChance > 0f)
            addDoors(area);
        if (vaultSet != null)
            addVaults(area);
        if (lightChance > 0f)
            addRoomLights(area);
        addStairs(area);
    }

    public void setup(ArrayList<Layer> layers, String wallTerrain, String doorTerrain, float doorChance, float lightChance, ArrayList<ULight> roomLights, String vaultSetName, String entranceTerrain, String exitTerrain, int exitDistance) {
        this.layers = layers;
        this.wallTerrain = wallTerrain;
        this.doorTerrain = doorTerrain;
        this.doorChance = doorChance;
        this.lightChance = lightChance;
        this.roomLights = roomLights;
        this.entranceTerrain = entranceTerrain;
        this.exitTerrain = exitTerrain;
        this.exitDistance = exitDistance;
        if (vaultSetName != null)
            if (!vaultSetName.equals(this.vaultSetName))
                this.vaultSet = commander.cartographer.loadVaultSet(vaultSetName);
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
                        area.setTerrain(x, y, doorTerrain);
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
        for (Shape.Room r : rooms) {
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

    void addVaults(UArea area) {
        if (vaultSet == null) return;
        if (rooms == null) return;
        ArrayList<UVault> vaults = vaultSet.getVaults();
        for (UVault v : vaults) {
            for (Shape.Room r : rooms) {
                if (v.fitsIn(r)) {
                    v.printToArea(area, r);
                    if (v.lights != null)
                        rooms.remove(r);
                    break;
                }
            }
        }
    }

    void addStairs(UArea area) {
        int separation = 0;
        DimapEntity dimap = new DimapEntity(area, Dimap.TYPE_SEEK, new HashSet<>(), null);
        int tries = 0;
        UCell entrance = null;
        UCell exit = null;
        while (separation < exitDistance && tries < 500) {
            entrance = area.randomOpenCell();
            if (entrance == null) {
                log.info("Failed to find randomOpenCell for entrance");
                return;
            }
            dimap.changeEntity(entrance.terrain());
            int etries = 0;
            while (separation < exitDistance && etries < 50) {
                exit = area.randomOpenCell();
                if (exit == null) {
                    log.info("Failed to find randomOpenCell for exit");
                    return;
                }
                separation = (int) dimap.valueAt(exit.x, exit.y);
                etries++;
            }
            tries++;
        }
        if (entrance != null && exit != null) {
            area.setTerrain(entrance.x, entrance.y, entranceTerrain);
            area.setTerrain(exit.x, exit.y, exitTerrain);
        }
    }
}
