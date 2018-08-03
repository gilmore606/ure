package ure.examplegame;

import ure.areas.UArea;
import ure.areas.UCartographer;
import ure.areas.ULandscaper;

public class ExampleCartographer extends UCartographer {

    @Override
    public void setupRegions() {
        // First try to load regions from disk
        super.setupRegions();
        // If regions were loaded from disk then we shouldn't need to do anything else here.  If there weren't
        // any to load, then we'll need to add one to get things started.
        if (regions.isEmpty()) {
            addRegion(
                    new ExampleRegionForest(
                            "forest",
                            "Mystic Forest",
                            new ULandscaper[]{new ExampleForestScaper()},
                            new String[]{"start"},
                            200,
                            200,
                            1,
                            "",
                            "",
                            "sounds/ultima_wanderer.ogg"
                    )
            );
        }
        startArea = "forest 1";
    }

    @Override
    public UArea getTitleArea() {
        UArea area = super.getTitleArea();
        removeActiveArea(area);
        ExampleCaveScaper scaper = new ExampleCaveScaper();
        scaper.buildArea(area, 1, new String[]{"cave","title"});
        scaper.scatterThings(area, new String[]{"crystal stalagmite"}, new String[]{"floor"}, 30);
        scaper.scatterThings(area, new String[]{"magma vent"}, new String[]{"floor"}, 30);
        scaper.scatterActorsByTags(area, 0,0,area.xsize-1, area.ysize-1, new String[]{"title"}, 1, 20);
        scaper.scatterThings(area, new String[]{"lamppost"}, new String[]{"floor"}, 10);
        addActiveArea(area);
        return area;
    }

}
