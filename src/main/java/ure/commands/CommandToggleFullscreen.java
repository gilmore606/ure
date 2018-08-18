package ure.commands;

import ure.actors.UPlayer;
import ure.actors.actions.ActionPass;

public class CommandToggleFullscreen extends UCommand {

    public static final String id = "FULLSCREEN";

    public CommandToggleFullscreen() {
        super(id);
    }
    @Override
    public void execute(UPlayer player) {
        commander.toggleFullscreen();
    }
}
