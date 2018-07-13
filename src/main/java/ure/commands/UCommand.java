package ure.commands;

import ure.sys.Injector;
import ure.sys.UCommander;
import ure.actors.UPlayer;

import javax.inject.Inject;

/**
 * UCommand represents a player-initiated command, which may or may not cause a game action or a UI popup.
 *
 * UCommand instances are only created once when Commander starts up, and then passed around as commands are
 * executed.
 *
 */
public abstract class UCommand {

    @Inject
    UCommander commander;

    public static String id = "";

    /**
     * Your subclass should super() down to this constructor if you override.
     *
     */
    public UCommand() {
        Injector.getAppComponent().inject(this);
    }

    /**
     * Override execute to actually do what your command does (cause player to do an Action, open a UModal, etc).
     *
     * @param player
     */
    public void execute(UPlayer player) {

    }
}
