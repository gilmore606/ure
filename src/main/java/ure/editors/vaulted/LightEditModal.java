package ure.editors.vaulted;

import ure.math.UColor;
import ure.ui.ULight;
import ure.ui.modals.UModal;
import ure.ui.modals.widgets.*;

public class LightEditModal extends UModal {

    VaultedModal vaulted;
    ULight light;
    WidgetHSlider rSlider,gSlider,bSlider;
    WidgetDropdown flickerTypeWidget;
    WidgetHSlider flickerSpeedSlider;
    WidgetHSlider flickerIntensitySlider;
    WidgetHSlider flickerOffsetSlider;
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

        rSlider = new WidgetHSlider(this,0,0,"R:", 8,light.getColor().iR(),0,255,true);
        gSlider = new WidgetHSlider(this,0,1,"G:", 8,light.getColor().iG(),0,255,true);
        bSlider = new WidgetHSlider(this,0,2,"B: ",8,light.getColor().iB(),0,255,true);
        rSlider.color = new UColor(light.getColor().fR(),0f,0f);
        gSlider.color = new UColor(0f,light.getColor().fG(),0f);
        bSlider.color = new UColor(0f,0f,light.getColor().fB());
        addWidget(rSlider);
        addWidget(gSlider);
        addWidget(bSlider);

        flickerTypeWidget = new WidgetDropdown(this, 5, 4, new String[]{"None", "Fire", "Pulse", "Fritz", "Blink", "Compulse"}, light.getFlickerStyle());
        addWidget(new WidgetText(this, 0, 4, "flicker: "));
        addWidget(flickerTypeWidget);
        flickerSpeedSlider = new WidgetHSlider(this, 5, 5, null, 8, (int)(light.getFlickerSpeed()*100f), 0, 800, false);
        addWidget(new WidgetText(this, 0, 5, "speed:"));
        addWidget(flickerSpeedSlider);
        flickerIntensitySlider = new WidgetHSlider(this, 5, 6, null, 8, (int)(light.getFlickerIntensity()*100f), 0, 100, false);
        addWidget(new WidgetText(this,0,6,"intensity:"));
        addWidget(flickerIntensitySlider);
        deleteButton = new WidgetButton(this, 0, 8, "[ Delete ]", null);
        addWidget(deleteButton);

        sizeToWidgets();
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
        } else if (widget == flickerTypeWidget || widget == flickerSpeedSlider || widget == flickerIntensitySlider) {
            light.setFlicker(flickerTypeWidget.selection, (float)(flickerSpeedSlider.value)/100f, (float)(flickerIntensitySlider.value)/100f, 0);
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

