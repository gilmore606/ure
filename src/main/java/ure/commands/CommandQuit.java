package ure.commands;

import ure.actors.UPlayer;
import ure.ui.modals.HearModalChoices;
import ure.ui.modals.UModalChoices;

import java.util.ArrayList;

public class CommandQuit extends UCommand implements HearModalChoices {

    public static String quitConfirmMsg = "Quit and save progress?";
    public static String quitYes = "Yes";
    public static String quitNo = "No";

    public CommandQuit() {
        super();
        id = "QUIT";
    }

    @Override
    public void execute(UPlayer player) {
        ArrayList<String> choices = new ArrayList<>();
        choices.add(quitYes);
        choices.add(quitNo);
        UModalChoices modal = new UModalChoices(quitConfirmMsg, choices, quitNo, null, this, "quit");
        commander.showModal(modal);
    }

    public void hearModalChoices(String callbackContext, String choice) {
        if (choice.equals(quitYes))
            commander.quitGame();
    }

}
