package ure.areas.gen;

import java.util.ArrayList;

public class Roomgroup {

    public boolean includeHallways;
    public int maxCount;
    public int minRoomSize;
    public int maxRoomSize;
    public float frequency;
    public int separation;
    public String floorType;

    public ArrayList<Shape.Room> rooms;

    public Roomgroup() {

    }

    public void update(boolean includeHallways, int maxCount, int minRoomSize, int maxRoomSize, float frequency, int separation, String floorTYpe) {
        this.includeHallways = includeHallways;
        this.maxCount = maxCount;
        this.minRoomSize = minRoomSize;
        this.maxRoomSize = maxRoomSize;
        this.frequency = frequency;
        this.separation = separation;
        this.floorType = floorType;
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
}
