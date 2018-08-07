package ure.ui.panels;

import ure.actors.UActor;
import ure.areas.UCell;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.UConfig;
import ure.terrain.UTerrain;
import ure.things.UThing;
import ure.ui.UCamera;

public class ULensPanel extends UPanel {

    UCamera camera;
    int cameraX,cameraY;
    int cellw,cellh;
    int xpos,ypos;

    int updateFrame = 0;

    public ULensPanel(UCamera watchCamera, int cameraOffsetX, int cameraOffsetY, int _pixelw, int _pixelh, int _padx, int _pady, UColor _fgColor, UColor _bgColor, UColor _borderColor) {
        super(_pixelw,_pixelh,_padx,_pady,_fgColor,_bgColor,_borderColor);
        camera = watchCamera;
        cameraX = cameraOffsetX;
        cameraY = cameraOffsetY;
        setCameraOffsets();
    }

    public void setCameraOffsets() {
        int screenw = 0, screenh = 0;
        if (commander.config.getModalPosition() == UConfig.POS_WINDOW_CENTER) {
            screenw = commander.config.getScreenWidth();
            screenh = commander.config.getScreenHeight();
        } else {
            screenw = camera.getWidthInCells() * commander.config.getTileWidth();
            screenh = camera.getHeightInCells() * commander.config.getTileHeight();
        }

        xpos = (screenw - (cellw * commander.config.getTileWidth())) / 2;
        ypos = (screenh - (cellh * commander.config.getTileHeight())) / 2;
    }

    @Override
    public void draw() {
        if (!hidden) {
            renderer.drawRectBorder(1, 1, width - 2, height - 2, 1, bgColor, borderColor);
            int mousex = commander.mouseX();
            int mousey = commander.mouseY();
            int x = toCellX(mousex);
            int y = toCellY(mousey);
            if (camera.visibilityAt(x, y) < commander.config.getVisibilityThreshold())
                return;
            UTerrain t = camera.terrainAt(x, y);
            if (t != null) {
                renderer.drawString(xpos + padX + commander.config.getTileWidth() * 2, ypos + padY, commander.config.getTextColor(), t.getName());
                t.getIcon().draw(xpos + padX, ypos + padY);
            }
            UCell cell = camera.area.cellAt(x + camera.leftEdge, y + camera.topEdge);
            if (cell != null) {
                UThing thing = cell.topThingAt();
                if (thing != null) {
                    thing.getIcon().draw( xpos + padX, ypos + padY + commander.config.getTileHeight());
                    renderer.drawString(xpos + padX + commander.config.getTileWidth() * 2, ypos + padY + commander.config.getTileHeight(), commander.config.getTextColor(), thing.getIname());

                }
                UActor actor = cell.actorAt();
                if (actor != null) {
                    actor.icon().draw(xpos + padX, ypos + padY + commander.config.getTileHeight() * 2);
                    renderer.drawString(xpos + padX + commander.config.getTileWidth() * 2, ypos + padY + commander.config.getTileHeight() * 2, commander.config.getTextColor(), actor.getIname());
                }
            }
        }
    }

    public int toCellX(int x) {
        return (x - cameraX) / commander.config.getTileWidth();
    }
    public int toCellY(int y) {
        return (y - cameraY) / commander.config.getTileHeight();
    }
}
