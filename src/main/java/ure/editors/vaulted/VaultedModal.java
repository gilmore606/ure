package ure.editors.vaulted;

import com.fasterxml.jackson.databind.ObjectMapper;
import ure.actors.UPlayer;
import ure.areas.UVaultSet;
import ure.commands.CommandQuit;
import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.GLKey;
import ure.ui.modals.HearModalGetString;
import ure.ui.modals.UModal;
import ure.ui.modals.UModalGetString;

import static org.lwjgl.glfw.GLFW.*;
import javax.inject.Inject;
import java.util.Set;

public class VaultedModal extends UModal implements HearModalGetString {

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
        super(null, "", UColor.BLACK);
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
    public void drawContent() {
        drawString("q/a : cycle terrains", 1, 15);
        drawString("1-9 : palette pick", 1, 16);
        drawString("pass: place terrain", 1, 17);
        drawString("C   : crop to corner", 1, 18);
        drawString("W   : wipe!", 1, 19);
        drawString("pgUp/Dn: cycle vaults", 1, 21);
        drawString("ins    : add new vault", 1, 22);
        drawString("n   : name vault", 1, 23);
        drawString("S   : save", 1, 24);
        drawString("vault " + Integer.toString(cursor + 1) + " (of " + Integer.toString(vaultSet.size()) + ")", 1, 27);
        drawString("'" + vaultSet.vaultAt(cursor).getName() + "'", 1, 28);
        drawString(filename + ".json", 1, 29);

        drawIcon(terrainCzar.getTerrainByName(terrains[currentTerrain]).getIcon(), 1, 1);
        drawString(terrains[currentTerrain], 3, 1);
        for (int i=0;i<terrainPalette.length;i++) {
            drawString(Integer.toString(i+1), 1, 3+i);
            drawIcon(terrainCzar.getTerrainByName(terrains[terrainPalette[i]]).getIcon(), 2, 3+i);
            drawString(terrains[terrainPalette[i]], 4, 3+i);
        }

        drawString(Integer.toString(commander.player().areaX()) + "," + Integer.toString(commander.player().areaY()), 6, 0);
    }

    @Override
    public void hearCommand(UCommand command, GLKey k) {
        if (command != null) {
            if (command.id.equals("MOVE_N"))
                move(0,-1);
            else if (command.id.equals("MOVE_NW"))
                move(-1,-1);
            else if (command.id.equals("MOVE_W"))
                move(-1,0);
            else if (command.id.equals("MOVE_SW"))
                move(-1,1);
            else if (command.id.equals("MOVE_S"))
                move(0,1);
            else if (command.id.equals("MOVE_SE"))
                move(1,1);
            else if (command.id.equals("MOVE_E"))
                move(1,0);
            else if (command.id.equals("MOVE_NE"))
                move(1,-1);
            else if (command.id.equals("PASS"))
                stampTerrain();
        }
        if (k.k == GLFW_KEY_Q && !k.shift) {
            currentTerrain++;
            if (currentTerrain >= terrains.length) currentTerrain = 0;
        } else if (k.k == GLFW_KEY_A) {
            currentTerrain--;
            if (currentTerrain < 0) currentTerrain = terrains.length - 1;
        } else if (k.k >= GLFW_KEY_1 && k.k <= GLFW_KEY_9) {
            currentTerrain = terrainPalette[k.k - GLFW_KEY_1];
        }
        else if (k.k == GLFW_KEY_C && k.shift)
            cropToCorner();
        else if (k.k == GLFW_KEY_W && k.shift)
            wipeAll();
        else if (k.k == GLFW_KEY_S && k.shift)
            writeFile();
        else if (k.k == GLFW_KEY_Q && k.shift)
            quitOut();
        else if (k.k == GLFW_KEY_PAGE_UP)
            switchVault(-1);
        else if (k.k == GLFW_KEY_PAGE_DOWN)
            switchVault(1);
        else if (k.k == GLFW_KEY_INSERT)
            addNewVault();
        else if (k.k == GLFW_KEY_N)
            renameVault();

    }

    @Override
    public void mouseClick() { }
    @Override
    public void mouseRightClick() { }

    void move(int dx, int dy) {
        int x = commander.player().areaX() + dx;
        int y = commander.player().areaY() + dy;
        boolean wrap = commander.config.isWrapSelect();
        if (x < 0)
            x = wrap ? area.xsize-1 : 0;
        if (y < 0)
            y = wrap ? area.ysize-1 : 0;
        if (x >= area.xsize)
            x = wrap ? 0 : area.xsize-1;
        if (y >= area.ysize)
            y = wrap ? 0 : area.ysize-1;
        commander.player().moveToCell(x,y);
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
        vaultSet.vaultAt(cursor).cropSize(xsize,ysize);
        area.cropSize(xsize,ysize);
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

    void renameVault() {
        UModalGetString nmodal = new UModalGetString("Vault name:", 30, true, null, this, "rename");
        commander.showModal(nmodal);
    }

    void quitOut() {
        UCommand quit = new CommandQuit();
        quit.execute((UPlayer)commander.player());
        if (dismissed) {
            commander.config.setLightEnable(true);
        }
    }

    public void hearModalGetString(String context, String input) {
        vaultSet.vaultAt(cursor).setName(input);
    }
}
