package ure.terrain;

public class Water extends URETerrain {

    public static final String TYPE = "water";

    public int waves;

    @Override
    public int glyphOffsetY() {
        int f = animationFrame;
        f = (f + cell.areaX() + cell.areaY()) % animationFrames;
        if (f > animationFrames/2)
            f = (animationFrames/2) - f;
        float n = (float)f / (float)animationFrames;
        return (int)(Math.sin((double)n) * waves + waves / 2);
    }
}
