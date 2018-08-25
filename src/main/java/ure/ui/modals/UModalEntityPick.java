package ure.ui.modals;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ure.actors.actions.UAction;
import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.Entity;
import ure.sys.GLKey;
import ure.things.UThing;
import ure.ui.modals.widgets.Widget;
import ure.ui.modals.widgets.WidgetEntityDetail;
import ure.ui.modals.widgets.WidgetEntityList;
import ure.ui.modals.widgets.WidgetText;

import java.util.ArrayList;
import java.util.HashMap;

public class UModalEntityPick extends UModal {

    WidgetText headerWidget;
    WidgetEntityList listWidget;
    WidgetEntityDetail detailWidget;

    public UModalEntityPick(String _header, ArrayList<Entity> _entities,
                            boolean _showDetail, HearModalEntityPick _callback, String _callbackContext) {
        super(_callback, _callbackContext);
        headerWidget = new WidgetText(this, 0, 0, _header);
        addWidget(headerWidget);

        listWidget = new WidgetEntityList(this, 0, headerWidget.h, 8, _entities.size());
        addWidget(listWidget);

        if (_showDetail) {
            detailWidget = new WidgetEntityDetail(this, listWidget.w + 1, headerWidget.h);
            addWidget(detailWidget);
        }
        sizeToWidgets();
        listWidget.setEntities(_entities);

    }

    @Override
    public void widgetChanged(Widget widget) {
        if (widget == listWidget)
            if (detailWidget != null)
                detailWidget.setEntity(listWidget.entity());
    }
    @Override
    public void pressWidget(Widget widget) {
        if (widget == listWidget)
            selectEntity();
    }

    public ArrayList<String> deDupeEntities(ArrayList<Entity> sourceEntities) {
        ArrayList<Entity> newent = new ArrayList<>();
        ArrayList<String> displaynames = new ArrayList<>();
        ArrayList<Integer> totals = new ArrayList<>();
        ArrayList<Entity> writeList = sourceEntities;
        int i = 0;
        for (Entity entity : sourceEntities) {
            boolean gotone = false;
            int ni = 0;
            for (Entity nent: newent) {
                if (nent.getName().equals(entity.getName())) {
                    gotone = true;
                } else if (!gotone) {
                    ni++;
                }
            }
            if (!gotone) {
                newent.add(entity);
                totals.add(1);
                i++;
            } else {
                totals.set(ni, totals.get(ni) + 1);
            }
        }
        sourceEntities = newent;
        i = 0;
        writeList.clear();
        for (Entity entity : sourceEntities) {
            int total = totals.get(i);
            if (total > 1)
                displaynames.add(Integer.toString(totals.get(i)) + " " + entity.getPlural());
            else
                displaynames.add(entity.name());
            i++;
            writeList.add(entity);
        }
        return displaynames;
    }

    void selectEntity() {
        dismiss();
        ((HearModalEntityPick) callback).hearModalEntityPick(callbackContext, listWidget.entity());
    }
}
