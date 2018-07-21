package ure.commands;

import ure.actors.UPlayer;

public class CommandEsc extends UCommand {

    public static final String id = "ESC";

    public CommandEsc() {
        super(id);
    }

    @Override
    public void execute(UPlayer player) { }

}
