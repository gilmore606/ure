package ure.actions;

import ure.actors.UActor;

/**
 * Actor tries to walk to a new cell from their current cell.
 *
 */
public class UActionWalk extends UAction {

    public static String id = "WALK";

    int xdir, ydir;

    public UActionWalk(UActor theactor, int xd, int yd) {
        actor = theactor;
        xdir = xd;
        ydir = yd;
    }

    @Override
    public float doneBy(UActor actor) {
        actor.walkDir(xdir, ydir);
        float time = super.doneBy(actor);
        time = time * (1f / actor.myCell().terrain().moveSpeed(actor));
        return time;
    }
}
