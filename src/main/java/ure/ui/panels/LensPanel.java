package ure.ui.panels;

import ure.actors.UActor;
import ure.areas.UCell;
import ure.math.UColor;
import ure.render.URenderer;
import ure.sys.UConfig;
import ure.terrain.UTerrain;
import ure.things.UThing;
import ure.ui.UCamera;

public class LensPanel extends UPanel {

    UCamera camera;
    int cameraX,cameraY;
    int cellw,cellh;
    int xpos,ypos;

    int updateFrame = 0;

    public LensPanel(UCamera watchCamera, int cameraOffsetX, int cameraOffsetY, int _padx, int _pady, UColor _fgColor, UColor _bgColor, UColor _borderColor) {
        super(_padx,_pady,_fgColor,_bgColor,_borderColor);
        camera = watchCamera;
        cameraX = cameraOffsetX;
        cameraY = cameraOffsetY;
    }

    @Override
    public void drawContent() {
        if (commander.hasModal()) return;
        int x = (commander.mouseX() - camera.getX()) / gw();
        int y = (commander.mouseY() - camera.getY()) / gh();
        if (camera.visibilityAt(x, y) < config.getVisibilityThreshold())
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
                renderer.drawString(xpos + padX + config.getTileWidth() * 2, ypos + padY + commander.config.getTileHeight(), commander.config.getTextColor(), thing.getIname());

            }
            UActor actor = cell.actorAt();
            if (actor != null) {
                actor.icon().draw(xpos + padX, ypos + padY + gh() * 2);
                renderer.drawString(xpos + padX + gw() * 2, ypos + padY + gh() * 2, commander.config.getTextColor(), actor.getIname());
            }
        }
    }
}
