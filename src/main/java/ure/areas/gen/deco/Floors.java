package ure.areas.gen.deco;

import ure.areas.UArea;
import ure.areas.gen.Room;

public class Floors extends Deco {

    public static final String TYPE = "Floors";

    public Floors() { super(TYPE); }

    @Override
    public void setupParams() {
        addParamT("floor", "null");
        addParamT("edge", "null");
    }

    @Override
    public void build(Room room, UArea area) {

    }
}
