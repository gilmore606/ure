package ure.commands;

import ure.actions.ActionInteract;
import ure.actions.Interactable;
import ure.actors.UActor;
import ure.actors.UPlayer;
import ure.areas.UArea;
import ure.terrain.UTerrain;
import ure.ui.modals.HearModalDirection;
import ure.ui.modals.UModalDirection;

import java.util.ArrayList;

public class CommandInteract extends UCommand implements HearModalDirection {

    public static String id = "INTERACT";

    public static String noTargetMsg = "There's nothing around to interact with.";
    public static String noTargetAtDirectionMsg = "There's nothing there to interact with.";
    public static String askDirectionMsg = "Interact which direction?  (space for here)";

    @Override
    public void execute(UPlayer player) {
        ArrayList<Interactable> targetsHere = findInteractablesAt(player.area(), player.areaX(), player.areaY());
        ArrayList<Interactable> targetsN = findInteractablesAt(player.area(), player.areaX(), player.areaY()-1);
        ArrayList<Interactable> targetsS = findInteractablesAt(player.area(), player.areaX(), player.areaY()+1);
        ArrayList<Interactable> targetsW = findInteractablesAt(player.area(), player.areaX()-1, player.areaY());
        ArrayList<Interactable> targetsE = findInteractablesAt(player.area(), player.areaX()+1, player.areaY());

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
            commander.showModal(new UModalDirection(askDirectionMsg, true, this, ""));
        }
    }

    public void PickInteractTarget(UPlayer player, ArrayList<Interactable> targets) {
        if (targets.size() == 1)
            player.doAction(new ActionInteract(player, targets.get(0)));
        else {
            // TODO: pop up selector to pick target by glyph+name
        }
    }

    public void hearModalDirection(String context, int xdir, int ydir) {
        ArrayList<Interactable> targets = findInteractablesAt(commander.player().area(), commander.player().areaX() + xdir, commander.player().areaY() + ydir);
        if (targets.size() == 0) {
            commander.printScroll(noTargetAtDirectionMsg);
        }
    }

    public ArrayList<Interactable> findInteractablesAt(UArea area, int x, int y) {
        ArrayList<Interactable> targets = new ArrayList<Interactable>();
        UActor actor = area.actorAt(x,y);
        if (actor != null && actor != commander.player() && actor.isInteractable(commander.player()))
            targets.add(actor);
        // TODO: also check Things for interactable, i was lazy
        Interactable terrain = (Interactable)(area.terrainAt(x,y));
        if (terrain != null && terrain.isInteractable(commander.player()))
            targets.add(terrain);
        return targets;
    }
}