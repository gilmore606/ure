package ure.commands;

import ure.actors.actions.ActionPass;
import ure.actors.UPlayer;

public class CommandPass extends UCommand {

    public static final String id = "PASS";

    public CommandPass() {
        super(id);
    }
    @Override
    public void execute(UPlayer player) {
        player.doAction(new ActionPass(player));
    }
}
