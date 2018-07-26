package ure.areas.vaulted;

import ure.actions.UAction;
import ure.actors.UPlayer;
import ure.areas.UArea;
import ure.areas.UCell;
import ure.areas.UVault;
import ure.math.UColor;
import ure.terrain.UTerrain;
import ure.things.UThing;

public class VaultedArea extends UArea {

    public VaultedArea(int thexsize, int theysize) {
        super(thexsize, theysize, "null");
        label = "vaulted";
        resetSunColorLerps();
        addSunColorLerp(0, UColor.COLOR_WHITE);
        addSunColorLerp(24*60, UColor.COLOR_WHITE);
        setSunColor(1f,1f,1f);
    }

    @Override
    public boolean willAcceptThing(UThing thing, int x, int y) {
        if (isValidXY(x, y)) {
            if (thing instanceof UPlayer) {
                return true;
            }
        }
        return super.willAcceptThing(thing,x,y);
    }

    @Override
    public void wakeCheckAll(int playerx, int playery) {

    }

    @Override
    public void broadcastEvent(UAction action) {

    }

    public void cropSize(int newx, int newy) {
        UCell[][] newcells = new UCell[newx][newy];
        for (int x=0;x<newx;x++) {
            for (int y=0;y<newy;y++) {
                newcells[x][y] = cells[x][y];
            }
        }
        xsize = newx;
        ysize = newy;
    }

    public void loadVault(UVault vault) {
        System.out.println("Loading vault...");
            resize(vault.getCols(), vault.getRows());
        for (int x=0;x<vault.getCols();x++) {
            for (int y=0;y<vault.getRows();y++) {
                setTerrain(x,y,vault.terrainCharAt(x,y));
            }
        }
        commander.player().moveToCell(this, 1,1);
    }


    public void resize(int newx, int newy) {
        xsize = newx;
        ysize = newy;
        for (int x=0;x<xsize;x++) {
            for (int y=0;y<ysize;y++) {
                cells[x][y] = new UCell(this,x,y,terrainCzar.getTerrainForFilechar('@'));
            }
        }
    }

    public void saveVault(UVault vault) {
        System.out.println("Saving vault...");
        String[] tlines = new String[ysize];
        for (int y=0; y<vault.getRows();y++) {
            String line = "";
            for (int x=0;x<vault.getCols();x++) {
                UTerrain t = terrainAt(x,y);
                line += t.getFilechar();
            }
            tlines[y] = line;
        }
        vault.setTerrain(tlines);
    }
}
