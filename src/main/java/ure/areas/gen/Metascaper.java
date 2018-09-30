package ure.areas.gen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ure.areas.UArea;
import ure.areas.UCell;
import ure.math.Dimap;
import ure.math.DimapEntity;
import ure.ui.ULight;

import java.util.ArrayList;
import java.util.HashSet;

public class Metascaper extends ULandscaper {



    public static final String TYPE = "meta";

    public ArrayList<Layer> layers;
    @JsonIgnore
    public Layer roomLayer;

    public String name;
    public int xsize,ysize;
    String wallTerrain, entranceTerrain, exitTerrain;
    float lightChance;
    ArrayList<ULight> roomLights;
    @JsonIgnore
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
        rooms = new ArrayList<>();
        Layer previousLayer = null;
        for (Layer layer : layers) {
            layer.shaper.rooms = rooms;
            layer.build(previousLayer, area);
            layer.print(area, 0, 0);
            previousLayer = layer;
            rooms = layer.shaper.rooms;
        }

        if (vaultSet != null)
            addVaults(area);
        if (lightChance > 0f)
            addRoomLights(area);
        addStairs(area);
    }

    public void setup(String name, int xsize, int ysize, ArrayList<Layer> layers, String wallTerrain, float lightChance, ArrayList<ULight> roomLights, String vaultSetName, String entranceTerrain, String exitTerrain, int exitDistance) {
        this.name = name;
        this.xsize = xsize;
        this.ysize = ysize;
        this.layers = layers;
        this.wallTerrain = wallTerrain;
        this.lightChance = lightChance;
        this.roomLights = roomLights;
        this.entranceTerrain = entranceTerrain;
        this.exitTerrain = exitTerrain;
        this.exitDistance = exitDistance;
        if (vaultSetName != null)
            if (!vaultSetName.equals(this.vaultSetName))
                this.vaultSet = commander.cartographer.loadVaultSet(vaultSetName);
    }



    void addRoomLights(UArea area) {
        if (roomLights == null) return;
        if (roomLights.size() == 0) return;
        for (Shape.Room r : rooms) {
            if (!r.isHallway() && r.unobstructed(area)) {
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

    public ArrayList<Layer> getLayers() {
        return layers;
    }

    public void setLayers(ArrayList<Layer> layers) {
        this.layers = layers;
    }

    public Layer getRoomLayer() {
        return roomLayer;
    }

    public void setRoomLayer(Layer roomLayer) {
        this.roomLayer = roomLayer;
    }

    public String getWallTerrain() {
        return wallTerrain;
    }

    public void setWallTerrain(String wallTerrain) {
        this.wallTerrain = wallTerrain;
    }

    public String getEntranceTerrain() {
        return entranceTerrain;
    }

    public void setEntranceTerrain(String entranceTerrain) {
        this.entranceTerrain = entranceTerrain;
    }

    public String getExitTerrain() {
        return exitTerrain;
    }

    public void setExitTerrain(String exitTerrain) {
        this.exitTerrain = exitTerrain;
    }

    public float getLightChance() {
        return lightChance;
    }

    public void setLightChance(float lightChance) {
        this.lightChance = lightChance;
    }

    public ArrayList<ULight> getRoomLights() {
        return roomLights;
    }

    public void setRoomLights(ArrayList<ULight> roomLights) {
        this.roomLights = roomLights;
    }

    public UVaultSet getVaultSet() {
        return vaultSet;
    }

    public void setVaultSet(UVaultSet vaultSet) {
        this.vaultSet = vaultSet;
    }

    public String getVaultSetName() {
        return vaultSetName;
    }

    public void setVaultSetName(String vaultSetName) {
        this.vaultSetName = vaultSetName;
    }

    public int getExitDistance() {
        return exitDistance;
    }

    public void setExitDistance(int exitDistance) {
        this.exitDistance = exitDistance;
    }

    public ArrayList<Shape.Room> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<Shape.Room> rooms) {
        this.rooms = rooms;
    }

    public String getName() { return name; }
    public void setName(String s) { name = s; }
}
