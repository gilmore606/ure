package ure.actions;

import ure.actors.UActor;

/**
 * Actor tries to walk to a new cell from their current cell.
 *
 */
public class ActionWalk extends UAction {

    public static String id = "WALK";

    int xdir, ydir;

    /**
     *
     * @param theactor
     * @param xd X direction to walk (-1, 0, 1)
     * @param yd Y direction to walk (-1, 0, 1)
     */
    public ActionWalk(UActor theactor, int xd, int yd) {
        actor = theactor;
        xdir = xd;
        ydir = yd;
    }

    @Override
    public void doMe() {
        actor.walkDir(xdir, ydir);
    }

    @Override
    public float timeCost() {
        float time = super.timeCost();
        time = time * (1f / (actor.myCell().terrain().moveSpeed(actor) * actor.moveSpeed()));
        return time;
    }
}
