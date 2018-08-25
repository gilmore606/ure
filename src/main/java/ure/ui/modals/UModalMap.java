package ure.ui.modals;

import ure.areas.UArea;
import ure.math.UColor;
import ure.ui.Icons.Icon;

public class UModalMap extends UModal {

    UArea area;

    public UModalMap(UArea area, int width, int height) {
        super(null, "");
        this.area = area;
        setDimensions(width, height);
        setPad(1,1);
    }

    @Override
    public void drawContent() {
        for (int x=0;x<cellw*2;x++) {
            for (int y=0;y<cellh*2;y++) {
                int areax = (int)(area.xsize*fx(x));
                int areay = (int)(area.ysize*fy(y));
                if (area.cellAt(areax,areay).isSeen()) {
                    UColor c = area.terrainAt(areax, areay).icon().bgColor();
                    renderer.drawRect((int) (x * gw() * 0.5f) + absoluteX(), (int) (y * gh() * 0.5f) + absoluteY(), (int) (gw() * 0.5f), (int) (gh() * 0.5f), c);
                }
            }
        }
        commander.player().icon().draw(absoluteX()+gw()*areaToMapX(commander.player().areaX())/2,absoluteY()+gh()*areaToMapY(commander.player().areaY())/2);
        for (int x=0;x<area.xsize;x++) {
            for (int y=0;y<area.ysize;y++) {
                Icon icon = area.cellAt(x,y).mapIcon();
                if (icon != null) {
                    icon.setAnimate(false);
                    icon.draw(absoluteX() + gw() * areaToMapX(x) / 2, absoluteY() + gh() * areaToMapY(y) / 2);
                    icon.setAnimate(true);
                }
            }
        }
    }

    int areaToMapX(int x) {
        return (int)(((float)x/(float)area.xsize) * cellw*2);
    }
    int areaToMapY(int y) {
        return (int)(((float)y/(float)area.ysize) * cellh*2);
    }
    float fx(int x) {
        return (float)x/(float)(cellw*2);
    }
    float fy(int y) {
        return (float)y/(float)(cellh*2);
    }
}
