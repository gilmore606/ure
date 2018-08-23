package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.GLKey;
import ure.ui.modals.widgets.Widget;
import ure.ui.modals.widgets.WidgetButton;
import ure.ui.modals.widgets.WidgetHSlider;
import ure.ui.modals.widgets.WidgetText;

public class UModalQuantity extends UModal {

    WidgetText headerWidget;
    WidgetHSlider sliderWidget;
    WidgetButton okButton;

    public UModalQuantity(String _prompt, int _min, int _max, HearModalQuantity _callback, String _callbackContext) {
        super(_callback,_callbackContext);
        headerWidget = new WidgetText(this,0,0,_prompt);
        sliderWidget = new WidgetHSlider(this,0,headerWidget.h+1,15, _min, _min, _max, true);
        okButton = new WidgetButton(this, sliderWidget.w + 1, sliderWidget.y, "[ OK ]", null);
        addWidget(headerWidget);
        addWidget(sliderWidget);
        addWidget(okButton);
        sizeToWidgets();
    }

    @Override
    public void pressWidget(Widget widget) {
        if (widget == okButton)
            sendInput(sliderWidget.value);
    }

    public void sendInput(int value) {
        dismiss();
        ((HearModalQuantity)callback).hearModalQuantity(callbackContext, value);
    }
}
