package ure.examplegame;

import ure.areas.UArea;
import ure.areas.ULandscaper;

import java.util.ArrayList;

public class ExampleComplexScaper extends ULandscaper {

    public static final String TYPE = "complexscaper";

    public ExampleComplexScaper() {
        super(TYPE);
    }

    @Override
    public void buildArea(UArea area, int level, String[] tags) {
        fillRect(area, "grass", 0, 0, area.xsize-1, area.ysize-1);
        ArrayList<Room> rooms = new ArrayList<>();
        buildComplex(area, 0, 0, area.xsize-1, area.ysize-1, "floor", "wall", new String[]{"grass","mud","sand"},
                6, 10+rand(20), 0.4f, 5, 30+rand(100), 10+rand(15), rooms);

    }

}
