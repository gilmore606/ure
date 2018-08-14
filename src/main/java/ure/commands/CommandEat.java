package ure.commands;

import ure.actors.UPlayer;
import ure.actors.actions.ActionUse;
import ure.actors.actions.UAction;
import ure.sys.Entity;
import ure.things.Food;
import ure.things.UThing;
import ure.ui.modals.HearModalEntityPick;
import ure.ui.modals.UModalEntityPick;

import java.util.ArrayList;

public class CommandEat extends CommandUseVerb {

    public static final String id = "EAT";

    public CommandEat() {
        super(id);
        verb = "eat";
        noTargetsMsg = "You don't have anything to eat.";
        whichDialog = "Eat which?";
    }
}
