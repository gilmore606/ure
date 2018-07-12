package ure.commands;

import ure.actors.UPlayer;
import ure.ui.modals.HearModalDirection;
import ure.ui.modals.UModalDirection;

public class CommandInteract extends UCommand implements HearModalDirection {

    public static String id = "INTERACT";

    @Override
    public void execute(UPlayer player) {
        commander.showModal(new UModalDirection("Interact which direction? (space for here)", true, this, ""));
    }

    public void hearModalDirection(String context, int xdir, int ydir) {
        commander.printScroll("You try to interact but nothing happens.....yet.");
    }
}
