package ure.ui.modals;

import ure.ui.modals.widgets.Widget;
import ure.ui.modals.widgets.WidgetChoices;
import ure.ui.modals.widgets.WidgetText;

/**
 * ModalChoices gives the user a few plaintext choices and returns the one picked.
 *
 */
public class UModalChoices extends UModal {


    WidgetText headerWidget;
    WidgetChoices choicesWidget;

    public UModalChoices(String _prompt, String[] _choices, HearModalChoices _callback, String _callbackContext) {
        super(_callback, _callbackContext);
        headerWidget = new WidgetText(this,0,0, _prompt);
        choicesWidget = new WidgetChoices(this,0, headerWidget.cellh + 1, _choices);
        choicesWidget.dismissFlash = true;
        addWidget(headerWidget);
        addWidget(choicesWidget);
        sizeToWidgets();
        centerWidget(headerWidget);
        centerWidget(choicesWidget);
    }

    @Override
    public void pressWidget(Widget widget) {
        if (widget == choicesWidget)
            pickSelection(choicesWidget.choice());
    }

    public void pickSelection(String selection) {
        dismiss();
        ((HearModalChoices)callback).hearModalChoices(callbackContext, selection);
    }
}
