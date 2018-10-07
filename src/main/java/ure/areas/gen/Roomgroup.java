package ure.areas.gen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.areas.UArea;
import ure.math.UColor;
import ure.math.URandom;
import ure.sys.Injector;
import ure.terrain.UTerrain;

import javax.inject.Inject;
import java.util.ArrayList;

public class Roomgroup {

    public boolean includeHallways;
    public int maxCount;
    public int minRoomSize;
    public int maxRoomSize;
    public float frequency;
    public int separation;
    public String floorType;

    public UColor editorColor;

    @JsonIgnore
    public ArrayList<Shape.Room> rooms;

    @Inject
    @JsonIgnore
    public URandom random;

    public Roomgroup() {
        Injector.getAppComponent().inject(this);
        floorType = "null";
        rooms = new ArrayList<>();
    }

    public void update(boolean includeHallways, int maxCount, int minRoomSize, int maxRoomSize, float frequency, int separation, String floorType) {
        this.includeHallways = includeHallways;
        this.maxCount = maxCount;
        this.minRoomSize = minRoomSize;
        this.maxRoomSize = maxRoomSize;
        this.frequency = frequency;
        this.separation = separation;
        this.floorType = floorType;
    }

    public void filterRooms(ArrayList<Shape.Room> baseRooms, UArea area) {
        if (rooms == null) rooms = new ArrayList<>();
        rooms.clear();
        for (Shape.Room r : baseRooms) {
            if (!r.isHallway() || includeHallways) {
                if ((r.width * r.height) >= minRoomSize && (r.width * r.height) <= maxRoomSize) {
                    if (matchFloorType(r, area)) {
                        if (random.f() < frequency) {
                            rooms.add(r);
                        }
                    }
                }
            }
        }
        while (rooms.size() > maxCount) {
            rooms.remove(random.i(rooms.size()));
        }
        for (Shape.Room r : rooms) {
            baseRooms.remove(r);
        }
    }

    boolean matchFloorType(Shape.Room r, UArea area) {
        if (floorType == null) return true;
        if (floorType.equals("null")) return true;
        for (int x=0;x<r.width;x++) {
            for (int y=0;y<r.height;y++) {
                UTerrain t = area.terrainAt(x+r.x,y+r.y);
                if (t.name().equals(floorType))
                    return true;
            }
        }
        return false;
    }

    public boolean isIncludeHallways() {
        return includeHallways;
    }

    public void setIncludeHallways(boolean includeHallways) {
        this.includeHallways = includeHallways;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getMinRoomSize() {
        return minRoomSize;
    }

    public void setMinRoomSize(int minRoomSize) {
        this.minRoomSize = minRoomSize;
    }

    public int getMaxRoomSize() {
        return maxRoomSize;
    }

    public void setMaxRoomSize(int maxRoomSize) {
        this.maxRoomSize = maxRoomSize;
    }

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    public int getSeparation() {
        return separation;
    }

    public void setSeparation(int separation) {
        this.separation = separation;
    }

    public String getFloorType() {
        return floorType;
    }

    public void setFloorType(String floorType) {
        this.floorType = floorType;
    }

    public ArrayList<Shape.Room> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<Shape.Room> rooms) {
        this.rooms = rooms;
    }

    public UColor getEditorColor() { return editorColor; }
    public void setEditorColor(UColor c) { editorColor = c; }
}
