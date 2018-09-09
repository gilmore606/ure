package ure.editors.landed;

import ure.commands.UCommand;
import ure.sys.GLKey;
import ure.terrain.UTerrain;
import ure.ui.Icons.Icon;
import ure.ui.modals.HearModalDropdown;
import ure.ui.modals.UModal;
import ure.ui.modals.UModalDropdown;
import ure.ui.modals.widgets.Widget;

import java.util.HashMap;

public class WidgetTerrainpick extends Widget implements HearModalDropdown {

    public String selection;

    String label;

    HashMap<String, Icon> icons;
    String[] names;

    public WidgetTerrainpick(UModal modal, int x, int y, String label, String selection) {
        super(modal);
        this.label = label;
        focusable = true;
        this.selection = selection;
        setDimensions(x,y,modal.textWidth(label) + 6, 1);

        icons = new HashMap<>();
        names = new String[modal.terrainCzar.getAllTerrains().size()];
        int i = 0;
        for (UTerrain t : modal.terrainCzar.getAllTerrainTemplates()) {
            icons.put(t.getName(), t.icon());
            names[i] = t.getName();
            i++;
        }
    }

    @Override
    public void drawMe() {
        if (label != null)
            drawString(label, 0, 0, focused ? null : grayColor());
        drawIcon(icons.get(selection), modal.textWidth(label) + 1, 0);
        drawString(selection, modal.textWidth(label)+3, 0, focused ? null : grayColor());
    }

    @Override
    public void mouseClick(int mousex, int mousey) { showPicker(); }
    @Override
    public void hearCommand(UCommand c, GLKey k) {
        if (c != null)
            if (c.id.equals("PASS"))
                showPicker();
    }

    void showPicker() {
        UModalDropdown dmodal = new UModalDropdown(names, 0, this, "");
        dmodal.setChildPosition(col, row, modal);
        modal.commander.showModal(dmodal);
    }

    public void hearModalDropdown(String context, int selection) {
        this.selection = names[selection];
    }
}
