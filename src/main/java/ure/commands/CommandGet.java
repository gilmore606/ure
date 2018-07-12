package ure.commands;

import ure.actions.UActionGet;
import ure.actors.UPlayer;

public class CommandGet extends UCommand {

    public static String id = "GET";

    @Override
    public void execute(UPlayer player) {
        player.doAction(new UActionGet(player));
    }
}
