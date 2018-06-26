package ure;

import java.awt.*;
import java.lang.Math;

/**
 * An instance of a light source somewhere in an area
 * Not a thing, but a thing can create one
 */

public class URELight {
    public UColor color;
    public int falloff = 1;
    public int range;

    UREArea area;
    public int x,y;

    public URELight(Color thecolor, int therange) {
        int[] rgb = {(int)thecolor.getRed(), (int)thecolor.getGreen(), (int)thecolor.getBlue()};
        color = new UColor(rgb[0],rgb[1],rgb[2]);
        range = therange;
    }
    public URELight(int[] thecolor, int therange) {
        color = new UColor(thecolor[0],thecolor[1],thecolor[2]);
        range = therange;
    }
    public URELight(UColor thecolor, int therange) {
        color = thecolor;
        range = therange;
    }

    public void close() {
        removeFromArea();
    }

    public void moveTo(UREArea thearea, int thex, int they) {
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

    public boolean canTouch(URECamera camera) {
        int circleDistX = Math.abs(x - camera.centerX);
        int circleDistY = Math.abs(y - camera.centerY);
        if (circleDistX > (camera.width/2 + range)) return false;
        if (circleDistY > (camera.height/2 + range)) return false;
        if (circleDistX <= (camera.width/2)) return true;
        if (circleDistY <= (camera.height/2)) return true;
        double cornerDistSq = Math.pow(circleDistX - camera.width/2, 2) + Math.pow(circleDistY - camera.height/2, 2);
        if (cornerDistSq <= Math.pow(range,2)) return true;
        return false;
    }
    public boolean canTouch(int tx, int ty) {
        if (intensityAtOffset(x - tx, y - ty) > 0.01f)
            return true;
        return false;
    }

    public float intensityAtOffset(int xoff, int yoff) {
        xoff = (int)Math.pow(Math.abs(xoff),2);
        yoff = (int)Math.pow(Math.abs(yoff),2);
        double dist = Math.sqrt((double)xoff + (double)yoff);
        if (dist < falloff) {
            return 1f;
        } else if (dist > range) {
            return 0f;
        } else {
            float scale = ((float)dist - falloff) / ((float)range - falloff);
            double root = Math.sqrt(scale);
            return 1f - (float)root;
        }
    }
}
