package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.GLKey;
import ure.ui.modals.widgets.Widget;
import ure.ui.modals.widgets.WidgetChoices;
import ure.ui.modals.widgets.WidgetText;

import java.util.ArrayList;

/**
 * ModalChoices gives the user a few plaintext choices and returns the one picked.
 *
 */
public class UModalChoices extends UModal {

    UColor tempHiliteColor;
    UColor flashColor;

    WidgetText headerWidget;
    WidgetChoices choicesWidget;

    public UModalChoices(String _prompt, String[] _choices, HearModalChoices _callback, String _callbackContext) {
        super(_callback, _callbackContext);
        headerWidget = new WidgetText(this,0,0, _prompt);
        choicesWidget = new WidgetChoices(this,0, headerWidget.h + 1, _choices);
        addWidget(headerWidget);
        addWidget(choicesWidget);
        sizeToWidgets();
        centerWidget(headerWidget);
        centerWidget(choicesWidget);
        dismissFrameEnd = 8;
        tempHiliteColor = config.getHiliteColor();
        flashColor = new UColor(config.getHiliteColor());
        flashColor.setAlpha(1f);
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

    @Override
    public void animationTick() {
        if (dismissed) {
            if ((dismissFrames % 2) == 0) {
                tempHiliteColor = commander.config.getModalBgColor();
            } else {
                tempHiliteColor = flashColor;
            }
        }
        super.animationTick();
    }
}
