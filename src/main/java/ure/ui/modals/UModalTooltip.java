package ure.ui.modals;

import ure.math.UColor;
import ure.sys.Entity;
import ure.sys.UConfig;
import ure.ui.modals.UModal;
import ure.ui.modals.widgets.WidgetEntityDetail;
import ure.ui.modals.widgets.WidgetEntityTip;

public class UModalTooltip extends UModal {

    Entity entity;

    WidgetEntityTip entityWidget;

    public UModalTooltip(Entity entity) {
        super(null, "");
        this.entity = entity;
        entityWidget = new WidgetEntityTip(this, 0, 0, entity);
        addWidget(entityWidget);
        sizeToWidgets();
    }

    @Override
    public void drawFrame() {
        bgColor.setAlpha(zoom / 2f);
        int xpos = 0;
        int ypos = 0;
        renderer.drawRect(xpos - gw()/2, ypos - gh()/2,  (cellw+1)*gw(),(cellh+1)*gh(), bgColor);
    }

    @Override
    public int zoomFrames() {
        return config.getModalZoomFrames() * 2;
    }
}
