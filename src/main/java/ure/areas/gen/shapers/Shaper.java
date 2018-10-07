package ure.areas.gen.shapers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.areas.UArea;
import ure.areas.gen.Layer;
import ure.areas.gen.Room;
import ure.areas.gen.Shape;
import ure.sys.Injector;
import ure.sys.UCommander;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;

public class Shaper extends Shape {

    @JsonIgnore
    @Inject
    public UCommander commander;

    public HashMap<String,Integer> paramsI;
    public HashMap<String,Integer> paramsImin;
    public HashMap<String,Integer> paramsImax;
    public HashMap<String,Float> paramsF;
    public HashMap<String,Float> paramsFmin;
    public HashMap<String,Float> paramsFmax;
    public HashMap<String,Boolean> paramsB;
    public HashMap<String,String> paramsT;

    @JsonIgnore
    public ArrayList<Room> rooms;

    public String type;

    public Shaper() {
        super();
    }

    public Shaper(String type) {
        this();
        this.type = type;
    }

    public void initialize(int xsize, int ysize) {
        resize(xsize,ysize);
        paramsI = new HashMap<>();
        paramsImin = new HashMap<>();
        paramsImax = new HashMap<>();
        paramsF = new HashMap<>();
        paramsFmin = new HashMap<>();
        paramsFmax = new HashMap<>();
        paramsB = new HashMap<>();
        paramsT = new HashMap<>();
        setupParams();
    }

    public void addParamI(String name, int min, int val, int max) {
        paramsI.put(name,val);
        paramsImin.put(name,min);
        paramsImax.put(name,max);
    }
    public void addParamF(String name, float min, float val, float max) {
        paramsF.put(name,val);
        paramsFmin.put(name,min);
        paramsFmax.put(name,max);
    }
    public void addParamB(String name, boolean val) {
        paramsB.put(name,val);
    }
    public void addParamT(String name, String val) {
        paramsT.put(name,val);
    }

    public int getParamI(String param) { return paramsI.get(param); }
    public float getParamF(String param) { return paramsF.get(param); }
    public boolean getParamB(String param) { return paramsB.get(param); }
    public String getParamT(String param) { return paramsT.get(param); }

    public void setupParams() { }

    /**
     *
     * Build the shape based on the parameters defined.  We're given the previous layer shape, and the area we're
     * building for, in case we want to react to those.  However we should not print into the area, we should just
     * build our shape.
     */

    public void build() { build(null,null); }
    public void build(Layer previousLayer, UArea area) { }

    @Override
    public Shape clear() {
        if (cells == null) {
            cells = new boolean[xsize][ysize];
            buffer = new boolean[xsize][ysize];
        }
        return super.clear();
    }

    void addRoom(Room r) {
        for (Room t : rooms) {
            if (t.touches(r)) {
                return;
            }
        }
        rooms.add(r);
    }

    public void pruneRooms() {
        if (rooms == null) return;
        ArrayList<Room> keepers = new ArrayList<>();
        for (Room r : rooms) {
            if (r.unobstructed(this))
                keepers.add(r);
        }
        this.rooms = keepers;
    }

    public static Shape makeTerrainMask(UArea area, String terrain) {
        Shape mask = new Shape(area.xsize, area.ysize);
        for (int x=0;x<area.xsize;x++) {
            for (int y = 0;y < area.ysize;y++)
                if (area.hasTerrainAt(x, y, terrain))
                    mask.set(x, y);
        }
        return mask;
    }

    public HashMap<String, Integer> getParamsI() {
        return paramsI;
    }

    public void setParamsI(HashMap<String, Integer> paramsI) {
        this.paramsI = paramsI;
    }

    public HashMap<String, Integer> getParamsImin() {
        return paramsImin;
    }

    public void setParamsImin(HashMap<String, Integer> paramsImin) {
        this.paramsImin = paramsImin;
    }

    public HashMap<String, Integer> getParamsImax() {
        return paramsImax;
    }

    public void setParamsImax(HashMap<String, Integer> paramsImax) {
        this.paramsImax = paramsImax;
    }

    public HashMap<String, Float> getParamsF() {
        return paramsF;
    }

    public void setParamsF(HashMap<String, Float> paramsF) {
        this.paramsF = paramsF;
    }

    public HashMap<String, Float> getParamsFmin() {
        return paramsFmin;
    }

    public void setParamsFmin(HashMap<String, Float> paramsFmin) {
        this.paramsFmin = paramsFmin;
    }

    public HashMap<String, Float> getParamsFmax() {
        return paramsFmax;
    }

    public void setParamsFmax(HashMap<String, Float> paramsFmax) {
        this.paramsFmax = paramsFmax;
    }

    public HashMap<String, Boolean> getParamsB() {
        return paramsB;
    }

    public void setParamsB(HashMap<String, Boolean> paramsB) {
        this.paramsB = paramsB;
    }

    public HashMap<String, String> getParamsT() {
        return paramsT;
    }

    public void setParamsT(HashMap<String, String> paramsT) {
        this.paramsT = paramsT;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
