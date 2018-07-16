package ure.things;

import ure.actors.UActor;
import ure.math.UColor;
import ure.areas.UArea;
import ure.ui.ULight;

import java.util.Random;

/**
 * A thing which can project a Light into the area at its location.
 *
 */
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
    public boolean switchable = false;

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
            light = new ULight(getGlyphColor(), lightrange, lightfalloff);
        } else {
            light = new ULight(new UColor(lightcolor[0], lightcolor[1], lightcolor[2]), lightrange, lightfalloff);
        }
        Random random = new Random();
        if (lightflicker > 0)
            light.setFlicker(lightflicker, lightflickerspeed, lightflickerintensity, random.nextInt(lightflickeroffset));
    }

    public boolean on() {
        if (getLocation() == null)
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
    public void moveTo(UContainer container) {
        super.moveTo(container);
        if (on()) {
            if (container.containerType() == UContainer.TYPE_CELL) {
                light.moveTo(area(), areaX(), areaY());
            }
        }
    }

    @Override
    public void notifyMove() {
        if (on()) {
            light.moveTo(area(), areaX(), areaY());
        }
    }

    @Override
    public void leaveCurrentLocation() {
        if (getLocation() != null) {
            if (getLocation().containerType() == UContainer.TYPE_CELL) {
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
            if (getLocation() != null) {
                light.moveTo(area(), areaX(), areaY());
            }
        }
    }

    public void turnOff() {
        on = false;
        light.removeFromArea();
    }

    @Override
    public boolean isUsable(UActor actor) {
        return switchable;
    }

    @Override
    public float useFrom(UActor actor) {
        if (!on())
            turnOn();
        else
            turnOff();
        return 0.5f;
    }
}
