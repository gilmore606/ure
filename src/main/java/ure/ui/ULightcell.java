package ure.ui;

import ure.math.UColor;

import java.util.HashMap;


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
    float sunBuffer;

    private UColor lightBuffer;

    HashMap<ULight,Float> sources;
    private UCamera camera;

    public ULightcell(UCamera thecam) {
        camera = thecam;
        visibility = 0f;
        sources = new HashMap<ULight,Float>();
        lightBuffer = new UColor(0f,0f,0f);
    }

    public void wipe() {
        visibility = 0f;
        sources.clear();
    }

    public void receiveLight(ULight source, float intensity) {
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

    public void setRenderedSun(float thebri) { sunBuffer = thebri; }

    public float getSunBrightness() { return sunBrightness; }

    public float getRenderedSun() { return sunBuffer; }

    public UColor light() { return light(0); }
    public UColor light(int time) {
        lightBuffer.set(0f,0f,0f);
        lightBuffer.addLights(camera.area.sunColor, getRenderedSun());
        for (ULight source : sources.keySet()) {
            float intensity = sources.get(source) * source.intensityAtTime(time);
            lightBuffer.addLights(source.color, intensity);
        }
        return lightBuffer;
    }

}
