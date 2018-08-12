package ure.ui.particles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.math.UColor;
import ure.math.URandom;
import ure.render.URenderer;
import ure.sys.Injector;
import ure.sys.UAnimator;
import ure.areas.UArea;
import ure.sys.UCommander;
import ure.sys.UConfig;
import ure.ui.UCamera;

import javax.inject.Inject;
import java.util.Random;

public class UParticle implements UAnimator {

     int ticksLeft;
     int ticksInitial;

    @JsonIgnore
    UArea area;

    @Inject
    @JsonIgnore
    protected URenderer renderer;
    @Inject
    @JsonIgnore
    protected UConfig config;
    @Inject
    @JsonIgnore
    protected URandom random;

    public int x, y;
    char glyph;
    float fgR,fgG,fgB,bgR,bgG,bgB;
    boolean receiveLight;
    float alpha;
    float alphadecay;
    UColor colorbuffer;
    String glyphFrames;

    float offx, offy;
    float vecx, vecy;
    float gravx, gravy;

    public UParticle(int x, int y, int ticksLeft, UColor fgColor, float alpha, boolean receiveLight, float vecx, float vecy, float gravx, float gravy) {
        Injector.getAppComponent().inject(this);
        this.x = x;
        this.y = y;
        this.ticksLeft = ticksLeft;
        ticksInitial = ticksLeft;
        glyph = '*';
        this.receiveLight = receiveLight;
        fgR = fgColor.fR();
        fgG = fgColor.fG();
        fgB = fgColor.fB();
        this.alpha = alpha;
        alphadecay = alpha / ticksLeft;
        colorbuffer = new UColor(fgR,fgG,fgB);
        this.vecx = vecx;
        this.vecy = vecy;
        this.gravx = gravx;
        this.gravy = gravy;
    }

    public void reconnect(UArea area) {
        this.area = area;
    }

    public void animationTick() {
        ticksLeft--;
        alpha -= alphadecay;
        offx += vecx;
        offy += vecy;
        vecx += gravx;
        vecy += gravy;
    }

    public boolean isFizzled() { return (ticksLeft < 0); }

    public char glyph() {
        if (glyphFrames != null) {
            return glyphFrames.charAt(ticksInitial - ticksLeft);
        }
        return glyph;
    }
    public boolean isReceiveLight() { return receiveLight; }

    public void draw(UCamera camera, UColor light, float vis) {
        if (receiveLight)
            colorbuffer.illuminateWith(light, vis);
        else
            colorbuffer.set(fgR,fgG,fgB);
        colorbuffer.setAlpha(alpha * vis);
        renderer.drawGlyph(glyph(), (x - camera.leftEdge)*config.getTileWidth() + glyphX(), (y - camera.topEdge)*config.getTileHeight() + glyphY(), colorbuffer);
    }

    public int glyphX() { return (int)offx; }
    public int glyphY() { return (int)offy; }
}
