package ure.commands;

import ure.actions.ActionInteract;
import ure.actions.Interactable;
import ure.actors.UActor;
import ure.actors.UPlayer;
import ure.areas.UArea;
import ure.terrain.UTerrain;
import ure.things.UThing;
import ure.ui.modals.HearModalDirection;
import ure.ui.modals.UModalDirection;

import java.util.ArrayList;

public class CommandInteract extends UCommand implements HearModalDirection {

    public static String noTargetMsg = "There's nothing around to interact with.";
    public static String noTargetAtDirectionMsg = "There's nothing there to interact with.";
    public static String askDirectionMsg = "Interact which direction?  (space for here)";

    public CommandInteract() {
        super();
        id = "INTERACT";
    }

    @Override
    public void execute(UPlayer player) {
        if (commander.config.isSmartInteract()) {
            ArrayList<Interactable> targetsHere = findInteractablesAt(player.area(), player.areaX(), player.areaY());
            ArrayList<Interactable> targetsN = findInteractablesAt(player.area(), player.areaX(), player.areaY() - 1);
            ArrayList<Interactable> targetsS = findInteractablesAt(player.area(), player.areaX(), player.areaY() + 1);
            ArrayList<Interactable> targetsW = findInteractablesAt(player.area(), player.areaX() - 1, player.areaY());
            ArrayList<Interactable> targetsE = findInteractablesAt(player.area(), player.areaX() + 1, player.areaY());

            if (targetsHere.size() > 0 && targetsN.size() == 0 && targetsS.size() == 0 && targetsW.size() == 0 && targetsE.size() == 0)
                PickInteractTarget(player, targetsHere);
            else if (targetsHere.size() == 0 && targetsN.size() > 0 && targetsS.size() == 0 && targetsW.size() == 0 && targetsE.size() == 0)
                PickInteractTarget(player, targetsN);
            else if (targetsHere.size() == 0 && targetsN.size() == 0 && targetsS.size() > 0 && targetsW.size() == 0 && targetsE.size() == 0)
                PickInteractTarget(player, targetsS);
            else if (targetsHere.size() == 0 && targetsN.size() == 0 && targetsS.size() == 0 && targetsW.size() > 0 && targetsE.size() == 0)
                PickInteractTarget(player, targetsW);
            else if (targetsHere.size() == 0 && targetsN.size() == 0 && targetsS.size() == 0 && targetsW.size() == 0 && targetsE.size() > 0)
                PickInteractTarget(player, targetsE);
            else if (targetsHere.size() == 0 && targetsN.size() == 0 && targetsS.size() == 0 && targetsW.size() == 0 && targetsE.size() == 0)
                commander.printScroll(noTargetMsg);
            else {
                commander.showModal(new UModalDirection(askDirectionMsg, true, player.cameraX(commander.modalCamera()), player.cameraY(commander.modalCamera()), this, ""));
            }
        } else {
            commander.showModal(new UModalDirection(askDirectionMsg, true, player.cameraX(commander.modalCamera()), player.cameraY(commander.modalCamera()), this, ""));
        }
    }

    public void PickInteractTarget(UPlayer player, ArrayList<Interactable> targets) {
        if (targets.size() == 1) {
            player.doAction(new ActionInteract(player, targets.get(0)));
        } else {
            // TODO: pop up selector to pick target by glyph+name
            player.doAction(new ActionInteract(player, targets.get(0)));
        }
    }

    public void hearModalDirection(String context, int xdir, int ydir) {
        ArrayList<Interactable> targets = findInteractablesAt(commander.player().area(), commander.player().areaX() + xdir, commander.player().areaY() + ydir);
        if (targets.size() == 0) {
            commander.printScroll(noTargetAtDirectionMsg);
        } else {
            PickInteractTarget((UPlayer)commander.player(), targets);
        }
    }

    public ArrayList<Interactable> findInteractablesAt(UArea area, int x, int y) {
        ArrayList<Interactable> targets = new ArrayList<Interactable>();
        UActor actor = area.actorAt(x,y);
        if (actor != null && actor != commander.player() && actor.isInteractable(commander.player()))
            targets.add((Interactable)actor);
        UThing thing = area.cellAt(x,y).topThingAt();
        if (thing != null && thing.isInteractable(commander.player()))
            targets.add((Interactable)thing);
        Interactable terrain = (Interactable)(area.terrainAt(x,y));
        if (terrain != null && terrain.isInteractable(commander.player()))
            targets.add((Interactable)terrain);
        return targets;
    }
}
