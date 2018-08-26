package ure.ui.modals;

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

        bagWidget.setThings(container.things());
        inventoryWidget.setThings(commander.player().things());
    }

    public void widgetChanged(Widget widget) {
        if (widget == bagWidget) {
            detailWidget.setEntity(bagWidget.entity());
        } else if (widget == inventoryWidget) {
            detailWidget.setEntity(inventoryWidget.entity());
        }
    }
}
