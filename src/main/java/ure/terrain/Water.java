package ure.terrain;

/**
 * Water has a pretty animation.  It should probably do something more interesting.
 *
 */
public class Water extends TerrainI implements UTerrain {

    public static final String TYPE = "water";

    protected int waves;

    @Override
    public int glyphOffsetY() {
        int f = getAnimationFrame();
        f = (f + cell.areaX() * 10 + cell.areaY()) % getAnimationFrames();
        int mid = getAnimationFrames() /2;
        if (f > mid + 1)
            f =  getAnimationFrames() - f;
        float n = (float)f / (float)(getAnimationFrames() / 2);
        return (int)(Math.sin((double)n * 6.28) * waves - waves);
    }

    public int getWaves() {
        return waves;
    }

    public void setWaves(int waves) {
        this.waves = waves;
    }
}
