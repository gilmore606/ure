package ure.commands;

import ure.actors.UPlayer;

public class CommandDrop extends UCommand {

    public static String id = "DROP";

    @Override
    public void execute(UPlayer player) {
        // player.doAction(new UActionDrop(player));
    }
}
