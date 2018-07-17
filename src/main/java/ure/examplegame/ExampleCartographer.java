package ure.examplegame;

import ure.areas.*;

public class ExampleCartographer extends UCartographer {


    public ExampleCartographer() {
        super();
        addRegion(new ExampleRegionForest("forest", "Mystic Forest", new ULandscaper[]{new ExampleForestScaper()},
                    new String[]{"start"}, 200, 200, 1, "", ""));
        startArea = "forest 1";
    }

}
