package ure.areas.gen.shapers;

import ure.areas.UArea;
import ure.areas.gen.Layer;
import ure.areas.gen.Shape;
import ure.sys.Injector;
import ure.sys.UCommander;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Shaper extends Shape {

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

    public ArrayList<Room> rooms;

    public String name;

    public Shaper(int xsize, int ysize) {
        super(xsize,ysize);
        Injector.getAppComponent().inject(this);
        paramsI = new HashMap<>();
        paramsImin = new HashMap<>();
        paramsImax = new HashMap<>();
        paramsF = new HashMap<>();
        paramsFmin = new HashMap<>();
        paramsFmax = new HashMap<>();
        paramsB = new HashMap<>();
        paramsT = new HashMap<>();
        rooms = new ArrayList<>();
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

    abstract void setupParams();

    /**
     *
     * Build the shape based on the parameters defined.  We're given the previous layer shape, and the area we're
     * building for, in case we want to react to those.  However we should not print into the area, we should just
     * build our shape.
     */

    public void build() { build(null,null); }
    public abstract void build(Layer previousLayer, UArea area);

    @Override
    public Shape clear() {
        rooms.clear();
        return super.clear();
    }

    public void pruneRooms() {
        ArrayList<Room> keepers = new ArrayList<>();
        for (Room r : rooms) {
            if (r.isOpen())
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
}
