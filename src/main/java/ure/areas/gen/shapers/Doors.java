package ure.areas.gen.shapers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.areas.UArea;
import ure.areas.gen.Layer;
import ure.areas.gen.Shape;

import java.util.ArrayList;

public class Doors extends Shaper {

    @JsonIgnore
    ArrayList<Boolean[][]> patterns;

    public static final String TYPE = "Doors";

    public Doors() {
        super(TYPE);
        patterns = new ArrayList<>();
        patterns.add(new Boolean[][]{{true,false,true},{true,true,true},{true,false,true}});
        patterns.add(new Boolean[][]{{true,false,true},{true,true,true},{false,false,true}});
        patterns.add(new Boolean[][]{{true,false,true},{true,true,true},{false,false,false}});
        patterns.add(new Boolean[][]{{false,false,true},{true,true,true},{false,false,true}});
        patterns.add(new Boolean[][]{{true,false,true},{true,true,true},{true,false,false}});
        patterns.add(new Boolean[][]{{true,true,true},{false,true,false},{false,true,false}});
        patterns.add(new Boolean[][]{{true,true,true},{false,true,false},{true,true,true}});
        patterns.add(new Boolean[][]{{true,true,true},{false,true,false},{true,true,false}});
        patterns.add(new Boolean[][]{{true,true,true},{false,true,false},{false,true,true}});
    }

    @Override
    public void setupParams() {
        addParamB("useTerrainSource", false);
        addParamT("terrainSource", "null");
        addParamI("separation", 2, 4, 10);
    }

    @Override
    public void build(Layer previousLayer, UArea area) {
        buildDoors(previousLayer, area, getParamB("useTerrainSource"), getParamT("terrainSource"), getParamI("separation"));
    }

    public void buildDoors(Layer previousLayer, UArea area, boolean useTerrainSource, String terrainSource, int separation) {
        if (previousLayer == null || area == null) return;
        clear();
        Shape source = useTerrainSource ? makeTerrainMask(area,terrainSource) : previousLayer.shaper;
        for (int x=0;x<xsize;x++) {
            for (int y=0;y<ysize;y++) {
                if (source.value(x,y)) {
                    for (Boolean[][] p : patterns) {
                        if (source.matchNeighbors(x,y,p)) {
                            if (!hasNeighborWithin(x,y,separation))
                                set(x,y);
                        }
                    }
                }
            }
        }
    }
}
