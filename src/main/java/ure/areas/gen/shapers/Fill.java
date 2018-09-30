package ure.areas.gen.shapers;

import ure.areas.UArea;
import ure.areas.gen.Layer;

public class Fill extends Shaper {

    public static final String TYPE = "Fill";

    public Fill() { super(TYPE); }

    @Override
    public void setupParams() {

    }

    @Override
    public void build(Layer previousLayer, UArea area) {
        buildFill();
    }

    public void buildFill() {
        clear();
        for (int x=0;x<xsize;x++) {
            for (int y=0;y<ysize;y++) {
                set(x,y);
            }
        }
    }
}
