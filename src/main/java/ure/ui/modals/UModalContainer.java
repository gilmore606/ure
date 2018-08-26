package ure.ui.modals;

import ure.actors.actions.ActionDrop;
import ure.actors.actions.ActionGet;
import ure.things.UContainer;
import ure.things.UThing;
import ure.ui.modals.widgets.*;

import java.util.ArrayList;

public class UModalContainer extends UModal {

    UContainer container;

    WidgetText headerWidget;
    WidgetEntityList bagWidget;
    WidgetEntityDetail detailWidget;
    WidgetEntityList inventoryWidget;
    WidgetButton takeAllButton;

    public UModalContainer(UContainer container, String header) {
        super(null, "");
        this.container = container;

        headerWidget = new WidgetText(this, 0, 0, header);
        addWidget(headerWidget);
        bagWidget = new WidgetEntityList(this, 0, 2, 10, 10);
        addWidget(bagWidget);
        detailWidget = new WidgetEntityDetail(this, bagWidget.cellw+1, 2);
        addWidget(detailWidget);
        takeAllButton = new WidgetButton(this, detailWidget.col, detailWidget.row + detailWidget.cellh + 1, "[ Take all -> ]", null);
        addWidget(takeAllButton);
        inventoryWidget = new WidgetEntityList(this, detailWidget.col + detailWidget.cellw + 1, 2, 10, 10);
        addWidget(inventoryWidget);

        sizeToWidgets();
        updateLists();
    }

    void updateLists() {
        bagWidget.setThings(filterMovables(container.things()));
        inventoryWidget.setThings(filterMovables(commander.player().things()));
    }
    ArrayList<UThing> filterMovables(ArrayList<UThing> source) {
        ArrayList<UThing> things = new ArrayList<>();
        for (UThing thing : source) {
            if (!thing.equipped && thing != container) {
                things.add(thing);
            }
        }
        return things;
    }

    public void widgetChanged(Widget widget) {
        if (widget == bagWidget) {
            detailWidget.setEntity(bagWidget.entity());
        } else if (widget == inventoryWidget) {
            detailWidget.setEntity(inventoryWidget.entity());
        }
    }

    public void pressWidget(Widget widget) {
        if (widget == inventoryWidget) {
            UThing dropThing = (UThing)inventoryWidget.entity();
            commander.player().doAction(new ActionDrop(commander.player(), dropThing, container));
            updateLists();
        } else if (widget == bagWidget) {
            UThing getThing = (UThing)bagWidget.entity();
            commander.player().doAction(new ActionGet(commander.player(), getThing));
            updateLists();
        } else if (widget == takeAllButton) {
            for (UThing thing : bagWidget.getThings()) {
                commander.player().doAction(new ActionGet(commander.player(), thing));
            }
            updateLists();
        }
    }
}
