package ure.ui.modals.widgets;

import ure.areas.UArea;
import ure.math.UColor;
import ure.ui.Icons.Icon;
import ure.ui.modals.UModal;

public class WidgetMap extends Widget {

    public float zoom = 0.25f;
    public UArea area;
    public int camx,camy;
    public int centerx,centery;

    boolean dragging = false;
    int dragStartX, dragStartY;
    int dragCenterX, dragCenterY;

    public boolean showLabels = true;

    public WidgetMap(UModal modal, int col, int row, int cellw, int cellh) {
        super(modal);
        setDimensions(col,row,cellw,cellh);
        focusable = true;
        clipsToBounds = true;
    }

    public void lookAtArea(UArea area) {
        this.area = area;
    }
    public void moveView(int areax, int areay) {
        centerx = areax;
        centery = areay;
        recenter();
    }

    public void recenter() {
        camx = centerx - (int)(width/(int)(zoom*gw()))/2;
        camy = centery - (int)(height/(int)(zoom*gh()))/2;
    }

    public void zoomIn() {
        if (zoom < 0.5f) {
            zoom = zoom * 1.3f;
            recenter();
        }
    }

    public void zoomOut() {
        if (zoom > 0.2f) {
            zoom = zoom * 0.7f;
            recenter();
        }
    }

    @Override
    public void mouseClick(int mousex, int mousey) {
        dragging = true;
        dragCenterX = centerx;
        dragCenterY = centery;
        dragStartX = modal.commander.mouseX() - absoluteX();
        dragStartY = modal.commander.mouseY() - absoluteY();
    }

    @Override
    public void mouseInside(int mousex, int mousey) {
        if (dragging) {
            if (!modal.commander.mouseButton()) {
                dragging = false;
            } else {
                int mouseX = dragStartX - (modal.commander.mouseX() - absoluteX());
                int mouseY = dragStartY - (modal.commander.mouseY() - absoluteY());
                moveView(dragCenterX + (mouseX / (int)(zoom*gw())),
                        dragCenterY + (mouseY / (int)(zoom*gh())));
            }
        }
    }

    @Override
    public void drawMe() {
        for (int pass=1;pass<=2;pass++) {
            int penx = 0;
            int peny = 0;
            int penw = (int) (zoom * gw());
            int penh = (int) (zoom * gh());
            int xi = 0;
            int yi = 0;
            while (penx < width) {
                peny = 0;
                yi = 0;
                while (peny < height) {
                    if (area.isValidXY(xi + camx, yi + camy)) {
                        if (area.cellAt(xi + camx, yi + camy).isSeen()) {
                            if (pass == 1) {
                                UColor c = area.terrainAt(xi + camx, yi + camy).icon().bgColor();
                                if (c != null)
                                    modal.renderer.drawRect(penx, peny, penw, penh, c);
                            } else if (pass == 2) {
                                Icon icon = area.cellAt(xi+camx,yi+camy).mapIcon();
                                if (icon != null) {
                                    icon.setAnimate(false);
                                    icon.draw(penx, peny);
                                    if (showLabels)
                                        drawLabel(area.cellAt(xi+camx,yi+camy).mapLabel(), penx+gw()+2, peny);
                                }
                                if (xi+camx == modal.commander.player().areaX() && yi+camy == modal.commander.player().areaY()) {
                                    modal.commander.player().icon().draw(penx, peny);
                                    if (showLabels)
                                        drawLabel(modal.commander.player().name(), penx+gw()+2,peny);
                                }
                            }
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

    void drawLabel(String label, int x, int y) {
        int labelwidth = modal.renderer.textWidth(label);
        modal.renderer.drawRect(x-2,y-2,labelwidth+4, gh(), UColor.DARKSHADE);
        modal.renderer.drawString(x+1,y+1,UColor.BLACK, label);
        modal.renderer.drawString(x, y, UColor.OFFWHITE, label);
    }
}
