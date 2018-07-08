package ure.terrain;

/**
 * Water has a pretty animation.  It should probably do something more interesting.
 *
 */
public class Water extends TerrainI implements UTerrain {

    public static final String TYPE = "water";

    public int waves;

    @Override
    public int glyphOffsetY() {
        int f = animationFrame;
        f = (f + cell.areaX() * 10 + cell.areaY()) % animationFrames;
        int mid = animationFrames/2;
        if (f > mid + 1)
            f =  animationFrames - f;
        float n = (float)f / (float)(animationFrames / 2);
        return (int)(Math.sin((double)n * 6.28) * waves - waves);
    }
}
