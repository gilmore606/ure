package ure.ui.modals;

import ure.ui.modals.widgets.Widget;
import ure.ui.modals.widgets.WidgetListVert;
import ure.ui.modals.widgets.WidgetText;

public class UModalStringPick extends UModal {

    WidgetText headerWidget;
    WidgetListVert choicesWidget;

    public UModalStringPick(String _header, String[] _choices,
                            HearModalStringPick _callback, String _callbackContext) {
        super(_callback, _callbackContext);
        if (_header != null) {
            headerWidget = new WidgetText(this, 0, 0, _header);
            addWidget(headerWidget);
            choicesWidget = new WidgetListVert(this, 0, headerWidget.cellh + 1, _choices);
        } else {
            choicesWidget = new WidgetListVert(this, 0, 0, _choices);
        }
        addWidget(choicesWidget);
        sizeToWidgets();
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
