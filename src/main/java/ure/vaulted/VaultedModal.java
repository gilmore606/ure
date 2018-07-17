package ure.vaulted;

import ure.areas.UArea;
import ure.commands.UCommand;
import ure.math.UColor;
import ure.render.URenderer;
import ure.ui.modals.UModal;

import java.util.Set;

public class VaultedModal extends UModal {

    UArea area;
    int currentTerrain = 0;
    int nullTerrain;
    int[] terrainPalette;
    String[] terrains;

    public VaultedModal(UArea edarea) {
        super(null, "", UColor.COLOR_BLACK);
        area = edarea;
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
    }

    @Override
    public void drawContent(URenderer renderer) {
        drawString(renderer, "Q/A = cycle terrains", 1, 15);
        drawString(renderer, "1-9 = palette pick", 1, 16);
        drawString(renderer, "pass = place terrain", 1, 17);

        drawIcon(renderer, terrainCzar.getTerrainByName(terrains[currentTerrain]).getIcon(), 1, 1);
        drawString(renderer, terrains[currentTerrain], 3, 1);
        for (int i=0;i<terrainPalette.length;i++) {
            drawString(renderer, Integer.toString(i+1), 1, 3+i);
            drawIcon(renderer, terrainCzar.getTerrainByName(terrains[terrainPalette[i]]).getIcon(), 2, 3+i);
            drawString(renderer, terrains[terrainPalette[i]], 4, 3+i);
        }
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

    }

    public void stampTerrain() {
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
}
