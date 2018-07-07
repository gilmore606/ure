package ure.things;

import ure.UColor;
import ure.UContainer;
import ure.UArea;
import ure.ULight;

import java.util.Random;

public class Lightsource extends ThingI implements UThing {

    public static final String TYPE = "lightsource";

    public int[] lightcolor;
    public boolean lightcolorUseGlyph = false;
    public int lightrange;
    public int lightfalloff;
    public int lightflicker;
    public float lightflickerspeed, lightflickerintensity;
    public int lightflickeroffset;
    public boolean spawnOn = false;

    public boolean on;

    ULight light;

    @Override
    public void initialize() {
        on = false;
        super.initialize();
    }

    void makeLight() {
        if (lightcolorUseGlyph) {
            SetupColors();
            light = new ULight(glyphColor, lightrange, lightfalloff);
        } else {
            light = new ULight(new UColor(lightcolor[0], lightcolor[1], lightcolor[2]), lightrange, lightfalloff);
        }
        Random random = new Random();
        if (lightflicker > 0)
            light.setFlicker(lightflicker, lightflickerspeed, lightflickerintensity, random.nextInt(lightflickeroffset));
    }

    public boolean on() {
        if (location == null)
            return false;
        if (light == null)
            makeLight();
        if (spawnOn)
            on = true;
        return on;
    }

    @Override
    public void moveToCell(UArea area, int x, int y) {
        super.moveToCell(area, x, y);
        if (on()) {
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
        if (!on()) {
            on = true;
            if (location != null) {
                light.moveTo(area(), areaX(), areaY());
            }
        }
    }

    public void turnOff() {
        on = false;
        light.removeFromArea();
    }

}
