/**
 * Created by gilmore on 6/20/2018.
 *
 * A single xy grid cell of a Camera
 * Tracks all light hitting that cell of the camera.
 *
 */

import java.util.Hashtable;

public class Lightcell {
    boolean isVisible = false;
    Hashtable<URELight,Float> sources;

    public Lightcell() {
        isVisible = false;
        sources = new Hashtable<URELight,Float>();
    }

    public void wipe() {
        isVisible = false;
        sources.clear();
    }

    public void receiveLight(URELight source, float intensity) {
        sources.put(source, intensity);
    }
}
