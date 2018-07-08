package ure.actions;

import ure.actors.UActor;
import ure.things.UThing;

public class UActionGet extends UAction {

    public static String id = "GET";

    public static String nothingToGetMsg = "There's nothing here to get.";

    UThing thing;

    public UActionGet() {
        thing = null;
    }

    public UActionGet(UThing thething) {
        thing = thething;
    }

    public void doFor(UActor actor) {
        if (thing == null) {
            thing = actor.myCell().topThingAt();
            if (thing == null) {
                if (actor.isPlayer())
                    actor.camera.area.commander().printScroll(nothingToGetMsg);
            }
        }
        actor.tryGetThing(thing);
    }
}
