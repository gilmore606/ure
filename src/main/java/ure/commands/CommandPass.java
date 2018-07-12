package ure.commands;

import ure.actions.UActionPass;
import ure.actors.UPlayer;

public class CommandPass extends UCommand {

    public static String id = "PASS";

    @Override
    public void execute(UPlayer player) {
        player.doAction(new UActionPass(player));
    }
}
