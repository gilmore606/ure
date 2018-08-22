package ure.terrain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.eventbus.EventBus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ure.actors.UPlayer;
import ure.areas.UArea;
import ure.areas.UCell;
import ure.actors.UActor;
import ure.sys.events.PlayerChangedAreaEvent;
import ure.sys.Injector;
import ure.ui.modals.HearModalChoices;
import ure.ui.modals.UModalChoices;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * Stairs is any area-transition terrain, be it a cave mouth, building entrance, or actual stairs.
 * Stairs are tightly integrated with UCartographer to determine cross-area travel and area generation.
 *
 * To build your area map structure, you probably want to put logic into UCartographer and avoid
 * overriding the label methods on Stairs directly.
 *
 */
public class Stairs extends UTerrain implements HearModalChoices {

    public static final String TYPE = "stairs";

    protected String label = "";

    protected int destX;
    protected int destY;
    protected boolean onstep = true;
    protected boolean confirm = true;

    @Inject
    @JsonIgnore
    EventBus bus;

    private Log log = LogFactory.getLog(Stairs.class);

    public Stairs() {
        Injector.getAppComponent().inject(this); // This will inject superclass fields too, so no need to call super()
    }

    public String getLabel() {
        /**
         * In general you shouldn't override this; you probably want to setLabel() persistently to
         * change your area routing.
         */
        return label;
    }

    public void setLabel(String thelabel) {
        label = thelabel;
        log.debug("setting stairs dest : " + label);
    }

    public void transportActor(UActor actor) {
        // TODO: cool transition bullshit i dunno
        UArea sourcearea = actor.area();
        log.debug("heading to " + label);
        UArea destarea = commander.cartographer.getArea(label);
        log.debug("got new area " + destarea.getLabel());
        UCell dest = destarea.findExitTo(sourcearea.getLabel());
        if (dest == null) {
            log.warn("couldn't find back-matching stairs!  going to random space");
            dest = destarea.randomOpenCell(actor);
        }
        actor.moveToCell(destarea, dest.areaX(), dest.areaY());
        if (actor instanceof UPlayer) {
            bus.post(new PlayerChangedAreaEvent((UPlayer)actor, this, sourcearea, destarea));
        }
    }

    @Override
    public boolean isInteractable(UActor actor) {
        if (commander.config.isInteractStairs())
            return true;
        return false;
    }

    @Override
    public float interactionFrom(UActor actor) {
        transportActor(actor);
        return 0f;
    }

    @Override
    public void walkedOnBy(UActor actor, UCell cell) {
        if (actor instanceof UPlayer && isOnstep()) {
            if (!isConfirm()) {
                transportActor(actor);
            } else {
                askConfirm();
            }
        } else if (isOnstep()) {
            if (commander.cartographer.areaIsActive(label))
                transportActor(actor);
            else
                log.warn(name + " tried to enter " + label + ", but it's not active");
        }
    }

    public void askConfirm() {
        String confirmMsg = "Travel to " + commander.cartographer.describeLabel(label) + "?";
        confirmMsg = walkmsg + "\n" + confirmMsg;
        UModalChoices modal = new UModalChoices(confirmMsg, new String[]{"Yes","No"},true,
                null, this, "travel");
        commander.showModal(modal);
    }

    public void hearModalChoices(String context, String choice) {
        if (choice.equals("Yes"))
            transportActor(commander.player());
    }


    public int getDestX() {
        return destX;
    }

    public void setDestX(int destX) {
        this.destX = destX;
    }

    public int getDestY() {
        return destY;
    }

    public void setDestY(int destY) {
        this.destY = destY;
    }

    public boolean isOnstep() {
        return onstep;
    }

    public void setOnstep(boolean onstep) {
        this.onstep = onstep;
    }

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }
}
