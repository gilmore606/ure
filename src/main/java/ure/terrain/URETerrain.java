package ure.terrain;

import ure.UCell;
import ure.UColor;
import ure.UREActor;

/**
 * Created by gilmore on 6/20/2018.
 *
 * A type of terrain which can be in a cell.
 *
 */

public abstract class URETerrain {

    public static final String TYPE = "";

    public String name;
    public String type;
    public String walkmsg = "";
    public char filechar;
    public char icon;
    public String variants;

    public int[] fgcolor;
    public int[] bgcolor;

    public UColor fgColor;
    public UColor bgColor;

    public boolean passable;
    public boolean opaque;
    public boolean glow = false;
    public float sunDefault;

    public boolean isPassable() {
        return passable;
    }
    public boolean isPassable(UREActor actor) { return isPassable(); }
    public boolean isOpaque() {
        return opaque;
    }

    public void initialize() {
        fgColor = new UColor(fgcolor[0],fgcolor[1],fgcolor[2]);
        bgColor = new UColor(bgcolor[0],bgcolor[1],bgcolor[2]);
    }

    public char icon() {
        return icon;
    }

    public char icon(int x, int y) {
        if (variants == null)
            return icon();
        int seed = (x * y * 19 + 1883) / 74;
        int period = variants.length();
        return variants.charAt(seed % period);
    }

    public void moveTriggerFrom(UREActor actor, UCell cell) {
        if (isPassable(actor)) {
            System.out.println("terrain is moving actor");
            actor.moveToCell(cell.areaX(), cell.areaY());
        }
    }

    public boolean preventMoveFrom(UREActor actor) {
        return false;
    }
}
