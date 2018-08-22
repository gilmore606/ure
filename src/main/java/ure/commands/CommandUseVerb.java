package ure.commands;

import ure.actors.UPlayer;
import ure.actors.actions.ActionUse;
import ure.sys.Entity;
import ure.things.UThing;
import ure.ui.modals.HearModalEntityPick;
import ure.ui.modals.UModalEntityPick;

import java.util.ArrayList;

public abstract class CommandUseVerb extends UCommand implements HearModalEntityPick {

    public static final String id = "USEVERB";

    public String noTargetsMsg = "You don't have anything you can do that with.";
    public String whichDialog = "Which?";

    public String verb;
    public boolean useOnGround = true;

    public CommandUseVerb(String i) { super(i); }

    @Override
    public void execute(UPlayer player) {
        ArrayList<Entity> targets = findTargets(player);
        if (targets.size() < 1) commander.printScroll(noTargetsMsg);
        else if (targets.size() == 1) doExecute(player, (UThing)targets.get(0));
        else {
            UModalEntityPick modal = new UModalEntityPick(whichDialog, targets, true, true, false, this, verb);
            commander.showModal(modal);
        }
    }

    public ArrayList<Entity> findTargets(UPlayer player) {
        ArrayList<Entity> targets = new ArrayList<>();
        for (UThing thing : player.getContents().getThings())
            if (thing.useVerb().equals(verb)) targets.add(thing);
        if (useOnGround) {
            for (UThing thing : player.getLocation().things())
                if (thing.useVerb().equals(verb)) targets.add(thing);
        }
        return targets;
    }

    void doExecute(UPlayer player, UThing target) {
        ActionUse action = new ActionUse(player, target);
        player.doAction(action);
    }

    public void hearModalEntityPick(String context, Entity selection) {
        doExecute((UPlayer)commander.player(), (UThing)selection);
    }
}
