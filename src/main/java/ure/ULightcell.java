package ure;

import java.awt.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Created by gilmore on 6/20/2018.
 *
 * A single xy grid cell of a Camera
 * Tracks all light hitting that cell of the camera.
 *
 */

public class ULightcell {
    float visibility;
    float sunBrightness;
    float renderedSun;

    HashMap<URELight,Float> sources;

    public ULightcell() {
        visibility = 0f;
        sources = new HashMap<URELight,Float>();
    }

    public void wipe() {
        visibility = 0f;
        sources.clear();
    }

    public void receiveLight(URELight source, float intensity) {
        sources.put(source, intensity);
    }

    public void setVisibility(float thevis) {
        visibility = thevis;
    }

    public float visibility() {
        return visibility;
    }

    public void setSunBrightness(float thebri) {
        sunBrightness = thebri;
    }

    public void setRenderedSun(float thebri) { renderedSun = thebri; }

    public float getSunBrightness() { return sunBrightness; }

    public float getRenderedSun() { return renderedSun; }

    public int[] light() {
        int r = 0;
        int g = 0;
        int b = 0;

        for (URELight source : sources.keySet()) {
            r = r + source.color[0];
            g = g + source.color[1];
            b = b + source.color[2];
        }
        if (r > 255) r = 255;
        if (g > 255) g = 255;
        if (b > 255) b = 255;
        int light[] = {r,g,b};
        return light;
    }

}
