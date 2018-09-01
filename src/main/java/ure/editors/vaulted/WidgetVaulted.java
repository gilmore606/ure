package ure.editors.vaulted;

import ure.areas.UVault;
import ure.math.UColor;
import ure.math.UPath;
import ure.terrain.UTerrain;
import ure.ui.Icons.Icon;
import ure.ui.UCamera;
import ure.ui.ULight;
import ure.ui.modals.UModal;
import ure.ui.modals.widgets.Widget;

public class WidgetVaulted extends Widget {

    UVault vault;
    String[][] undoTerrain;

    VaultedArea area;
    UCamera camera;

    int tool;
    int toolX,toolY;
    int toolFinishX,toolFinishY;
    int cursorx,cursory;
    Icon brushIcon;
    UColor brightHilite;

    public WidgetVaulted(UModal modal, int x, int y, int w, int h) {
        super(modal);
        area = new VaultedArea(w, h);
        camera = new UCamera(x*gw(),y*gh(),w*gw(),h*gh());
        camera.moveTo(area,w/2,h/2);
        camera.setLightEnable(false);
        modal.config.setVisibilityEnable(false);
        setDimensions(x,y,w,h);
        modal.commander.addAnimator(camera);
        focusable = true;
        brightHilite = new UColor(modal.config.getHiliteColor());
        brightHilite.setAlpha(1f);
        camera.renderLights();
    }

    void setCell(String t, int col, int row) {
        vault.terrain[col][row] = t;
        area.setTerrain(col,row,t);
        camera.renderLights();
    }

    @Override
    public void setDimensions(int x, int y, int w, int h) {
        super.setDimensions(x,y,w,h);
        area.initialize(w,h);
        camera.resize(w*gw(),h*gh());
        camera.moveTo(area, w/2,h/2);
    }

    public void saveUndo() {
        ((VaultedModal)modal).log.debug("(saving undo buffer)");
        if (undoTerrain == null) {
            undoTerrain = new String[cellw][cellh];
        }
        if (undoTerrain.length != vault.terrain.length || undoTerrain[0].length != vault.terrain[0].length) {
            undoTerrain = new String[cellw][cellh];
        }
        for (int i=0;i<cellw;i++) {
            for (int j=0;j<cellh;j++) {
                undoTerrain[i][j] = vault.terrain[i][j];
            }
        }
    }

    public void undo() {
        ((VaultedModal)modal).log.info("(restoring undo buffer)");
        String[][] swaps;
        swaps = vault.terrain;
        vault.terrain = undoTerrain;
        undoTerrain = swaps;
        setDimensions(col,row,vault.terrain.length,vault.terrain[0].length);
        for (int i=0;i<vault.terrain.length;i++) {
            for (int j=0;j<vault.terrain[0].length;j++) {
                area.setTerrain(i,j,vault.terrain[i][j]);
            }
        }
    }

    public void doCrop(int x1, int y1, int x2, int y2) {
        ((VaultedModal)modal).log.info("Cropping " + Integer.toString(x1) + "," + Integer.toString(y1) + " to " + Integer.toString(x2) + "," + Integer.toString(y2));
        int neww = x2-x1;
        int newh = y2-y1;
        String[][] oldTerrain = vault.terrainClone();
        setDimensions(col,row,neww,newh);
        vault.initialize(neww,newh);
        int cx = 0; int cy = 0;
        for (int i=x1;i<x2;i++) {
            cy = 0;
            for (int j=y1;j<y2;j++) {
                setCell(oldTerrain[i][j],cx,cy);
                cy++;
            }
            cx++;
        }
    }

    public void paint (UTerrain t) { paint(t,cursorx,cursory); }
    public void paint(UTerrain t, int px, int py) {
        if (vault.terrain[px][py] != t.getName()) {
            setCell(t.getName(), px, py);
        }
    }

    @Override
    public void mouseInside(int mousex, int mousey) {
        cursorx = mousex;
        cursory = mousey;
        if (!modal.commander.mouseButton() && tool > 0) {
            releaseTool();
        }
    }

