package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.GLKey;
import ure.ui.modals.widgets.Widget;
import ure.ui.modals.widgets.WidgetStringInput;
import ure.ui.modals.widgets.WidgetText;

import static org.lwjgl.glfw.GLFW.*;

public class UModalGetString extends UModal {

    WidgetText headerWidget;
    WidgetStringInput inputWidget;

    public UModalGetString(String _prompt, int _width, int _maxlength, HearModalGetString _callback, String _callbackContext) {
        super(_callback, _callbackContext);

        headerWidget = new WidgetText(this, 0, 0, _prompt);
        addWidget(headerWidget);

        inputWidget = new WidgetStringInput(this, 0, 2, _width, "", _maxlength);
        inputWidget.dismissFlash = true;
        addWidget(inputWidget);

        sizeToWidgets();
    }

    @Override
    public void widgetChanged(Widget widget) {
        if (widget == inputWidget && inputWidget.hitEnter)
            sendInput();
    }

    public void sendInput() {
        dismiss();
        ((HearModalGetString)callback).hearModalGetString(callbackContext, inputWidget.text);
    }
}
