package ure.ui.modals;

import ure.areas.UArea;
import ure.math.UColor;
import ure.ui.Icons.Icon;
import ure.ui.modals.widgets.Widget;
import ure.ui.modals.widgets.WidgetButton;
import ure.ui.modals.widgets.WidgetMap;

public class UModalMap extends UModal {

    WidgetMap mapWidget;
    WidgetButton zoomInButton, zoomOutButton;
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

        markerButton = new WidgetButton(this, 12, 0, "[ Set marker ]", null);
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
    }


    public void drawContentsucks() {
        for (int x=0;x<cellw*2;x++) {
            for (int y=0;y<cellh*2;y++) {
                int areax = (int)(area.xsize*fx(x));
                int areay = (int)(area.ysize*fy(y));
                if (area.cellAt(areax,areay).isSeen()) {
                    UColor c = area.terrainAt(areax, areay).icon().bgColor();
                    renderer.drawRect((int) (x * gw() * 0.5f), (int) (y * gh() * 0.5f), (int) (gw() * 0.5f), (int) (gh() * 0.5f), c);
                }
            }
        }
        commander.player().icon().draw(gw()*areaToMapX(commander.player().areaX())/2,gh()*areaToMapY(commander.player().areaY())/2);
        for (int x=0;x<area.xsize;x++) {
            for (int y=0;y<area.ysize;y++) {
                Icon icon = area.cellAt(x,y).mapIcon();
                if (icon != null) {
                    icon.setAnimate(false);
                    icon.draw(gw() * areaToMapX(x) / 2, gh() * areaToMapY(y) / 2);
                    icon.setAnimate(true);
                }
            }
        }
    }

    int areaToMapX(int x) {
        return (int)(((float)x/(float)area.xsize) * cellw*2);
    }
    int areaToMapY(int y) {
        return (int)(((float)y/(float)area.ysize) * cellh*2);
    }
    float fx(int x) {
        return (float)x/(float)(cellw*2);
    }
    float fy(int y) {
        return (float)y/(float)(cellh*2);
    }
}