    @Override
    public void drawMe() {
        if (focused) {
            if (tool == VaultedModal.TOOL_BOX || tool == VaultedModal.TOOL_CROP || tool == VaultedModal.TOOL_LIGHT) {
                int x1 = Math.min(toolX, cursorx);
                int x2 = Math.max(toolX, cursorx);
                int y1 = Math.min(toolY, cursory);
                int y2 = Math.max(toolY, cursory);
                if (tool == VaultedModal.TOOL_CROP) {
                    modal.renderer.drawRect(0, 0, x1 * gw() - 1, height, UColor.DARKERSHADE);
                    modal.renderer.drawRect((x2 + 1) * gw(), 0, width - (x2 + 1) * gw(), height, UColor.DARKERSHADE);
                    modal.renderer.drawRect(x1 * gw(), 0, (x2 - x1 + 1) * gw(), y1 * gh() - 1, UColor.DARKERSHADE);
                    modal.renderer.drawRect(x1 * gw(), (y2 + 1) * gh() + 1, (x2 - x1 + 1) * gw(), height - y2 * gh() + 1, UColor.DARKERSHADE);
                } else if (tool == VaultedModal.TOOL_LIGHT) {
                    modal.renderer.drawRect(x1*gw(),y1*gh(),(x2-x1+1)*gw(),(y2-y1+1)*gh(),UColor.LIGHT);
                } else if (brushIcon != null) {
                    for (int i = x1;i <= x2;i++) {
                        for (int j = y1;j <= y2;j++) {
                            drawIcon(brushIcon, i, j);
                        }
                    }
                }
                modal.renderer.drawRectBorder(x1 * gw() - 2, y1 * gh() - 2, (x2 - x1 + 1) * gw() + 2, (y2 - y1 + 1) * gh() + 2, 4, UColor.CLEAR, UColor.YELLOW);
            } else if (tool == VaultedModal.TOOL_LINE) {
                for (int[] point : UPath.line(toolX,toolY,cursorx,cursory)) {
                    if (brushIcon != null) drawIcon(brushIcon,point[0],point[1]);
                    modal.renderer.drawRectBorder(point[0]*gw()-2,point[1]*gh()-2,gw()+4, gh()+4, 4, UColor.CLEAR, UColor.YELLOW);
                }
            } else {
                if (brushIcon != null && tool == VaultedModal.TOOL_DRAW) {
                    drawIcon(brushIcon,cursorx,cursory);
                }
                modal.renderer.drawRectBorder(cursorx * gw()-1, cursory * gh()-1, gw() + 2, gh() + 2, 2, UColor.CLEAR, brightHilite);
            }
        }
    }

    public void loadVault(UVault vault) {
        this.vault = vault;
        for (ULight l : ((VaultedModal)modal).vault.lights ) {
            l.removeFromArea();
        }
        setDimensions(col,row,vault.cols,vault.rows);
        for (int i=0;i<cellw;i++) {
            for (int j=0;j<cellh;j++) {
                setCell(vault.terrainAt(i,j), i,j);
            }
        }
        saveUndo();
        for (ULight l : vault.lights) {
            l.moveTo(area, l.x, l.y);
        }
        camera.renderLights();
    }

    public void grow() {
        String[][] oldTerrain = vault.terrain;
        setDimensions(col,row,cellw+2,cellh+2);
        vault.initialize(cellw,cellh);
        for (int x=0;x<cellw;x++) {
            for (int y=0;y<cellh;y++) {
                if (x==0 || y==0 || x==(cellw-1) || y==(cellh-1)) {
                    setCell("null",x,y);
                } else {
                    setCell(oldTerrain[x-1][y-1],x,y);
                }
            }
        }
        for (ULight l : area.lights()) {
            l.moveTo(area, l.x+1,l.y+1);
        }
        camera.renderLights();
    }

    public void setToolStart(int tool) {
        this.tool = tool;
        toolX = cursorx;
        toolY = cursory;
    }

    void releaseTool() {
        if (tool == VaultedModal.TOOL_LINE) {
            toolFinishX = cursorx;
            toolFinishY = cursory;
        } else {
            int minx = Math.min(toolX, cursorx);
            int maxx = Math.max(toolX, cursorx);
            int miny = Math.min(toolY, cursory);
            int maxy = Math.max(toolY, cursory);
            toolX = minx;
            toolY = miny;
            toolFinishX = maxx;
            toolFinishY = maxy;
        }
        tool = 0;
        modal.widgetChanged(this);
    }
}
