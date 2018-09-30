package ure.areas.gen.shapers;

import ure.areas.UArea;
import ure.areas.UCell;
import ure.areas.gen.Layer;
import ure.math.Dimap;
import ure.math.DimapEntity;

import java.util.HashSet;

public class Stairs extends Shaper {

    public static final String TYPE = "Stairs";

    public Stairs() {
        super(TYPE);
    }

    @Override
    public void setupParams() {
        addParamI("separation", 1, 10, 100);
        addParamT("entranceType", "null");
        addParamT("exitType", "null");
        addParamB("roomsOnly", false);
    }

    @Override
    public void build(Layer previousLayer, UArea area) {
        buildStairs(previousLayer, area, getParamI("separation"), getParamT("entranceType"), getParamT("exitType"), getParamB("roomsOnly"));
    }

    public void buildStairs(Layer previousLayer, UArea area, int separation, String entranceType, String exitType, boolean roomsOnly) {
        clear();
        int sep = 0;
        DimapEntity dimap = new DimapEntity(area, Dimap.TYPE_SEEK, new HashSet<>(), null);
        int tries = 0;
        UCell entrance = null;
        UCell exit = null;
        while (sep < separation && tries < 1000) {
            entrance = area.randomOpenCell();
            if (entrance == null) {
                return;
            }
            dimap.changeEntity(entrance.terrain());
            int etries = 0;
            while (sep < separation && etries < 100) {
                exit = area.randomOpenCell();
                if (exit == null) {
                    return;
                }
                sep = (int) dimap.valueAt(exit.x, exit.y);
                etries++;
            }
            tries++;
        }
        if (entrance != null && exit != null) {
            area.setTerrain(entrance.x, entrance.y, entranceType);
            area.setTerrain(exit.x, exit.y, exitType);
        }
    }
}
