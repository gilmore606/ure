package ure.ui;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.areas.UArea;
import ure.math.UColor;
import ure.math.UPath;
import ure.math.URandom;
import ure.sys.Injector;
import ure.sys.UCommander;

import javax.inject.Inject;
import java.lang.Math;

/**
 * ULight represents a light source placed in an area cell.
 *
 */

public class ULight {

    @Inject
    @JsonIgnore
    UCommander commander;
    @Inject
    @JsonIgnore
    URandom random;

    public static final int POINT = 0;
    public static final int AMBIENT = 1;

    public static final int FLICKER_NONE = 0;
    public static final int FLICKER_FIRE = 1;
    public static final int FLICKER_PULSE = 2;
    public static final int FLICKER_FRITZ = 3;
    public static final int FLICKER_BLINK = 4;
    public static final int FLICKER_COMPULSE = 5;

    protected UColor color;
    protected int falloff = 1;
    protected int range;
    protected int flickerStyle = 0;
    protected float flickerSpeed = 0f;
    protected float flickerIntensity = 0f;
    protected int flickerOffset = 0;

    protected boolean permanent;

    @JsonIgnore
    UArea area;

    @JsonIgnore
    int fritzFrames;

    public int x,y;
    public int type = 0;
    public int width,height;

    public ULight() {
        Injector.getAppComponent().inject(this);
    }

    public ULight(int[] thecolor, int therange, int thefalloff) {
        Injector.getAppComponent().inject(this);
        type = POINT;
        setColor(new UColor(thecolor[0],thecolor[1],thecolor[2]));
        setRange(therange);
        setFalloff(thefalloff);
    }
    public ULight(UColor thecolor, int therange, int thefalloff) {
        Injector.getAppComponent().inject(this);
        type = POINT;
        setColor(new UColor(thecolor.fR(), thecolor.fG(), thecolor.fB(), thecolor.fA()));
        setRange(therange);
        setFalloff(thefalloff);
    }

    public void reconnect(UArea area) {
        this.area = area;
    }

    public void setFlicker(int style, float speed, float intensity, int offset) {
        setFlickerStyle(style);
        setFlickerSpeed(speed);
        setFlickerIntensity(intensity);
        setFlickerOffset(offset);
    }

    public void moveTo(UArea thearea, int thex, int they) {
        x = thex;
        y = they;
        if (area != thearea) {
            if (area != null) {
                area.removeLight(this);
            }
            area = thearea;
            area.addLight(this);
        }
    }

    public void removeFromArea() {
        if (area != null) {
            area.removeLight(this);
        }
        area = null;
    }

    public void makeAmbient(int width, int height) {
        type = AMBIENT;
        this.width = width;
        this.height = height;
    }

    public boolean canTouch(UCamera camera) {
        if (type == POINT) {
            int circleDistX = Math.abs(x - camera.getCenterColumn());
            int circleDistY = Math.abs(y - camera.getCenterRow());
            if (circleDistX > (camera.columns / 2 + getRange())) return false;
            if (circleDistY > (camera.rows / 2 + getRange())) return false;
            if (circleDistX <= (camera.columns / 2)) return true;
            if (circleDistY <= (camera.rows / 2)) return true;
            double cornerDistSq = Math.pow(circleDistX - camera.columns / 2, 2) + Math.pow(circleDistY - camera.rows / 2, 2);
            if (cornerDistSq <= Math.pow(getRange(), 2)) return true;
            return false;
        } else {
            int x1 = x-falloff; int x2 = x+width+falloff;
            int y1 = y-falloff; int y2 = y+height+falloff;
            if (x2 < camera.leftEdge) return false;
            if (y2 < camera.topEdge) return false;
            if (x1 > camera.rightEdge) return false;
            if (y1 > camera.bottomEdge) return false;
            return true;
        }
    }
    public boolean canTouch(int tx, int ty) {
        if (type == POINT) {
            if (intensityAtOffset(x - tx, y - ty) > 0.01f)
                return true;
            return false;
        } else {
            if (tx >= x-2 && tx <= x+width+1 && ty >= y-2 && ty <= y+height+1)
                return true;
            return false;
        }
    }

