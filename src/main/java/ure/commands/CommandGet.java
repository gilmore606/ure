package ure.commands;

import ure.actors.UActor;
import ure.actors.actions.ActionGet;
import ure.actors.UPlayer;
import ure.things.UThing;

import java.util.ArrayList;

public class CommandGet extends UCommand {

    public static final String id = "GET";

    public static String nothingToGetMsg = "There's nothing here to get.";

    public CommandGet() {
        super(id);
    }

    @Override
    public void execute(UPlayer player) {
        ArrayList<UThing> gettables = player.myCell().gettableThingsAt(player);
        if (gettables.isEmpty()) {
            commander.printScroll(nothingToGetMsg);
        } else {
            UThing thing = gettables.get(gettables.size()-1);
            player.doAction(new ActionGet(player, thing));
        }
    }
}
