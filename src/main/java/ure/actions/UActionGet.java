package ure.actions;

import ure.actors.UREActor;
import ure.things.UREThing;

public class UActionGet extends UAction {

    public static String id = "GET";

    public static String nothingToGetMsg = "There's nothing here to get.";

    UREThing thing;

    public UActionGet() {
        thing = null;
    }

    public UActionGet(UREThing thething) {
        thing = thething;
    }

    public float doneBy(UREActor actor) {
        if (thing == null) {
            thing = actor.myCell().topThingAt();
            if (thing == null) {
                if (actor.isPlayer())
                    actor.camera.area.commander().printScroll(nothingToGetMsg);
                return 0f;
            }
        }
        actor.tryGetThing(thing);
        return super.doneBy(actor);
    }
}
