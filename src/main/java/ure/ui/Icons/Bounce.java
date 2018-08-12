package ure.ui.Icons;

public class Bounce extends Icon  {

    public static final String TYPE = "bounce";

    public Bounce() { super(TYPE); }

    @Override
    int glyphX() {
        return -(int)(Bounce(commander.frameCounter) * animAmpX);
    }
    @Override
    int glyphY() {
        return -(int)(Bounce(commander.frameCounter) * animAmpY);
    }

    float Bounce(int frame) {
        return (float)(Math.abs(Math.sin((frame + animOffset) * animFreq * 0.4f)) * 8f);
    }
}
