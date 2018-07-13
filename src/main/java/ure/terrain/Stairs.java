package ure.terrain;

import ure.actors.UPlayer;
import ure.areas.UArea;
import ure.areas.UCartographer;
import ure.areas.UCell;
import ure.actors.UActor;

/**
 * Stairs is any area-transition terrain, be it a cave mouth, building entrance, or actual stairs.
 * Stairs are tightly integrated with UCartographer to determine cross-area travel and area generation.
 *
 * To build your area map structure, you probably want to put logic into UCartographer and avoid
 * overriding the label methods on Stairs directly.
 *
 */
public class Stairs extends TerrainI implements UTerrain {

    public static final String TYPE = "stairs";

    String label = "";

    int destX, destY;
    boolean onstep = false;
    boolean confirm = true;

    private UCartographer cartographer;

    public String label() {
        /**
         * In general you shouldn't override this; you probably want to setLabel() persistently to
         * change your area routing.
         */
        return label;
    }

    public void setLabel(String thelabel, UCartographer carto) {
        cartographer = carto;
        label = thelabel;
        System.out.println("CARTO : setting stairs dest : " + label);
    }

    public void transportActor(UActor actor) {
        // TODO: cool transition bullshit i dunno
        UArea sourcearea = actor.area();
        UArea destarea = cartographer.getArea(label);
        System.out.println("CARTO : stairs got new area " + destarea.label);
        UCell dest = destarea.findExitTo(sourcearea.label);
        if (dest == null) {
            System.out.println("CARTO : couldn't find back-matching stairs!  going to random space");
            dest = destarea.randomOpenCell(actor);
        }
        actor.moveToCell(destarea, dest.areaX(), dest.areaY());
        if (actor instanceof UPlayer) {
            cartographer.playerLeftArea((UPlayer)actor, sourcearea);
            commander.playerChangedArea(sourcearea, destarea);
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
}
