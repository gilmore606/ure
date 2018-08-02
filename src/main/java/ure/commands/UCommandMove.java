package ure.commands;

import ure.actors.actions.ActionWalk;
import ure.actors.UPlayer;

public abstract class UCommandMove extends UCommand {

    public static String id = "M";

    public int xdir, ydir;
    boolean latch;

    public UCommandMove(String id, int _xdir, int _ydir, boolean _latch) {
        super(id);
        xdir = _xdir;
        ydir = _ydir;
        latch = _latch;
    }

    @Override
    public void execute(UPlayer player) {
        if (latch)
            commander.setMoveLatch(xdir, ydir);
        player.doAction(new ActionWalk(player, xdir, ydir));
    }
}