    public boolean lightsWall(int wallx, int wally, UCamera camera) {
        if (type == POINT) {
            // if we can see the point source, we can see the walls it lights up
            return camera.visibilityAt(x - camera.leftEdge, y-camera.topEdge) > 0.1f;
        } else {
            if (commander.player() == null) return true;
            // if ambient exists between player and wall, wall is lit
            if (UPath.intersectsRect(commander.player().areaX(),commander.player().areaY(),wallx,wally, x,y,width,height))
                return true;
            return false;
        }
    }

    public boolean isProjectedAt(int x, int y, UCamera camera) {
        ULightcell lightcell = camera.lightcellAt(x - camera.leftEdge, y - camera.topEdge);
        if (lightcell == null) return false;
        if (lightcell.sources.keySet().contains(this))
            return true;
        return false;
    }

    public float intensityAtTime(int time) {
        if (getFlickerStyle() == FLICKER_NONE) return 1f;
        float i = 0f;
        time = (int)((float)(time + getFlickerOffset()) * (getFlickerSpeed()));
        switch (getFlickerStyle()) {
            case FLICKER_FIRE: i =  intensityFlickerFire(time); break;
            case FLICKER_PULSE: i = intensityFlickerPulse(time); break;
            case FLICKER_FRITZ: i = intensityFlickerFritz(time); break;
            case FLICKER_BLINK: i = intensityFlickerBlink(time); break;
            case FLICKER_COMPULSE: i = intensityFlickerCompulse(time); break;
        }
        return 1f - (i * getFlickerIntensity());
    }

    float intensityFlickerFire(int time) {
        float f = (float)time / 6.0f;
        f = .75f + Math.max((float)(Math.sin(f * 2.7777)), (float)(Math.sin(f * 3.1117))) * .25f;
        return f;
    }
    float intensityFlickerPulse(int time) {
        float i = (float)Math.sin((double)time * 0.05);
        return i;
    }
    float intensityFlickerFritz(int time) {
        if (fritzFrames > 0)
            return random.f() * 0.2f + 0.3f;
        return 0f;
    }

    float intensityFlickerBlink(int time) {
        return 0f;
    }
    float intensityFlickerCompulse(int time) {
        float i = (float)Math.sin((double)time*0.05) + (float)Math.sin((double)time*0.03);
        return i;
    }
    public float intensityAtOffset(int xoff, int yoff) {
        if (type == AMBIENT) return 1f;
        xoff = (int)Math.pow(Math.abs(xoff),2);
        yoff = (int)Math.pow(Math.abs(yoff),2);
        double dist = Math.sqrt((double)xoff + (double)yoff);
        if (dist < getFalloff()) {
            return 1f;
        } else if (dist > getRange()) {
            return 0f;
        } else {
            float scale = ((float)dist - getFalloff()) / ((float) getRange() - getFalloff());
            double root = Math.sqrt(scale);
            return 1f - (float)root;
        }
    }

    public void animationTick() {
        if (fritzFrames > 0)
            fritzFrames--;
        if (flickerStyle == FLICKER_FRITZ) {
            if (random.f() < 0.02f) {
                fritzFrames += 1+random.i(3);
            }
        }
    }

    public UColor getColor() {
        return color;
    }

    public void setColor(UColor color) {
        this.color = color;
    }

    public int getFalloff() {
        return falloff;
    }

    public void setFalloff(int falloff) {
        this.falloff = falloff;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getFlickerStyle() {
        return flickerStyle;
    }

    public void setFlickerStyle(int flickerStyle) {
        this.flickerStyle = flickerStyle;
    }

    public float getFlickerSpeed() {
        return flickerSpeed;
    }

    public void setFlickerSpeed(float flickerSpeed) {
        this.flickerSpeed = flickerSpeed;
    }

    public float getFlickerIntensity() {
        return flickerIntensity;
    }

    public void setFlickerIntensity(float flickerIntensity) {
        this.flickerIntensity = flickerIntensity;
    }

    public int getFlickerOffset() {
        return flickerOffset;
    }

    public void setFlickerOffset(int flickerOffset) {
        this.flickerOffset = flickerOffset;
    }

    public void setX(int i) { x = i; }
    public int getX() { return x; }
    public void setY(int i) { y = i; }
    public int getY() { return y; }
    public void setType(int i) { type = i; }
    public int getType() { return type; }
    public void setWidth(int i) { width = i; }
    public int getWidth() { return width; }
    public void setHeight(int i) { height = i; }
    public int getHeight() { return height; }
    public boolean isPermanent() { return permanent; }
    public void setPermanent(boolean b) { permanent = b; }
}
