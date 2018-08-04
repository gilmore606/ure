package ure.examplegame;

import ure.areas.Shape;
import ure.areas.UArea;
import ure.areas.ULandscaper;

public class ExampleMineScaper extends ULandscaper {

    public static final String TYPE = "minescaper";

    public ExampleMineScaper() {
        super(TYPE);
    }

    @Override
    public void buildArea(UArea area, int level, String[] tags) {
        fillRect(area, "rock", 0, 0, 100,100);
        Shape mask = shapeMines(100,100,3,6, 12,7, 0.4f, 0.02f, 0.6f, 0.6f, 1f, 300, 3,12);
        mask.writeTerrain(area, "floor", 0, 0);
        mask.edges().sparsen(12, 24).writeThings(area, "lamppost", 0,0);
        addDoors(area, "door", new String[]{"rock"}, 1f);
    }

}
