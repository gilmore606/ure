package ure.ui.modals.widgets;

import ure.areas.UArea;
import ure.math.UColor;
import ure.ui.modals.UModal;

public class WidgetMap extends Widget {

    public float zoom = 0.25f;
    public UArea area;
    public int camx,camy;

    public WidgetMap(UModal modal, int col, int row, int cellw, int cellh) {
        super(modal);
        setDimensions(col,row,cellw,cellh);
    }

    public void lookAtArea(UArea area) {
        this.area = area;
    }
    public void moveView(int areax, int areay) {
        camx = areax;
        camy = areay;
    }

    @Override
    public void drawMe() {
        int penx = 0;
        int peny = 0;
        int penw = (int)(zoom * gw());
        int penh = (int)(zoom * gh());
        int xi = 0;
        int yi = 0;
        while (penx < width) {
            peny = 0;
            yi = 0;
            while (peny < height) {
                if (area.isValidXY(xi+camx,yi+camy)) {
                    if (area.cellAt(xi + camx, yi + camy).isSeen()) {
                        UColor c = area.terrainAt(xi + camx, yi + camy).icon().bgColor();
                        if (c != null)
                            modal.renderer.drawRect(penx, peny, penw, penh, c);
                    }
                }
                peny += penh;
                yi++;
            }
            penx += penw;
            xi++;
        }
    }
}
