package ure.editors.vaulted;

import ure.areas.UVault;
import ure.math.UColor;
import ure.terrain.UTerrain;
import ure.ui.Icons.Icon;
import ure.ui.modals.UModal;
import ure.ui.modals.widgets.Widget;

public class WidgetVaulted extends Widget {

    Icon[][] gridIcons;
    String[][] gridTerrain;

    Icon[][] undoIcons;
    String[][] undoTerrain;

    static int TOOLTYPE_BOX = 1;
    static int TOOLTYPE_LINE = 2;

    int tool;
    int toolX,toolY;
    int toolFinishX,toolFinishY;
    int cursorx,cursory;
    UColor brightHilite;

    public WidgetVaulted(UModal modal, int x, int y, int w, int h) {
        super(modal);
        setDimensions(x,y,w,h);
        clearGrid();
        focusable = true;
        brightHilite = new UColor(modal.config.getHiliteColor());
        brightHilite.setAlpha(1f);
    }

    void clearGrid() {
        gridIcons = new Icon[cellw][cellh];
        gridTerrain = new String[cellw][cellh];
        for (int i=0;i<cellw;i++) {
            for (int j=0;j<cellh;j++) {
                gridTerrain[i][j] = "null";
                gridIcons[i][j] = modal.iconCzar.getIconByName("null");
            }
        }
    }

    public void saveUndo() {
        ((VaultedModal)modal).log.info("(saving undo buffer)");
        if ((undoIcons == null) || (undoTerrain == null)) {
            undoIcons = new Icon[cellw][cellh];
            undoTerrain = new String[cellw][cellh];
        }
        if (undoIcons.length != gridIcons.length || undoIcons[0].length != gridIcons[0].length) {
            undoIcons = new Icon[cellw][cellh];
            undoTerrain = new String[cellw][cellh];
        }
        for (int i=0;i<cellw;i++) {
            for (int j=0;j<cellh;j++) {
                undoIcons[i][j] = gridIcons[i][j];
                undoTerrain[i][j] = gridTerrain[i][j];
            }
        }
    }

    public void undo() {
        ((VaultedModal)modal).log.info("(restoring undo buffer)");
        Icon[][] swapi;
        String[][] swaps;
        swapi = gridIcons;
        swaps = gridTerrain;
        gridIcons = undoIcons;
        gridTerrain = undoTerrain;
        undoIcons = swapi;
        undoTerrain = swaps;
        setDimensions(col,row,gridTerrain.length,gridTerrain[0].length);
    }

    public void doCrop(int x1, int y1, int x2, int y2) {
        ((VaultedModal)modal).log.info("Cropping " + Integer.toString(x1) + "," + Integer.toString(y1) + " to " + Integer.toString(x2) + "," + Integer.toString(y2));
        int neww = x2-x1;
        int newh = y2-y1;
        String[][] oldTerrain = gridTerrain;
        Icon[][] oldIcons = gridIcons;
        setDimensions(col,row,neww,newh);
        gridIcons = new Icon[cellw][cellh];
        gridTerrain = new String[cellw][cellh];
        int cx = 0; int cy = 0;
        for (int i=x1;i<x2;i++) {
            cy = 0;
            for (int j=y1;j<y2;j++) {
                gridIcons[cx][cy] = oldIcons[i][j];
                gridTerrain[cx][cy] = oldTerrain[i][j];
                cy++;
            }
            cx++;
        }
    }

    public void paint(UTerrain t) {
        if (gridTerrain[cursorx][cursory] != t.getName()) {
            gridTerrain[cursorx][cursory] = t.getName();
            gridIcons[cursorx][cursory] = modal.iconCzar.getIconByName(t.getName());
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
        for (int x=0;x<cellw;x++) {
            for (int y=0;y<cellh;y++) {
                drawIcon(gridIcons[x][y], x, y);
            }
        }
        if (focused) {
            if (tool == TOOLTYPE_BOX || tool == TOOLTYPE_LINE) {
                int x1 = Math.min(toolX, cursorx);
                int x2 = Math.max(toolX, cursorx);
                int y1 = Math.min(toolY, cursory);
                int y2 = Math.max(toolY, cursory);
                if (tool == TOOLTYPE_BOX) {
                    modal.renderer.drawRectBorder(x1*gw()-2,y1*gh()-2,(x2-x1+1)*gw()+2,(y2-y1+1)*gh()+2, 4, UColor.CLEAR, UColor.YELLOW);
                }
            } else {
                modal.renderer.drawRectBorder(cursorx * gw(), cursory * gh(), gw() + 2, gh() + 2, 2, UColor.CLEAR, brightHilite);
            }
        }
    }

    public void saveVault(UVault vault) {
        vault.initialize(cellw,cellh);
        for (int i=0;i<cellw;i++) {
            for (int j=0;j<cellh;j++) {
                vault.setTerrainAt(i,j,gridTerrain[i][j]);
            }
        }
    }

    public void loadVault(UVault vault) {
        setDimensions(col,row,vault.cols,vault.rows);
        gridTerrain = new String[cellw][cellh];
        gridIcons = new Icon[cellw][cellh];
        for (int i=0;i<cellw;i++) {
            for (int j=0;j<cellh;j++) {
                gridTerrain[i][j] = vault.terrainAt(i,j);
                gridIcons[i][j] = modal.iconCzar.getIconByName(gridTerrain[i][j]);
            }
        }
        saveUndo();
    }

    public void grow() {
        String[][] oldTerrain = gridTerrain;
        Icon[][] oldIcons = gridIcons;
        setDimensions(col,row,cellw+2,cellh+2);
        gridTerrain = new String[cellw][cellh];
        gridIcons = new Icon[cellw][cellh];
        for (int x=0;x<cellw;x++) {
            for (int y=0;y<cellh;y++) {
                if (x==0 || y==0 || x==(cellw-1) || y==(cellh-1)) {
                    gridTerrain[x][y] = "null";
                    gridIcons[x][y] = modal.iconCzar.getIconByName("null");
                } else {
                    gridTerrain[x][y] = oldTerrain[x-1][y-1];
                    gridIcons[x][y] = oldIcons[x-1][y-1];
                }
            }
        }
    }

    public void setToolStart(int tool) {
        this.tool = tool;
        toolX = cursorx;
        toolY = cursory;
    }

    void releaseTool() {
        tool = 0;
        int minx = Math.min(toolX, cursorx);
        int maxx = Math.max(toolX, cursorx);
        int miny = Math.min(toolY, cursory);
        int maxy = Math.max(toolY, cursory);
        toolX = minx;
        toolY = miny;
        toolFinishX = maxx;
        toolFinishY = maxy;
        modal.widgetChanged(this);
    }
}
