package ure.areas.gen.shapers;

import ure.areas.UArea;
import ure.areas.gen.Layer;
import ure.areas.gen.Shape;

public class Outline extends Shaper {

    public Outline(int xsize, int ysize) {
        super(xsize,ysize);
        name = "Outline";
    }

    @Override
    public void setupParams() {
        addParamB("useTerrainSource", false);
        addParamT("terrainSource", "null");
        addParamB("inner", false);
        addParamB("diagonals", true);
        addParamI("iterations", 1, 1, 5);
        addParamF("densityDrop", 0f, 0f, 1f);
        addParamF("writeChance", 0f, 1f, 1f);
    }

    @Override
    public void build(Layer previousLayer, UArea area) {
        buildOutline(previousLayer, area, getParamB("useTerrainSource"), getParamT("terrainSource"), getParamB("inner"), getParamB("diagonals"), getParamI("iterations"), getParamF("densityDrop"), getParamF("writeChance"));
    }

    public void buildOutline(Layer previousLayer, UArea area, boolean useTerrainSource, String terrainSource, boolean inner, boolean diagonals, int iterations, float densityDrop, float writeChance) {
        clear();
        if (previousLayer == null || area == null) return;
        Shape source = useTerrainSource ? makeTerrainMask(area,terrainSource) : previousLayer.shaper;
        Shape scratch = new Shape(xsize,ysize);
        for (int i=0;i<iterations;i++) {
            scratch.clear();
            for (int x = 0;x < xsize;x++) {
                for (int y = 0;y < ysize;y++) {
                    if (inner) {
                        if (!value(x,y) && source.value(x,y)) {
                            if (!source.value(x+1,y) || value(x+1,y) ||
                                !source.value(x,y+1) || value(x,y+1) ||
                                !source.value(x-1,y) || value(x-1,y) ||
                                !source.value(x,y-1) || value(x,y-1) ||
                                    (diagonals && (!source.value(x-1,y-1) || value(x-1,y-1))) ||
                                    (diagonals && (!source.value(x+1,y+1) || value(x+1,y+1))) ||
                                    (diagonals && (!source.value(x-1,y+1) || value(x-1,y+1))) ||
                                    (diagonals && (!source.value(x+1,y-1) || value(x+1,y-1)))) {
                                if (writeChance >= 1f || random.f() < writeChance) {
                                    scratch.set(x, y);
                                }
                            }
                        }
                    } else {
                        if (!value(x,y) && !source.value(x, y)) {
                            if (source.value(x + 1, y) || value(x + 1, y) ||
                                    source.value(x, y + 1) || value(x, y + 1) ||
                                    source.value(x - 1, y) || value(x - 1, y) ||
                                    source.value(x, y - 1) || value(x, y - 1) ||
                                    (diagonals && ((source.value(x - 1, y - 1)) || value(x - 1, y - 1))) ||
                                    (diagonals && ((source.value(x + 1, y + 1)) || value(x + 1, y + 1))) ||
                                    (diagonals && ((source.value(x - 1, y + 1)) || value(x - 1, y + 1))) ||
                                    (diagonals && ((source.value(x + 1, y - 1)) || value(x + 1, y - 1)))) {
                                if (writeChance >= 1f || random.f() < writeChance) {
                                    scratch.set(x, y);
                                }
                            }
                        }
                    }
                }
            }
            maskWith(scratch, MASK_OR, 0, 0);
        }
    }


}
