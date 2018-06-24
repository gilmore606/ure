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

    public UColor light() {
        UColor total = new UColor(0,0,0);

        for (URELight source : sources.keySet()) {
            float intensity = sources.get(source);
            total.addLights(source.color, intensity);
        }
        return total;
    }

}
