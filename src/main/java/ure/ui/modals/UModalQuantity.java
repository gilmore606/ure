package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.GLKey;

public class UModalQuantity extends UModal {

    String[] prompt;
    boolean escapable;

    int min, max, count;
    int numberX;
    int barwidth;

    WidgetText headerWidget;
    WidgetHSlider sliderWidget;

    public UModalQuantity(String _prompt, int _min, int _max, HearModalQuantity _callback, String _callbackContext) {
        super(_callback,_callbackContext);
        headerWidget = new WidgetText(0,0,_prompt);
        sliderWidget = new WidgetHSlider(0,headerWidget.h+1,15, _min, _max, true);
        addCenteredWidget(headerWidget);
        addCenteredWidget(sliderWidget);
        sizeToWidgets();
    }

    public void sendInput() {
        dismiss();
        ((HearModalQuantity)callback).hearModalQuantity(callbackContext, count);
    }
}
