package ure.things;

import ure.actors.UActor;
import ure.areas.UCell;
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
    protected boolean litWhenUnequipped = true;
    protected boolean junkOnEmpty = true;
    protected float sparkrate = 0f;
    protected int[] onglyphcolor;
    protected UColor glyphColorSaved;
    protected String isOnMsg = "It's on.";
    protected String isOffMsg = "It's off.";
    protected char onglyph = 0;
    protected int fuel = -1;
    protected int lastBurnTurn;

    protected boolean on;

    protected ULight light;

    @Override
    public void initializeAsTemplate() {
        setOn(false);
        super.initializeAsTemplate();
    }

    @Override
    public void initializeAsCloneFrom(UThing template) {
        lastBurnTurn = commander.turnCounter;
    }

    public void addFuel(int fuel) {
        this.fuel += fuel;
        on();
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
            getLight().setFlicker(getLightflicker(), getLightflickerspeed(), getLightflickerintensity(), random.i(getLightflickeroffset()));
    }

    public boolean on() {
        if (getLocation() == null)
            return false;
        if (getLight() == null)
            makeLight();
        if (fuel == 0)
            return false;
        if (isSpawnOn() && !isSwitchable())
            setOn(true);
        return isOn();
    }

    public boolean lightIsVisible() {
        if (on()) {
            if (location instanceof UActor) {
                if (equipped || litWhenUnequipped)
                    return true;
                else
                    return false;
            } else if (location instanceof UCell) {
                return true;
            }
        }
        return false;
    }

    void deployLight() {
        if (lightIsVisible())
            getLight().moveTo(area(), areaX(), areaY());
        else
            getLight().removeFromArea();
    }

    @Override
    public void reconnect(UArea area, UContainer container) {
        super.reconnect(area, container);
        deployLight();
    }

    @Override
    public boolean tryEquip(UActor actor) {
        if (super.tryEquip(actor)) {
            deployLight();
            return true;
        }
        return false;
    }

    @Override
    public boolean tryUnequip(UActor actor) {
        if (super.tryUnequip(actor)) {
            deployLight();
            return true;
        }
        return false;
    }

    @Override
    public void moveToCell(UArea area, int x, int y) {
        super.moveToCell(area, x, y);
        if (on())
            deployLight();
    }

    @Override
    public void moveTo(UContainer container) {
        super.moveTo(container);
        if (on())
            deployLight();
    }

    @Override
    public void notifyMove() {
        if (on())
            deployLight();
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
            if (getLocation() != null)
                deployLight();
            if (onglyphcolor != null) {
                glyphColorSaved = icon().fgColor;
                icon.setFgColor(new UColor(onglyphcolor[0],onglyphcolor[1],onglyphcolor[2]));
            }
        }
    }

    public void turnOff() {
        setOn(false);
        getLight().removeFromArea();
        if (glyphColorSaved != null && onglyphcolor != null)
            icon.setFgColor(glyphColorSaved);
    }

    @Override
    public boolean isUsable(UActor actor) {
        return isSwitchable();
    }
    @Override
    public String useVerb() { return "switch"; }

    @Override
    public float useFrom(UActor actor) {
        if (!isOn()) {
            if (!litWhenUnequipped && !equipped)
                tryEquip(actor);
            turnOn();
        } else {
            turnOff();
        }
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
    public boolean isLitWhenUnequipped() { return litWhenUnequipped; }
    public void setLitWhenUnequipped(boolean b) { litWhenUnequipped = b; }

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
    public UColor getGlyphColorSaved() { return glyphColorSaved; }
    public void setGlyphColorSaved(UColor c) { glyphColorSaved = c; }
    public char getOnglyph() { return onglyph; }
    public void setOnglyph(char c) { onglyph = c; }
    public float getSparkrate() { return sparkrate; }
    public void setSparkrate(float r) { sparkrate = r; }
    public int getFuel() { return fuel; }
    public void setFuel(int i) { fuel = i; }

    @Override
    public void animationTick() {
        if (fuel > 0) {
            if (isOn())
                fuel = Math.max(0, fuel - (commander.turnCounter - lastBurnTurn));
            lastBurnTurn = commander.turnCounter;
            if (fuel == 0) {
                turnOff();
                if (junkOnEmpty) junk();
            }
        }
        if (on()) {
            if (random.f() < sparkrate) {
                area().addParticle(new ParticleSpark(areaX(), areaY(),
                        UColor.YELLOW, 8 + random.i(10), 0.3f + random.f(0.4f)));

            }
        }
    }

}
