package ure.terrain;

import ure.areas.UCell;
import ure.actors.UActor;

public class Door extends TerrainI implements UTerrain {

    public static final String TYPE = "door";

    public String openmsg = "The door opens.";
    public String closemsg = "The door closes.";
    public char glyphopen;

    boolean isOpen;

    public boolean isOpen() { return isOpen; }

    @Override
    public boolean isPassable() {
        return isOpen();
    }
    @Override
    public boolean isOpaque() {
        return !isOpen();
    }

    @Override
    public char glyph() {
        if (isOpen()) {
            return glyphopen;
        }
        return super.glyph();
    }
    @Override
    public void moveTriggerFrom(UActor actor, UCell cell) {
        if (!isOpen()) {
            openedBy(actor, cell);
        } else {
            super.moveTriggerFrom(actor, cell);
        }
    }

    public void openedBy(UActor actor, UCell cell) {
        printScroll(openmsg, cell);
        isOpen = true;
    }

}
