package ure.ui.Icons;

public class Bubbles extends Icon {

    public static final String TYPE = "bubbles";

    public Bubbles() { super(TYPE); }

    @Override
    public int glyph(int frame) {
        if (glyphVariants == null)
            return super.glyph(frame);
        if (glyphVariants.length == 0)
            return super.glyph(frame);
        frame += animOffset;
        int scale = (int)(30f * (1f-animFreq));
        if (scale < 1)
            return glyph;
        int maxframe = glyphVariants.length + 1;
        int gframe = (frame % (maxframe * scale)) / scale;
        if (gframe == 0)
            return glyph;
        else
            return glyphVariants[gframe-1];
    }
}
