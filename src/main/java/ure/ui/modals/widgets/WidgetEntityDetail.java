package ure.ui.modals.widgets;

import ure.math.UColor;
import ure.sys.Entity;
import ure.ui.modals.UModal;

import java.util.ArrayList;

public class WidgetEntityDetail extends Widget {

    public Entity entity;

    public WidgetEntityDetail(UModal modal, int x, int y) {
        super(modal);
        setClipsToBounds(true);
        setDimensions(x,y,10,6);
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void drawMe() {
        if (entity != null) {
            if (entity.icon() != null)
                drawIcon(entity.icon(), 0, 0);
            drawString(entity.name(), 2, 0);
            ArrayList<String> details = entity.UIdetails(modal.callbackContext);
            int linepos = 2;
            for (String line : details) {
                drawString(line, 0, linepos, grayColor());
                linepos++;
            }
        }
    }

}
