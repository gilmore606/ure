package ure.ui.modals;

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
        sliderWidget = new WidgetHSlider(this,0,headerWidget.cellh +1,null, 15, _min, _min, _max, true);
        okButton = new WidgetButton(this, sliderWidget.cellw + 1, sliderWidget.row, "[ OK ]", null);
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
        okButton.dismissFlash = true;
        dismiss();
        ((HearModalQuantity)callback).hearModalQuantity(callbackContext, value);
    }
}
