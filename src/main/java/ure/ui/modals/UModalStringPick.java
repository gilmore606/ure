package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.GLKey;
import ure.ui.modals.widgets.Widget;
import ure.ui.modals.widgets.WidgetListVert;
import ure.ui.modals.widgets.WidgetText;

import java.util.ArrayList;

public class UModalStringPick extends UModal {

    WidgetText headerWidget;
    WidgetListVert choicesWidget;

    public UModalStringPick(String _header, String[] _choices,
                            HearModalStringPick _callback, String _callbackContext) {
        super(_callback, _callbackContext);

        headerWidget = new WidgetText(this,0,0,_header);
        addWidget(headerWidget);
        choicesWidget = new WidgetListVert(this,0, headerWidget.h+1, _choices);
        addWidget(choicesWidget);
        sizeToWidgets();
        centerWidget(headerWidget);
        centerWidget(choicesWidget);
    }

    @Override
    public void pressWidget(Widget widget) {
        if (widget == choicesWidget)
            selectChoice(choicesWidget.choice());
    }

    public void selectChoice(String choice) {
        choicesWidget.dismissFlash = true;
        dismiss();
        ((HearModalStringPick)callback).hearModalStringPick(callbackContext, choice);
    }
}
