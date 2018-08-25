package ure.ui.modals;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.Entity;
import ure.sys.GLKey;
import ure.ui.Icons.Icon;
import ure.ui.modals.widgets.*;

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
    public void pressWidget(Widget widget) {
        if (widget == allButton) {
            allButton.dismissFlash = true;
            selectChoices(entities);
        } else if (widget == okButton) {
            okButton.dismissFlash = true;
            selectChoices(collectChoices());
        } else if (widget == listWidget) {
            listWidget.toggleOption(listWidget.selection);
        }
    }

    ArrayList<Entity> collectChoices() {
        ArrayList<Entity> choices = new ArrayList<>();
        for (int i=0;i<entities.size();i++) {
            if (listWidget.lit(i))
                choices.add(entities.get(i));
        }
        return choices;
    }

    void selectChoices(ArrayList<Entity> choices) {
        dismiss();
        ((HearModalEntityPickMulti)callback).hearModalEntityPickMulti(callbackContext, choices);
    }
}
