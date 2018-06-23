/**
 * Created by gilmore on 6/20/2018.
 *
 * A single xy grid cell of a Camera
 * Tracks all light hitting that cell of the camera.
 *
 */

import java.util.Hashtable;

public class ULightcell {
    float visibility;
    float sunBrightness;

    Hashtable<URELight,Float> sources;

    public ULightcell() {
        visibility = 0f;
        sources = new Hashtable<URELight,Float>();
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
}
