package ure.commands;

import ure.actors.UPlayer;
import ure.ui.modals.HearModalChoices;
import ure.ui.modals.UModalChoices;

import java.util.ArrayList;

public class CommandQuit extends UCommand implements HearModalChoices {

    public static final String id = "QUIT";

    public static String quitConfirmMsg = "Quit to main menu?\nAll progress will be saved.";

    public CommandQuit() {
        super(id);
    }

    @Override
    public void execute(UPlayer player) {
        UModalChoices modal = new UModalChoices(quitConfirmMsg, new String[]{"Yes", "No"}, true, null, this, "quit");
        commander.showModal(modal);
    }

    public void hearModalChoices(String callbackContext, String choice) {
        if (choice.equals("Yes"))
            commander.quitToTitle();
    }

}
