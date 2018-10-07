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
    public ArrayList<Roomgroup> groups;

    public String name;
    public int xsize,ysize;
    String wallTerrain;
    float lightChance;
    ArrayList<ULight> roomLights;
    @JsonIgnore
    UVaultSet vaultSet;
    String vaultSetName;

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
        buildRoomgroups(area);

        if (vaultSet != null)
            addVaults(area);
        if (lightChance > 0f)
            addRoomLights(area);
    }

    public void buildRoomgroups(UArea area) {
        if (groups == null) return;
        if (groups.size() < 1) return;
        groups.get(0).filterRooms(rooms, area);
        ArrayList<Shape.Room> baseRooms = groups.get(0).rooms;
        rooms.clear();
        for (Shape.Room r : baseRooms)
            rooms.add(r);
        if (groups.size() > 1) {
            for (int i=1;i<groups.size();i++) {
                groups.get(i).filterRooms(baseRooms, area);
            }
        }
    }

    public void setup(String name, int xsize, int ysize, ArrayList<Layer> layers, ArrayList<Roomgroup> groups, String wallTerrain, float lightChance, ArrayList<ULight> roomLights, String vaultSetName) {
        this.name = name;
        this.xsize = xsize;
        this.ysize = ysize;
        this.layers = layers;
        this.groups = groups;
        this.wallTerrain = wallTerrain;
        this.lightChance = lightChance;
        this.roomLights = roomLights;
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

    public ArrayList<Layer> getLayers() {
        return layers;
    }

    public void setLayers(ArrayList<Layer> layers) {
        this.layers = layers;
    }

    public String getWallTerrain() {
        return wallTerrain;
    }

    public void setWallTerrain(String wallTerrain) {
        this.wallTerrain = wallTerrain;
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

    public ArrayList<Shape.Room> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<Shape.Room> rooms) {
        this.rooms = rooms;
    }

    public String getName() { return name; }
    public void setName(String s) { name = s; }

    public ArrayList<Roomgroup> getGroups() { return groups; }
    public void setGroups(ArrayList<Roomgroup> groups) { this.groups = groups; }

}
