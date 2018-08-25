package ure.ui.modals;

import ure.actors.Bodypart;
import ure.actors.UActor;
import ure.actors.actions.ActionEquip;
import ure.actors.actions.ActionUnequip;
import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.GLKey;
import ure.things.UThing;
import ure.ui.modals.widgets.*;

import java.util.ArrayList;

public class UModalEquipment extends UModal implements HearModalEquipPick {

    ArrayList<Bodypart> slotsBodyparts;
    ArrayList<UThing> slotsThings;
    ArrayList<UThing> possible;

    WidgetText partNameWidget;
    WidgetThingList listWidget;
    WidgetEntityDetail detailWidget;
    WidgetText possibleWidget;

    public UModalEquipment(UActor actor) {
        super(null, "");
        height = 0;
        slotsBodyparts = new ArrayList<>();
        slotsThings = new ArrayList<>();
        possible = new ArrayList<>();
        fillSlots(actor);

        String partnames = "";
        String lastname = "";
        for (Bodypart part : slotsBodyparts) {
            if (part.name.equals(lastname))
                partnames += " \n";
            else
                partnames += part.name + " \n";
            lastname = part.name;
        }
        partNameWidget = new WidgetText(this, 0, 0, partnames);
        addWidget(partNameWidget);

        listWidget = new WidgetThingList(this, partNameWidget.w + 1, 0, 10, slotsThings.size());
        addWidget(listWidget);

        detailWidget = new WidgetEntityDetail(this, listWidget.x + listWidget.w + 1, 0);
        addWidget(detailWidget);

        possibleWidget = new WidgetText(this, detailWidget.x, detailWidget.y + detailWidget.h + 1, "");
        addWidget(possibleWidget);
        possibleWidget.color = config.getTextGray();
        sizeToWidgets();
        listWidget.setThings(slotsThings);
    }

    void fillSlots(UActor actor) {
        slotsBodyparts.clear();
        slotsThings.clear();
        for (Bodypart bodypart : actor.getBody().getParts()) {
            for (int i=0;i<bodypart.slots;i++) {
                height += 1;
                slotsBodyparts.add(bodypart);
                slotsThings.add(null);
            }
        }
        for (UThing thing : actor.equipment()) {
            int slotsToFill = thing.getEquipSlotCount();
            for (int i=0;i<slotsThings.size();i++) {
                if (slotsToFill > 0 && slotsBodyparts.get(i).name.equals(thing.equipSlots[0]) && slotsThings.get(i) == null) {
                    slotsThings.set(i,thing);
                    slotsToFill--;
                }
            }
        }
    }

    public void pressWidget(Widget widget) {
        if (widget == listWidget) {
            selectSlot();
        }
    }

    public void widgetChanged(Widget widget) {
        if (widget == listWidget) {
            detailWidget.setEntity(listWidget.thing());
            updatePossible();
        }
    }

    public void selectSlot() {
        updatePossible();
        if (possible.size() > 0) {
            int possiblePos = 0;
            for (int i=0;i<possible.size();i++)
                if (possible.get(i) == slotsThings.get(listWidget.selection))
                    possiblePos = i;
            UModalEquipPick emodal = new UModalEquipPick(possible, slotsThings.get(listWidget.selection), this, "equip");
            emodal.setChildPosition(partNameWidget.w + listWidget.iconSpacing, listWidget.selection - possiblePos,this);
            emodal.forceSelection(possiblePos);
            commander.showModal(emodal);
        }
    }

    void updatePossible() {
        possible.clear();
        String posstext = "";
        ArrayList<UThing> equipment = commander.player().getContents().getThings();
        for (UThing thing : equipment) {
            if (thing.fitsOnBodypart(slotsBodyparts.get(listWidget.selection).getName()))
                if (thing == slotsThings.get(listWidget.selection) || !thing.equipped) {
                    possible.add(thing);
                    if (!thing.equipped)
                        posstext += thing.name() + " \n";
                }
        }
        possible.add(null);
        possibleWidget.setText(posstext);
    }

    public void hearModalEquipPick(String context, UThing pick) {
        if (context.equals("unequip")) {
            if (pick != null)
                commander.player().doAction(new ActionUnequip(commander.player(), pick));
        } else {
            if (pick == slotsThings.get(listWidget.selection))
                return;
            else if (slotsThings.get(listWidget.selection) != null)
                commander.player().doAction(new ActionUnequip(commander.player(), slotsThings.get(listWidget.selection)));
            commander.player().doAction(new ActionEquip(commander.player(), pick));
        }
        fillSlots(commander.player());
        widgetChanged(listWidget);
    }
}
