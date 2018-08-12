package ure.things;

import ure.actors.UActor;
import ure.math.UColor;
import ure.areas.UArea;
import ure.ui.ULight;
import ure.ui.particles.ParticleSpark;

import java.util.ArrayList;

/**
 * A thing which can project a Light into the area at its location.
 *
 */
public class Lightsource extends UThing {

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
    protected float sparkrate = 0f;
    protected int[] onglyphcolor;
    protected String isOnMsg = "It's on.";
    protected String isOffMsg = "It's off.";
    protected char onglyph = 0;

    protected boolean on;

    protected ULight light;

    @Override
    public void initializeAsTemplate() {
        setOn(false);
        super.initializeAsTemplate();
    }

    void makeLight() {
        if (isLightcolorUseGlyph()) {
            if (icon().getFgColor() == null)
                setLight(new ULight(UColor.WHITE, getLightrange(), getLightfalloff()));
            else
                setLight(new ULight(icon().getFgColor(), getLightrange(), getLightfalloff()));
        } else {
            setLight(new ULight(new UColor(lightcolor[0], lightcolor[1], lightcolor[2]), getLightrange(), getLightfalloff()));
        }
        if (getLightflicker() > 0)
            getLight().setFlicker(getLightflicker(), getLightflickerspeed(), getLightflickerintensity(), random.nextInt(getLightflickeroffset()));
    }

    public boolean on() {
        if (getLocation() == null)
            return false;
        if (getLight() == null)
            makeLight();
        if (isSpawnOn() && !isSwitchable())
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
                getLight().moveTo(area(), areaX(), areaY());
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
    public String useVerb() { return "switch"; }

    @Override
    public float useFrom(UActor actor) {
        if (!isOn())
            turnOn();
        else
            turnOff();
        return 0.5f;
    }

    @Override
    public ArrayList<String> UIdetails(String context) {
            ArrayList<String> d = super.UIdetails(context);
            if (isOn())
                d.add(isOnMsg);
            else
                d.add(isOffMsg);
            return d;
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

    public void setIsOnMsg(String s) { isOnMsg = s; }
    public String getIsOnMsg() { return isOnMsg; }
    public void setIsOffMsg(String s) { isOffMsg = s; }
    public String getIsOffMsg() { return isOffMsg; }
    public int[] getOnglyphcolor() { return onglyphcolor; }
    public void setOnglyphcolor(int[] i) { onglyphcolor = i; }
    public char getOnglyph() { return onglyph; }
    public void setOnglyph(char c) { onglyph = c; }
    public float getSparkrate() { return sparkrate; }
    public void setSparkrate(float r) { sparkrate = r; }

    @Override
    public void animationTick() {
        if (random.nextFloat() < sparkrate) {
            if (random.nextFloat() < 0.3f) {
                area().addParticle(new ParticleSpark(areaX() - 1 + random.nextInt(3),
                        areaY() - 1 + random.nextInt(3),
                        UColor.YELLOW, 8 + random.nextInt(10), 0.3f + random.nextFloat() * 0.4f));
            } else {
                area().addParticle(new ParticleSpark(areaX(), areaY() - (random.nextInt(2)),  UColor.YELLOW, 8 + random.nextInt(10), 0.6f));
            }
        }
    }

}
