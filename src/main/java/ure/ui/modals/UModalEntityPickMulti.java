package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.Entity;
import ure.sys.GLKey;
import ure.ui.Icons.Icon;
import ure.ui.modals.widgets.WidgetButton;
import ure.ui.modals.widgets.WidgetEntityDetail;
import ure.ui.modals.widgets.WidgetListVert;
import ure.ui.modals.widgets.WidgetText;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

public class UModalEntityPickMulti extends UModal {

    int selection = 0;
    ArrayList<Entity> entities;
    ArrayList<Boolean> selectedEntities;

    WidgetText headerWidget;
    WidgetListVert listWidget;
    WidgetEntityDetail detailWidget;
    WidgetButton okButton;
    WidgetButton allButton;

    public UModalEntityPickMulti(String _prompt, ArrayList<Entity> _entities, boolean _showDetail, HearModalEntityPickMulti _callback, String _callbackContext) {
        super(_callback, _callbackContext);
        entities = _entities;
        selectedEntities = new ArrayList<>();
        for (int i=0;i<entities.size();i++) {
            selectedEntities.add(false);
        }
        String[] names = new String[entities.size()];
        Icon[] icons = new Icon[entities.size()];
        int i = 0;
        for (Entity e : entities) {
            names[i] = e.name();
            icons[i] = e.icon();
            i++;
        }

        headerWidget = new WidgetText(this,0,0,_prompt);
        listWidget = new WidgetListVert(this, 0, headerWidget.h + 1, names);
        listWidget.addIcons(icons);
        okButton = new WidgetButton(this, 0, headerWidget.h + listWidget.h + 2, "[ OK ]", null);
        allButton = new WidgetButton(this, okButton.w + 1, okButton.y, "[ Take all ]", null);
        addWidget(headerWidget);
        addWidget(listWidget);
        addWidget(okButton);
        addWidget(allButton);
        if (_showDetail) {
            detailWidget = new WidgetEntityDetail(this, listWidget.w + 1, headerWidget.h + 1);
            addWidget(detailWidget);
        }
        sizeToWidgets();
    }

    @Override
    public void hearCommand(UCommand command, GLKey k) {
        if (command != null) {
            if (command.id.equals("PASS")) {
                selectEntity();
            }
        } else if (k.k == GLFW_KEY_ENTER || k.k == GLFW_KEY_KP_ENTER) {
            completeSelection();
        } else if (k.k == GLFW_KEY_A && k.shift) {
            selectAll();
        }
        super.hearCommand(command, k);
    }

    @Override
    public void mouseClick() { selectEntity(); }

    void selectEntity() {
        selectedEntities.set(selection, !selectedEntities.get(selection));
    }

    void completeSelection() {
        dismiss();
        ArrayList<Entity> selected = new ArrayList<>();
        int i = 0;
        for (Entity entity : entities) {
            if (selectedEntities.get(i))
                selected.add(entity);
            i++;
        }
        ((HearModalEntityPickMulti)callback).hearModalEntityPickMulti(callbackContext, selected);
    }

    void selectAll() {
        boolean allAlready = true;
        for (int i = 0;i<selectedEntities.size();i++) {
            if (!selectedEntities.get(i))
                allAlready = false;
        }
        for (int i = 0;i<selectedEntities.size();i++) {
            selectedEntities.set(i, allAlready ? false : true);
        }
    }
}
