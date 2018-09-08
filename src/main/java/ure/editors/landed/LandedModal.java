package ure.editors.landed;

import ure.areas.UArea;
import ure.areas.gen.Metascaper;
import ure.areas.gen.Shape;
import ure.areas.gen.ULandscaper;
import ure.areas.gen.shapers.Caves;
import ure.ui.modals.UModal;
import ure.ui.modals.widgets.Widget;
import ure.ui.modals.widgets.WidgetButton;

import javax.inject.Inject;

public class LandedModal extends UModal {

    UArea area;
    Metascaper scaper;

    Shape shaper;

    String floorTerrain, wallTerrain;

    WidgetButton regenButton;
    WidgetButton quitButton;

    public LandedModal(UArea area) {
        super(null, "");
        this.area = area;

        regenButton = new WidgetButton(this, 0, 0, "[ Regenerate ]", null);
        addWidget(regenButton);
        quitButton = new WidgetButton(this, 10, 0, "[ Quit ]", null);
        addWidget(quitButton);
        sizeToWidgets();

        escapable = false;
        setChildPosition(commander.camera().columns - cellw - 2, commander.camera().rows - cellh - 2, commander.camera());

        scaper = new Metascaper();
        wallTerrain = "rock";
        floorTerrain = "floor";
    }

    @Override
    public void pressWidget(Widget widget) {
        if (widget == regenButton)
            regenerate();
        else if (widget == quitButton)
            quit();
    }

    void regenerate() {


        shaper = new Caves(area.xsize,area.ysize,0.45f, 5, 2, 3);
        scaper.setup(shaper, wallTerrain, floorTerrain);
        scaper.buildArea(area, 1, new String[]{});
        commander.camera().renderLights();
    }

    void quit() {
        escape();
        commander.game().setupTitleScreen();
    }
}
