package ure.editors.landed;

import ure.areas.UArea;
import ure.areas.gen.Metascaper;
import ure.areas.gen.Shape;
import ure.areas.gen.ULandscaper;
import ure.areas.gen.shapers.Caves;
import ure.areas.gen.shapers.Growdungeon;
import ure.ui.modals.UModal;
import ure.ui.modals.widgets.Widget;
import ure.ui.modals.widgets.WidgetButton;
import ure.ui.modals.widgets.WidgetHSlider;
import ure.ui.modals.widgets.WidgetText;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;

public class LandedModal extends UModal {

    UArea area;
    Metascaper scaper;

    Growdungeon shaper;

    String floorTerrain, wallTerrain;

    WidgetButton regenButton;
    WidgetButton quitButton;

    HashMap<String,Widget> shaperWidgets;

    public LandedModal(UArea area) {
        super(null, "");
        this.area = area;
        this.shaper = new Growdungeon(area.xsize-2,area.ysize-2);

        regenButton = new WidgetButton(this, 0, 30, "[ Regenerate ]", null);
        addWidget(regenButton);
        quitButton = new WidgetButton(this, 0, 31, "[ Quit ]", null);
        addWidget(quitButton);

        shaperWidgets = new HashMap<>();
        makeShaperWidgets();

        sizeToWidgets();

        escapable = false;
        setChildPosition(commander.camera().columns - cellw - 2, commander.camera().rows - cellh - 2, commander.camera());

        scaper = new Metascaper();
        wallTerrain = "rock";
        floorTerrain = "floor";
    }

    void makeShaperWidgets() {
        int y = 0;
        for (String pi : shaper.paramsI.keySet()) {
            Widget w = new WidgetHSlider(this, 0, y, 8, shaper.paramsI.get(pi), shaper.paramsImin.get(pi), shaper.paramsImax.get(pi), true);
            shaperWidgets.put(pi, w);
            addWidget(w);
            Widget t = new WidgetText(this, 0, y+1, pi);
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

    void regenerate() {
        //shaper = new Caves(area.xsize,area.ysize,0.45f, 5, 2, 3);
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
