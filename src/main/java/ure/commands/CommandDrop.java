package ure.commands;

import ure.actors.UPlayer;

public class CommandDrop extends UCommand {

    public CommandDrop() {
        super();
        id = "DROP";
    }

    @Override
    public void execute(UPlayer player) {
        // player.doAction(new UActionDrop(player));
    }
}
