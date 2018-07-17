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

    protected int[] lightcolor;
    protected boolean lightcolorUseGlyph = false;
    protected int lightrange;
    protected int lightfalloff;
    protected int lightflicker;
    protected float lightflickerspeed;
    protected float lightflickerintensity;
    protected int lightflickeroffset;
    protected boolean spawnOn = false;
    protected boolean switchable = false;

    protected boolean on;

    protected ULight light;

    @Override
    public void initialize() {
        setOn(false);
        super.initialize();
    }

    void makeLight() {
        if (isLightcolorUseGlyph()) {
            SetupColors();
            setLight(new ULight(getGlyphColor(), getLightrange(), getLightfalloff()));
        } else {
            setLight(new ULight(new UColor(lightcolor[0], lightcolor[1], lightcolor[2]), getLightrange(), getLightfalloff()));
        }
        Random random = new Random();
        if (getLightflicker() > 0)
            getLight().setFlicker(getLightflicker(), getLightflickerspeed(), getLightflickerintensity(), random.nextInt(getLightflickeroffset()));
    }

    public boolean on() {
        if (getLocation() == null)
            return false;
        if (getLight() == null)
            makeLight();
        if (isSpawnOn())
            setOn(true);
        return isOn();
    }

    @Override
    public void moveToCell(UArea area, int x, int y) {
        super.moveToCell(area, x, y);
        if (on()) {
            getLight().moveTo(area, x, y);
        }
    }

    @Override
    public void moveTo(UContainer container) {
        super.moveTo(container);
        if (on()) {
            if (container.containerType() == UContainer.TYPE_CELL) {
                getLight().moveTo(area(), areaX(), areaY());
            }
        }
    }

    @Override
    public void notifyMove() {
        if (on()) {
            getLight().moveTo(area(), areaX(), areaY());
        }
    }

    @Override
    public void leaveCurrentLocation() {
        if (getLocation() != null) {
            if (getLocation().containerType() == UContainer.TYPE_CELL) {
                if (on()) {
                    getLight().removeFromArea();
                }
            }
        }
        super.leaveCurrentLocation();
    }

    public void turnOn() {
        if (!on()) {
            setOn(true);
            if (getLocation() != null) {
                getLight().moveTo(area(), areaX(), areaY());
            }
        }
    }

    public void turnOff() {
        setOn(false);
        getLight().removeFromArea();
    }

    @Override
    public boolean isUsable(UActor actor) {
        return isSwitchable();
    }

    @Override
    public float useFrom(UActor actor) {
        if (!on())
            turnOn();
        else
            turnOff();
        return 0.5f;
    }


    public int[] getLightcolor() {
        return lightcolor;
    }

    public void setLightcolor(int[] lightcolor) {
        this.lightcolor = lightcolor;
    }

    public boolean isLightcolorUseGlyph() {
        return lightcolorUseGlyph;
    }

    public void setLightcolorUseGlyph(boolean lightcolorUseGlyph) {
        this.lightcolorUseGlyph = lightcolorUseGlyph;
    }

    public int getLightrange() {
        return lightrange;
    }

    public void setLightrange(int lightrange) {
        this.lightrange = lightrange;
    }

    public int getLightfalloff() {
        return lightfalloff;
    }

    public void setLightfalloff(int lightfalloff) {
        this.lightfalloff = lightfalloff;
    }

    public int getLightflicker() {
        return lightflicker;
    }

    public void setLightflicker(int lightflicker) {
        this.lightflicker = lightflicker;
    }

    public float getLightflickerspeed() {
        return lightflickerspeed;
    }

    public void setLightflickerspeed(float lightflickerspeed) {
        this.lightflickerspeed = lightflickerspeed;
    }

    public float getLightflickerintensity() {
        return lightflickerintensity;
    }

    public void setLightflickerintensity(float lightflickerintensity) {
        this.lightflickerintensity = lightflickerintensity;
    }

    public int getLightflickeroffset() {
        return lightflickeroffset;
    }

    public void setLightflickeroffset(int lightflickeroffset) {
        this.lightflickeroffset = lightflickeroffset;
    }

    public boolean isSpawnOn() {
        return spawnOn;
    }

    public void setSpawnOn(boolean spawnOn) {
        this.spawnOn = spawnOn;
    }

    public boolean isSwitchable() {
        return switchable;
    }

    public void setSwitchable(boolean switchable) {
        this.switchable = switchable;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public ULight getLight() {
        return light;
    }

    public void setLight(ULight light) {
        this.light = light;
    }
}
