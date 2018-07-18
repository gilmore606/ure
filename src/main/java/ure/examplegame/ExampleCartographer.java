package ure.examplegame;

import ure.areas.UCartographer;
import ure.areas.ULandscaper;

public class ExampleCartographer extends UCartographer {

    @Override
    public void setupRegions() {
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
                            100,
                            100,
                            1,
                            "",
                            ""
                    )
            );
        }
        startArea = "forest 1";
    }

}
