package ure.ui.particles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.Injector;
import ure.sys.UAnimator;
import ure.areas.UArea;
import ure.sys.UCommander;

import javax.inject.Inject;

public class UParticle implements UAnimator {

    @Inject
    UCommander commander;

     int ticksLeft;
     int ticksInitial;

    @JsonIgnore
    UArea area;

    public int x, y;
    char glyph;
    float fgR,fgG,fgB,bgR,bgG,bgB;
    boolean receiveLight;
    float alpha;
    float alphadecay;
    UColor colorbuffer;
    String glyphFrames;

    public UParticle(int thex, int they, int lifeticks, UColor _fgColor, float startalpha, boolean _receiveLight) {
        Injector.getAppComponent().inject(this);
        x = thex;
        y = they;
        ticksLeft = lifeticks;
        ticksInitial = lifeticks;
        glyph = '*';
        receiveLight = _receiveLight;
        fgR = _fgColor.fR();
        fgG = _fgColor.fG();
        fgB = _fgColor.fB();
        alpha = startalpha;
        alphadecay = alpha / lifeticks;
        colorbuffer = new UColor(fgR,fgG,fgB);
    }

    public void reconnect(UArea area) {
        this.area = area;
    }

    public void animationTick() {
        ticksLeft--;
        alpha -= alphadecay;
    }

    public boolean isFizzled() { return (ticksLeft < 0); }

    public char glyph() {
        if (glyphFrames != null) {
            return glyphFrames.charAt(ticksInitial - ticksLeft);
        }
        return glyph;
    }
    public boolean isReceiveLight() { return receiveLight; }

    public void render(URenderer renderer, int px, int py, UColor light, float vis) {
        if (receiveLight)
            colorbuffer.illuminateWith(light, vis);
        else
            colorbuffer.set(fgR,fgG,fgB);
        colorbuffer.setAlpha(alpha * vis);
        renderer.drawGlyph(glyph(), px, py, colorbuffer, 0, 0);
    }

    public int glyphOffsetX() { return 0; }
    public int glyphOffsetY() { return 0; }
}
