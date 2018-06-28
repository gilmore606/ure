package ure.terrain;

import ure.UCell;
import ure.UREActor;

public class Door extends URETerrain {

    public static final String TYPE = "door";

    public String openmsg = "The door opens.";
    public String closemsg = "The door closes.";
    public char iconopen;

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
    public char icon() {
        if (isOpen()) {
            return iconopen;
        }
        return super.icon();
    }
    @Override
    public void moveTriggerFrom(UREActor actor, UCell cell) {
        if (!isOpen()) {
            openedBy(actor, cell);
        } else {
            super.moveTriggerFrom(actor, cell);
        }
    }

    public void openedBy(UREActor actor, UCell cell) {
        printScroll(openmsg, cell);
        isOpen = true;
    }

}
