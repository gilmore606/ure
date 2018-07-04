package ure.things;

import ure.UColor;
import ure.UContainer;
import ure.UREArea;
import ure.URELight;

public class Lightsource extends UREThing {

    public static final String TYPE = "lightsource";

    public int[] lightcolor;
    public int lightrange;
    public int lightfalloff;

    public boolean on;

    URELight light;

    @Override
    public void initialize() {
        on = false;
        super.initialize();
    }

    void makeLight() {
        light = new URELight(new UColor(lightcolor[0],lightcolor[1],lightcolor[2]),lightrange,lightfalloff);
    }

    public boolean on() {
        if (location == null)
            return false;
        return on;
    }

    @Override
    public void moveToCell(UREArea area, int x, int y) {
        super.moveToCell(area, x, y);
        if (on()) {
            if (light == null)
                makeLight();
            light.moveTo(area, x, y);
        }
    }

    @Override
    public void leaveCurrentLocation() {
        if (location != null) {
            if (location.containerType() == UContainer.TYPE_CELL) {
                if (on()) {
                    light.removeFromArea();
                }
            }
        }
        super.leaveCurrentLocation();
    }

    public void turnOn() {
        on = true;
        if (light == null)
            makeLight();
        light.moveTo(area(), areaX(), areaY());
    }

    public void turnOff() {
        on = false;
        light.removeFromArea();
    }

}
