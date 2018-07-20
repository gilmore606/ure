package ure.vaulted;

import com.fasterxml.jackson.databind.ObjectMapper;
import ure.areas.UArea;
import ure.areas.UVault;
import ure.areas.UVaultSet;
import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;
import ure.ui.modals.UModal;

import javax.inject.Inject;
import java.io.PrintWriter;
import java.util.Set;

public class VaultedModal extends UModal {

    @Inject
    ObjectMapper objectMapper;

    VaultedArea area;
    int currentTerrain = 0;
    int nullTerrain;
    int[] terrainPalette;
    String[] terrains;
    String filename;
    UVaultSet vaultSet;
    int cursor;

    public VaultedModal(VaultedArea edarea, String _filename) {
        super(null, "", UColor.COLOR_BLACK);
        area = edarea;
        filename = _filename;
        setDimensions(15,30);
        Set<String> terrainset = terrainCzar.getAllTerrains();
        terrains = new String[terrainset.size()];
        int i = 0;
        int nullt = 0;
        for (String t : terrainset) {
            terrains[i] = t;
            if (t.equals("null"))
                nullt = i;
            i++;
        }
        terrainPalette = new int[]{nullt,nullt,nullt,nullt,nullt,nullt,nullt,nullt,nullt};
        nullTerrain = nullt;
        commander.config.setLightEnable(false);
        commander.config.setVisibilityEnable(false);

        vaultSet = commander.cartographer.loadVaultSet(filename);
        if (vaultSet == null) {
            commander.printScroll("No such file " + filename + ".json -- creating new vaultSet.");
            vaultSet = new UVaultSet();
            vaultSet.initialize();
            vaultSet.setFilename(filename);
        } else {
            commander.printScroll("Opening vaultset " + filename + ".json for editing.");
        }
        loadVault();
    }

    @Override
    public void drawContent(URenderer renderer) {
        drawString(renderer, "q/a : cycle terrains", 1, 15);
        drawString(renderer, "1-9 : palette pick", 1, 16);
        drawString(renderer, "pass: place terrain", 1, 17);
        drawString(renderer, "C   : crop to corner", 1, 18);
        drawString(renderer, "W   : wipe!", 1, 19);
        drawString(renderer, "o/l : cycle vaults", 1, 21);
        drawString(renderer, "O   : add new vault", 1, 22);
        drawString(renderer, "S   : save", 1, 23);
        drawString(renderer, "vault " + Integer.toString(cursor + 1) + " (of " + Integer.toString(vaultSet.size()) + ")", 1, 27);
        drawString(renderer, "'" + vaultSet.vaultAt(cursor).getName() + "'", 1, 28);
        drawString(renderer, filename + ".json", 1, 29);

        drawIcon(renderer, terrainCzar.getTerrainByName(terrains[currentTerrain]).getIcon(), 1, 1);
        drawString(renderer, terrains[currentTerrain], 3, 1);
        for (int i=0;i<terrainPalette.length;i++) {
            drawString(renderer, Integer.toString(i+1), 1, 3+i);
            drawIcon(renderer, terrainCzar.getTerrainByName(terrains[terrainPalette[i]]).getIcon(), 2, 3+i);
            drawString(renderer, terrains[terrainPalette[i]], 4, 3+i);
        }

        drawString(renderer, Integer.toString(commander.player().areaX()) + "," + Integer.toString(commander.player().areaY()), 6, 0);
    }

    @Override
    public void hearCommand(UCommand command, Character c) {
        if (command != null) {
            if (command.id.equals("MOVE_N"))
                commander.player().walkDir(0, -1);
            else if (command.id.equals("MOVE_S"))
                commander.player().walkDir(0, 1);
            else if (command.id.equals("MOVE_W"))
                commander.player().walkDir(-1, 0);
            else if (command.id.equals("MOVE_E"))
                commander.player().walkDir(1, 0);
            else if (command.id.equals("PASS"))
                stampTerrain();
        }
        if (c.equals('q')) {
            currentTerrain++;
            if (currentTerrain >= terrains.length) currentTerrain = 0;
        } else if (c.equals('a')) {
            currentTerrain--;
            if (currentTerrain < 0) currentTerrain = terrains.length - 1;
        } else if (c.equals('1'))
            currentTerrain = terrainPalette[0];
        else if (c.equals('2'))
            currentTerrain = terrainPalette[1];
        else if (c.equals('3'))
            currentTerrain = terrainPalette[2];
        else if (c.equals('4'))
            currentTerrain = terrainPalette[3];
        else if (c.equals('5'))
            currentTerrain = terrainPalette[4];
        else if (c.equals('6'))
            currentTerrain = terrainPalette[5];
        else if (c.equals('7'))
            currentTerrain = terrainPalette[6];
        else if (c.equals('8'))
            currentTerrain = terrainPalette[7];
        else if (c.equals('9'))
            currentTerrain = terrainPalette[8];
        else if (c.equals('C'))
            cropToCorner();
        else if (c.equals('W'))
            wipeAll();
        else if (c.equals('S'))
            writeFile();
        else if (c.equals('o'))
            switchVault(-1);
        else if (c.equals('l'))
            switchVault(1);
        else if (c.equals('O'))
            addNewVault();

    }

    void stampTerrain() {
        area.setTerrain(commander.player().areaX(), commander.player().areaY(), terrains[currentTerrain]);
        for (int i=0;i<terrainPalette.length;i++) {
            if (terrainPalette[i] == currentTerrain)
                return;
        }
        for (int i=0;i<terrainPalette.length;i++) {
            if (terrainPalette[i] == nullTerrain) {
                terrainPalette[i] = currentTerrain;
                return;
            }
        }
    }

    void cropToCorner() {
        int xsize = commander.player().areaX()+1;
        int ysize = commander.player().areaY()+1;
        area.cropSize(xsize,ysize);
        vaultSet.vaultAt(cursor).cropSize(xsize,ysize);
        saveVault();
    }

    void wipeAll() {
        for (int x=0;x<area.xsize;x++) {
            for (int y=0;y<area.ysize;y++) {
                area.setTerrain(x,y,terrains[currentTerrain]);
            }
        }
    }

    void writeFile() {
        vaultSet.persist(commander.config.getResourcePath() + "vaults/" + filename + ".json");
        commander.printScroll("Saved vaultset " + filename + ".json to resources.");
    }

    void loadVault() {
        area.loadVault(vaultSet.vaultAt(cursor));
    }

    void saveVault() {
        area.saveVault(vaultSet.vaultAt(cursor));
    }

    void switchVault(int delta) {
        saveVault();
        cursor += delta;
        if (cursor < 0)
            cursor = vaultSet.size()-1;
        if (cursor >= vaultSet.size())
            cursor = 0;
        loadVault();
    }

    void addNewVault() {
        saveVault();
        vaultSet.addVault();
        cursor = vaultSet.size()-1;
        loadVault();
    }
}
