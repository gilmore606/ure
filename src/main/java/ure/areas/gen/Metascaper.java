package ure.areas.gen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ure.areas.UArea;
import ure.ui.ULight;

import java.util.ArrayList;

public class Metascaper extends ULandscaper {



    public static final String TYPE = "meta";

    public ArrayList<Layer> layers;
    public ArrayList<Roomgroup> groups;

    public String name;
    public int xsize,ysize;

    @JsonIgnore
    ArrayList<Room> rooms;

    @JsonIgnore
    private Log log = LogFactory.getLog(Metascaper.class);

    public Metascaper() {
        super(TYPE);
    }

    public void buildArea(UArea area, int level, String[] tags) {
        area.wipe("null");

        Layer previousLayer = null;
        for (Layer layer : layers) {
            layer.build(previousLayer, area);
            layer.print(area, 0, 0);
            previousLayer = layer;
        }

        collectRooms();
        buildRoomgroups(area);
    }

    void collectRooms() {
        rooms = new ArrayList<>();
        for (Layer layer : layers) {
            ArrayList<Room> candidates = layer.shaper.rooms;
            ArrayList<Room> discards = new ArrayList<>();
            for (Room cand : candidates) {
                boolean collided = false;
                for (Room test : rooms) {
                    if (cand != test && !discards.contains(test) && test.touches(cand)) {
                        if ((test.height*test.width) < (cand.height*cand.width)) {
                            collided = true;
                            if (!rooms.contains(cand))
                                rooms.add(cand);
                            if (!discards.contains(test))
                                discards.add(test);
                        }
                    }
                }
                if (!collided)
                    rooms.add(cand);
            }
            for (Room dis : discards) {
                rooms.remove(dis);
            }
        }
    }

    public void buildRoomgroups(UArea area) {
        if (groups == null) return;
        if (groups.size() < 1) return;
        ArrayList<Room> baseRooms = new ArrayList<>();
        for (Room r : rooms)
            baseRooms.add(r);
        groups.get(0).filterRooms(baseRooms, area);
        baseRooms = groups.get(0).rooms;
        if (groups.size() > 1) {
            for (int i=1;i<groups.size();i++) {
                groups.get(i).filterRooms(baseRooms, area);
            }
        }
    }

    public void setup(String name, int xsize, int ysize, ArrayList<Layer> layers, ArrayList<Roomgroup> groups) {
        this.name = name;
        this.xsize = xsize;
        this.ysize = ysize;
        this.layers = layers;
        this.groups = groups;
    }


    public ArrayList<Layer> getLayers() {
        return layers;
    }

    public void setLayers(ArrayList<Layer> layers) {
        this.layers = layers;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    public String getName() { return name; }
    public void setName(String s) { name = s; }

    public ArrayList<Roomgroup> getGroups() { return groups; }
    public void setGroups(ArrayList<Roomgroup> groups) { this.groups = groups; }

}
