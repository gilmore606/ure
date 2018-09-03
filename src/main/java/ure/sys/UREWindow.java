package ure.sys;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import ure.sys.events.ResolutionChangedEvent;
import ure.ui.UCamera;
import ure.ui.View;
import ure.ui.panels.UPanel;

import javax.inject.Inject;
import java.util.ArrayList;

public class UREWindow extends View {

    @Inject
    UConfig config;
    @Inject
    EventBus bus;

    public UCamera camera;
    public ArrayList<UPanel> panels;

    public UREWindow() {
        Injector.getAppComponent().inject(this);
        panels = new ArrayList<>();
        bus.register(this);
        setBounds(0,0,config.getScreenWidth(), config.getScreenHeight());
    }

    public void setCamera(UCamera camera) {
        this.camera = camera;
        addChild(camera);
    }

    public void addPanel(UPanel panel) {
        panels.add(panel);
        addChild(panel);
    }

    @Subscribe
    public void resolutionChanged(ResolutionChangedEvent event) {
        setBounds(0,0,event.width,event.height);
        doLayout();
    }

    public void doLayout() {

        int camx1 = 0;
        int camx2 = width;
        int camy1 = 0;
        int camy2 = height;
        int fitx1 = camx1;
        int fitx2 = camx2;
        int fity1 = camy1;
        int fity2 = camy2;

        for (UPanel p : panels) {
            if (p.isHidden()) {
                p.setBounds(0,0,0,0);
            } else {
                int pwidth = p.widthForXsize(width) * config.getTileWidth();
                int pheight = p.heightForYsize(height) * config.getTileHeight();
                int px, py;
                if (p.layoutXpos == UPanel.XPOS_LEFT) {
                    px = 0;
                    fitx1 = Math.max(fitx1, pwidth);
                    if (p.layoutYpos == UPanel.YPOS_FIT)
                        camx1 = Math.max(camx1, pwidth);
                } else if (p.layoutXpos == UPanel.XPOS_RIGHT) {
                    px = width - pwidth;
                    fitx2 = Math.min(fitx2, px);
                    if (p.layoutYpos == UPanel.YPOS_FIT)
                        camx2 = Math.min(camx2, px);
                } else {
                    px = fitx1;
                    pwidth = Math.min(pwidth, fitx2 - fitx1);
                }
                if (p.layoutYpos == UPanel.YPOS_TOP) {
                    py = 0;
                    fity1 = Math.max(fity1, pheight);
                    if (p.layoutXpos == UPanel.XPOS_FIT)
                        camy1 = Math.max(camy1, pheight);
                } else if (p.layoutYpos == UPanel.YPOS_BOTTOM) {
                    py = height - pheight;
                    fity2 = Math.min(fity2, py);
                    if (p.layoutXpos == UPanel.XPOS_FIT)
                        camy2 = Math.min(camy2, py);
                } else {
                    py = fity1;
                    pheight = Math.min(pheight, fity2 - fity1);
                }
                p.resizeView(px, py, pwidth, pheight);
                p.setBounds(px, py, pwidth, pheight);
            }
        }
        camera.resizeView(camx1,camy1,camx2-camx1,camy2-camy1);
    }
}
