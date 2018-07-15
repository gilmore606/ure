package ure.examplegame;

import ure.actors.UActor;
import ure.areas.*;

public class ExampleCartographer extends UCartographer {


    public ExampleCartographer() {
        super();
        //addRegion(new URegion("forest", "Mystic Forest", new Class<ULandscaper>[]{ExampleForestScaper.class},
        //            new String[]{"start"}, 250, 250, 1, "", ""));
        addRegion(new URegion("cavern-1", "Caverns of fear", new Class[]{ExampleCaveScaper.class},
                        new String[]{"start"}, 60, 60, 5, "cave entrance", "cave exit"));
    }
}
