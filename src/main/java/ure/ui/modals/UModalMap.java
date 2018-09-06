package ure.ui.modals;

import ure.areas.UArea;
import ure.math.UColor;
import ure.ui.Icons.Icon;
import ure.ui.modals.widgets.Widget;
import ure.ui.modals.widgets.WidgetButton;
import ure.ui.modals.widgets.WidgetMap;
import ure.ui.modals.widgets.WidgetRadio;

public class UModalMap extends UModal {

    WidgetMap mapWidget;
    WidgetButton zoomInButton, zoomOutButton;
    WidgetRadio labelRadio;
    WidgetButton markerButton;

    UArea area;

    public UModalMap(UArea area, int width, int height) {
        super(null, "");
        this.area = area;

        mapWidget = new WidgetMap(this, 0, 1, width, height-3);
        addWidget(mapWidget);

        zoomInButton = new WidgetButton(this, 0, 0, "[ +Zoom ]", null);
        zoomOutButton = new WidgetButton(this, 5, 0, "[ -Zoom ]", null);
        addWidget(zoomInButton);
        addWidget(zoomOutButton);

        labelRadio = new WidgetRadio(this, 12, 0, "labels", new Icon(9675, UColor.GRAY, null), new Icon(9787, UColor.WHITE, null), true);
        addWidget(labelRadio);

        markerButton = new WidgetButton(this, 17, 0, "[ Set marker ]", null);
        addWidget(markerButton);

        sizeToWidgets();
        setPad(1,1);

        mapWidget.lookAtArea(area);
        mapWidget.moveView(commander.player().areaX(),commander.player().areaY());
    }

    @Override
    public void pressWidget(Widget widget) {
        if (widget == zoomInButton)
            mapWidget.zoomIn();
        else if (widget == zoomOutButton)
            mapWidget.zoomOut();
        else
            super.pressWidget(widget);
    }

    @Override
    public void widgetChanged(Widget widget) {
        if (widget == labelRadio)
            mapWidget.showLabels = !mapWidget.showLabels;
    }
}
