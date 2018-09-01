package ure.editors.vaulted;

import ure.ui.ULight;
import ure.ui.modals.UModal;
import ure.ui.modals.widgets.Widget;
import ure.ui.modals.widgets.WidgetButton;

public class LightEditModal extends UModal {

    VaultedModal vaulted;
    ULight light;
    WidgetButton deleteButton;

    public LightEditModal(VaultedModal vaulted, ULight light) {
        super(null, "");
        this.vaulted = vaulted;
        this.light = light;
        setDimensions(15,10);
        escapable = true;
        if (light.type == ULight.AMBIENT)
            setTitle("ambient light");
        else
            setTitle("point light");

        deleteButton = new WidgetButton(this, 0, 0, "[ Delete ]", null);
        addWidget(deleteButton);
    }

    @Override
    public void pressWidget(Widget widget) {
        if (widget == deleteButton) {
            dismiss();
            vaulted.deleteLight(light);
        }
    }
}

