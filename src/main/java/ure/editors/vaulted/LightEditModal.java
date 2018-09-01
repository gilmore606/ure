package ure.editors.vaulted;

import ure.math.UColor;
import ure.ui.ULight;
import ure.ui.modals.UModal;
import ure.ui.modals.widgets.Widget;
import ure.ui.modals.widgets.WidgetButton;
import ure.ui.modals.widgets.WidgetHSlider;

public class LightEditModal extends UModal {

    VaultedModal vaulted;
    ULight light;
    WidgetHSlider rSlider,gSlider,bSlider;
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

        rSlider = new WidgetHSlider(this,0,0,8,light.getColor().iR(),0,255,false);
        gSlider = new WidgetHSlider(this,0,1,8,light.getColor().iG(),0,255,false);
        bSlider = new WidgetHSlider(this,0,2,8,light.getColor().iB(),0,255,false);
        rSlider.color = new UColor(light.getColor().fR(),0f,0f);
        gSlider.color = new UColor(0f,light.getColor().fG(),0f);
        bSlider.color = new UColor(0f,0f,light.getColor().fB());
        addWidget(rSlider);
        addWidget(gSlider);
        addWidget(bSlider);
        deleteButton = new WidgetButton(this, 0, 4, "[ Delete ]", null);
        addWidget(deleteButton);
    }

    @Override
    public void widgetChanged(Widget widget) {
        if (widget == rSlider) {
            rSlider.color.setR(rSlider.value);
            light.getColor().setR(rSlider.value);
        } else if (widget == gSlider) {
            gSlider.color.setG(gSlider.value);
            light.getColor().setG(gSlider.value);
        } else if (widget == bSlider) {
            bSlider.color.setB(bSlider.value);
            light.getColor().setB(bSlider.value);
        }
    }

    @Override
    public void pressWidget(Widget widget) {
        if (widget == deleteButton) {
            dismiss();
            vaulted.deleteLight(light);
        }
    }
}

