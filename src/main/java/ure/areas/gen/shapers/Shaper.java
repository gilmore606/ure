package ure.areas.gen.shapers;

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
    public int getParamI(String param) { return paramsI.get(param); }
    public float getParamF(String param) { return paramsF.get(param); }

    abstract void setupParams();

    public abstract void build();

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
}
