package ure.terrain;

import ure.areas.UCell;
import ure.actors.UActor;

/**
 * Doors can be opened by walking into them, closed by interaction, and block movement and light
 * when closed.
 *
 */
public class Door extends TerrainI implements UTerrain {

    public static final String TYPE = "door";

    protected String openmsg = "The door opens.";
    protected String closemsg = "The door closes.";
    protected char glyphopen;
    protected boolean openOnMove = true;

    boolean open;

    public boolean isOpen() { return open; }

    @Override
    public boolean isPassable() {
        return isOpen();
    }
    @Override
    public boolean isOpaque() {
        return !isOpen();
    }

    /**
     * Do I open if actor walks into me?
     * Override this to make doors un-openable in some conditions.
     */
    public boolean openOnMove(UActor actor) {
        return openOnMove && canBeOpenedBy(actor);
    }

    @Override
    public char getGlyph() {
        if (isOpen()) {
            return glyphopen;
        }
        return super.getGlyph();
    }

    @Override
    public void moveTriggerFrom(UActor actor, UCell cell) {
        if (!isOpen() && openOnMove(actor)) {
            openedBy(actor, cell);
        } else {
            super.moveTriggerFrom(actor, cell);
        }
    }

    /**
     * Can actor open me?
     *
     * @param actor
     * @return
     */
    public boolean canBeOpenedBy(UActor actor) {
        if (isOpen())
            return false;
        return true;
    }

    /**
     * Can actor close me?
     *
     * @param actor
     * @return
     */
    public boolean canBeClosedBy(UActor actor) {
        if (!isOpen())
            return false;
        return true;
    }

    /**
     * Override this to do things when someone opens the door.
     * @param actor Can be null if the door opened by mysterious means.
     */
    public void openedBy(UActor actor, UCell cell) {
        printScroll(openmsg, cell);
        open = true;
    }

    /**
     * Override this to do things when someone closes the door.
     * @param actor Can be null if the door closed by mysterious means.
     * @param cell
     */
    public void closedBy(UActor actor, UCell cell) {
        printScroll(closemsg, cell);
        open = false;
    }

    @Override
    public boolean isInteractable(UActor actor) {
        if (actor.myCell() == cell)
            return false;
        return true;
    }

    @Override
    public float interactionFrom(UActor actor) {
        if (!isOpen() && canBeOpenedBy(actor)) {
            openedBy(actor, cell);
        } else if (isOpen() && canBeClosedBy(actor)) {
            closedBy(actor, cell);
        }
        return 1f;
    }

    public String getOpenmsg() {
        return openmsg;
    }

    public void setOpenmsg(String openmsg) {
        this.openmsg = openmsg;
    }

    public String getClosemsg() {
        return closemsg;
    }

    public void setClosemsg(String closemsg) {
        this.closemsg = closemsg;
    }

    public char getGlyphopen() {
        return glyphopen;
    }

    public void setGlyphopen(char glyphopen) {
        this.glyphopen = glyphopen;
    }

    public boolean isOpenOnMove() {
        return openOnMove;
    }

    public void setOpenOnMove(boolean openOnMove) {
        this.openOnMove = openOnMove;
    }
}
