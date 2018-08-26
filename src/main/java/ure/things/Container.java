package ure.things;

import ure.actors.UActor;
import ure.ui.modals.UModalContainer;

/**
 * A thing which can have other things put into it, or taken out of it.
 *
 */
public class Container extends UThing {

    public static final String TYPE = "container";

    public boolean openableBy(UActor actor) {
        return true;
    }

    @Override
    public String useVerb() { return "open"; }

    @Override
    public boolean isUsable(UActor actor) { return true; }

    @Override
    public boolean willAcceptThing(UThing thing) { return true; }

    @Override
    public float useFrom(UActor actor) {
        return openFrom(actor);
    }

    public float openFrom(UActor actor) {
        UModalContainer cmodal = new UModalContainer(this, "You open the " + name() + "...");
        commander.showModal(cmodal);
        return 0f;
    }
}
