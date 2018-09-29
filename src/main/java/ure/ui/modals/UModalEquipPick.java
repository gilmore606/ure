package ure.ui.modals;

import ure.things.UThing;
import ure.ui.modals.widgets.Widget;
import ure.ui.modals.widgets.WidgetEntityDetail;
import ure.ui.modals.widgets.WidgetEntityList;

import java.util.ArrayList;

public class UModalEquipPick extends UModal {

    WidgetEntityList listWidget;
    WidgetEntityDetail nowDetailWidget;
    WidgetEntityDetail pickDetailWidget;


    public UModalEquipPick(ArrayList<UThing> _things, UThing _equipped, HearModalEquipPick _callback, String _callbackContext) {
        super(_callback, _callbackContext);

        listWidget = new WidgetEntityList(this, 0, 0, 10, _things.size());
        listWidget.scrollable = false;
        addWidget(listWidget);

        nowDetailWidget = new WidgetEntityDetail(this, 12, 0);
        addWidget(nowDetailWidget);

        pickDetailWidget = new WidgetEntityDetail(this, 12, nowDetailWidget.cellh + 2);
        addWidget(pickDetailWidget);

        sizeToWidgets();

        nowDetailWidget.setEntity(_equipped);
        pickDetailWidget.setEntity(null);
        listWidget.setThings(_things);
    }

    @Override
    public void widgetChanged(Widget widget) {
        if (widget == listWidget) {
            if (listWidget.entity() != nowDetailWidget.entity)
                pickDetailWidget.setEntity(listWidget.entity());
            else
                pickDetailWidget.setEntity(null);
        }
    }

    @Override
    public void pressWidget(Widget widget) {
        if (widget == listWidget) {
            selectEquip();
        }
    }

    public void forceSelection(int i) {
        listWidget.selection = i;
    }

    void selectEquip() {
        dismiss();
        UThing thing = (UThing)listWidget.entity();
        if (thing == nowDetailWidget.entity)
            dismissFrameEnd = 0;
        if (thing != null) {
            ((HearModalEquipPick) callback).hearModalEquipPick(callbackContext, thing);
        } else {
            ((HearModalEquipPick) callback).hearModalEquipPick("unequip", (UThing)nowDetailWidget.entity);
        }
    }
}
