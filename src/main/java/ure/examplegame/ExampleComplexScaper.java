package ure.examplegame;

import ure.areas.UArea;
import ure.areas.ULandscaper;
import ure.terrain.UTerrainCzar;
import ure.things.UThingCzar;

public class ExampleComplexScaper extends ULandscaper {

    public ExampleComplexScaper(UTerrainCzar theTerrainCzar, UThingCzar theThingCzar) {
        super(theTerrainCzar, theThingCzar);
    }

    @Override
    public void buildArea(UArea area) {
        fillRect(area, "grass", 0, 0, area.xsize-1, area.ysize-1);
        buildComplex(area, 0, 0, area.xsize-1, area.ysize-1, "floor", "wall", new String[]{"grass","mud","sand"},
                6, 10+rand(20), 0.4f, 5, 30+rand(100), 10+rand(15));

    }

}
