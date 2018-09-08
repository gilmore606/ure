package ure.editors.landed;

import ure.areas.UArea;
import ure.areas.gen.Metascaper;
import ure.areas.gen.Shape;
import ure.areas.gen.ULandscaper;
import ure.areas.gen.shapers.Caves;
import ure.areas.gen.shapers.Growdungeon;
import ure.areas.gen.shapers.Mines;
import ure.areas.gen.shapers.Shaper;
import ure.ui.modals.UModal;
import ure.ui.modals.widgets.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;

public class LandedModal extends UModal {

    UArea area;
    Metascaper scaper;

    Shaper shaper;

    Shaper[] shapers;
    String[] shaperNames;

    String floorTerrain, wallTerrain;

    WidgetDropdown shaperDropdown;
    WidgetButton regenButton;
    WidgetButton quitButton;

    HashMap<String,Widget> shaperWidgets;
    HashMap<String,Widget> shaperNameWidgets;

    public LandedModal(UArea area) {
        super(null, "");

        shapers = new Shaper[]{
                new Caves(area.xsize,area.ysize),
                new Mines(area.xsize,area.ysize),
                new Growdungeon(area.xsize,area.ysize)
        };
        shaperNames = new String[]{
                "Caves",
                "Mines",
                "Growdungeon"
        };

        this.area = area;
        this.shaper = shapers[0];

        shaperDropdown = new WidgetDropdown(this, 0, 28, shaperNames, 0);
        addWidget(shaperDropdown);

        regenButton = new WidgetButton(this, 0, 30, "[ Regenerate ]", null);
        addWidget(regenButton);
        quitButton = new WidgetButton(this, 0, 31, "[ Quit ]", null);
        addWidget(quitButton);

        shaperWidgets = new HashMap<>();
        shaperNameWidgets = new HashMap<>();
        makeShaperWidgets();

        sizeToWidgets();

        escapable = false;
        setChildPosition(commander.camera().columns - cellw - 2, commander.camera().rows - cellh - 2, commander.camera());

        scaper = new Metascaper();
        wallTerrain = "rock";
        floorTerrain = "floor";
    }

    void remakeShaperWidgets() {
        for (Widget w : shaperWidgets.values())
            removeWidget(w);
        for (Widget t : shaperNameWidgets.values())
            removeWidget(t);
        shaperWidgets.clear();
        shaperNameWidgets.clear();
        makeShaperWidgets();
    }
    void makeShaperWidgets() {
        int y = 0;
        for (String pi : shaper.paramsI.keySet()) {
            Widget w = new WidgetHSlider(this, 0, y, 8, shaper.paramsI.get(pi), shaper.paramsImin.get(pi), shaper.paramsImax.get(pi), true);
            shaperWidgets.put(pi, w);
            addWidget(w);
            Widget t = new WidgetText(this, 0, y+1, pi);
            shaperNameWidgets.put(pi, t);
            addWidget(t);
            y += 2;
        }
    }

    @Override
    public void pressWidget(Widget widget) {
        if (widget == regenButton)
            regenerate();
        else if (widget == quitButton)
            quit();
    }

    @Override
    public void widgetChanged(Widget widget) {
        if (widget == shaperDropdown) {
            selectShaper(shaperDropdown.selection);
        }
    }

    void selectShaper(int selection) {
        shaper = shapers[selection];
        remakeShaperWidgets();
        regenerate();
    }

    void regenerate() {
        for (String pi : shaper.paramsI.keySet()) {
            int val = ((WidgetHSlider)(shaperWidgets.get(pi))).value;
            shaper.paramsI.put(pi, val);
        }
        scaper.setup(shaper, wallTerrain, floorTerrain);
        shaper.build();
        scaper.buildArea(area, 1, new String[]{});
        commander.camera().renderLights();
    }

    void quit() {
        escape();
        commander.game().setupTitleScreen();
    }
}
