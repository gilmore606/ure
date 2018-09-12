package ure.editors.landed;

import ure.areas.UArea;
import ure.areas.gen.Metascaper;
import ure.areas.gen.Shape;
import ure.areas.gen.ULandscaper;
import ure.areas.gen.shapers.*;
import ure.math.UColor;
import ure.ui.ULight;
import ure.ui.modals.UModal;
import ure.ui.modals.UModalLoading;
import ure.ui.modals.UModalTabs;
import ure.ui.modals.widgets.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class LandedModal extends UModalTabs {

    UArea area;
    Metascaper scaper;

    Shaper shaper;

    Shaper[] shapers;
    String[] shaperNames;

    ArrayList<ULight> roomLights;

    WidgetRadio pruneRadio, wipeRadio, roundRadio;
    WidgetDropdown shaperDropdown;

    WidgetHSlider areaWidthSlider, areaHeightSlider;
    WidgetTerrainpick fillPicker, floorPicker, doorPicker;
    WidgetHSlider doorSlider;

    HashMap<String,Widget> shaperWidgets;

    WidgetHSlider lightChanceSlider;
    WidgetButton lightNewAmbient, lightNewPoint;
    WidgetListVert lightList;

    WidgetSlideTabs tabSlider;
    WidgetButton regenButton;
    WidgetButton quitButton;

    boolean dragging = false;
    int dragStartX, dragStartY;
    int dragCenterX, dragCenterY;

    public LandedModal(UArea area) {
        super(null, "");

        shapers = new Shaper[]{
                new Caves(area.xsize-2,area.ysize-2),
                new Mines(area.xsize-2,area.ysize-2),
                new Growdungeon(area.xsize-2,area.ysize-2),
                new Chambers(area.xsize-2,area.ysize-2),
                new Ruins(area.xsize-2,area.ysize-2),
                new Convochain(area.xsize-2,area.ysize-2)
        };
        shaperNames = new String[]{
                "Caves",
                "Mines",
                "Growdungeon",
                "Chambers",
                "Ruins",
                "Convochain"
        };

        this.area = area;
        this.shaper = shapers[0];

        regenButton = new WidgetButton(this, 0, 34, "[ Regenerate ]", null);
        addWidget(regenButton);
        quitButton = new WidgetButton(this, 16, 34, "[ Quit ]", null);
        addWidget(quitButton);


        changeTab("Global");

        fillPicker = new WidgetTerrainpick(this, 0, 0, "fill:", "rock");
        addWidget(fillPicker);
        areaWidthSlider = new WidgetHSlider(this, 0, 2, "width", 6, 100, 40, 200, true);
        areaHeightSlider = new WidgetHSlider(this, 0, 3, "height", 6, 100, 40, 200, true);
        addWidget(areaWidthSlider);
        addWidget(areaHeightSlider);


        changeTab("Shape");

        floorPicker = new WidgetTerrainpick(this, 8, 0, "floor:", "floor");
        addWidget(floorPicker);

        shaperDropdown = new WidgetDropdown(this, 0, 28, shaperNames, 0);
        addWidget(shaperDropdown);
        shaperWidgets = new HashMap<>();
        makeShaperWidgets();

        pruneRadio = new WidgetRadio(this, 0, 29, "prune dead ends", null, null, true);
        wipeRadio = new WidgetRadio(this,0,30,"wipe small regions", null, null, true);
        roundRadio = new WidgetRadio(this, 0, 31, "round corners", null, null, false);
        addWidget(pruneRadio);
        addWidget(wipeRadio);
        addWidget(roundRadio);


        changeTab("Decorate");

        doorSlider = new WidgetHSlider(this,  0, 32,"door chance", 6, 0, 0, 100, true);
        addWidget(doorSlider);
        doorPicker = new WidgetTerrainpick(this, 15, 32, "type:", "door");
        addWidget(doorPicker);


        changeTab("Lights");

        lightChanceSlider = new WidgetHSlider(this, 0, 0, "room light chance", 6, 0, 0, 100, true);
        addWidget(lightChanceSlider);
        lightNewAmbient = new WidgetButton(this, 0, 2, "[ New Ambient ]", null);
        addWidget(lightNewAmbient);
        lightNewPoint = new WidgetButton(this, 12, 2, "[ New Point ]", null);
        addWidget(lightNewPoint);
        lightList = new WidgetListVert(this, 0, 4, new String[]{});
        addWidget(lightList);

        changeTab(null);

        tabSlider = new WidgetSlideTabs(this, 0, 36, 20, tabList(), 0);
        addWidget(tabSlider);

        sizeToWidgets();

        escapable = false;
        setChildPosition(commander.camera().columns - cellw - 2, commander.camera().rows - cellh - 2, commander.camera());

        scaper = new Metascaper();
        roomLights = new ArrayList<>();

        changeTab("Global");
    }

    void remakeShaperWidgets() {
        for (Widget w : shaperWidgets.values())
            removeWidget(w);
        shaperWidgets.clear();
        makeShaperWidgets();
    }
    void makeShaperWidgets() {
        int y = 2;
        ArrayList<String> ni = new ArrayList<>();
        for (String pi : shaper.paramsI.keySet())
            ni.add(pi);
        Collections.sort(ni);
        for (String pi : ni) {
            Widget w = new WidgetHSlider(this, 0, y, pi, 8, shaper.paramsI.get(pi), shaper.paramsImin.get(pi), shaper.paramsImax.get(pi), true);
            shaperWidgets.put(pi, w);
            addWidget(w);
            y += 1;
        }
        ArrayList<String> nf = new ArrayList<>();
        for (String pf : shaper.paramsF.keySet())
            nf.add(pf);
        Collections.sort(nf);
        for (String pf : nf) {
            Widget w = new WidgetHSlider(this, 0, y, pf, 8, (int)(shaper.paramsF.get(pf)*100), (int)(shaper.paramsFmin.get(pf)*100), (int)(shaper.paramsFmax.get(pf)*100), true);
            shaperWidgets.put(pf, w);
            addWidget(w);
            y += 1;
        }
    }

    @Override
    public void pressWidget(Widget widget) {
        if (widget == regenButton)
            regenerate();
        else if (widget == quitButton)
            quit();
        else if (widget == pruneRadio)
            pruneRadio.on = !pruneRadio.on;
        else if (widget == wipeRadio)
            wipeRadio.on = !wipeRadio.on;
        else if (widget == roundRadio)
            roundRadio.on = !roundRadio.on;
        else if (widget == lightNewAmbient)
            makeNewLight(ULight.AMBIENT);
        else if (widget == lightNewPoint)
            makeNewLight(ULight.POINT);
    }

    @Override
    public void widgetChanged(Widget widget) {
        if (widget == shaperDropdown) {
            selectShaper(shaperDropdown.selection);
        } else if (widget == tabSlider) {
            changeTab(tabSlider.tabs.get(tabSlider.selection));
        }
    }

    @Override
    public void mouseClick() {
        if (isMouseInside())
            super.mouseClick();
        else {
            dragging = true;
            dragCenterX = commander.camera().getCenterColumn();
            dragCenterY = commander.camera().getCenterRow();
            dragStartX = commander.mouseX() - absoluteX();
            dragStartY = commander.mouseY() - absoluteY();
        }
    }

    @Override
    public void animationTick() {
        super.animationTick();
        if (dragging) {
            if (!commander.mouseButton()) {
                dragging = false;
            } else {
                int mouseX = dragStartX - (commander.mouseX() - absoluteX());
                int mouseY = dragStartY - (commander.mouseY() - absoluteY());
                commander.camera().moveTo(dragCenterX + (mouseX / gw()),
                                            dragCenterY + (mouseY / gh()));
            }
        }
    }

    void selectShaper(int selection) {
        shaper = shapers[selection];
        remakeShaperWidgets();
        regenerate();
    }

    void regenerate() {
        UModalLoading lmodal = new UModalLoading();
        lmodal.setChildPosition(2,2,commander.camera());
        commander.showModal(lmodal);
        commander.renderer.render();

        if (area.xsize != areaWidthSlider.value || area.ysize != areaHeightSlider.value) {
            area.initialize(areaWidthSlider.value, areaHeightSlider.value, fillPicker.selection);
            shaper.resize(areaWidthSlider.value-2, areaHeightSlider.value-2);
        }
        for (String pi : shaper.paramsI.keySet()) {
            int val = ((WidgetHSlider)(shaperWidgets.get(pi))).value;
            shaper.paramsI.put(pi, val);
        }
        for (String pf : shaper.paramsF.keySet()) {
            int val = ((WidgetHSlider)(shaperWidgets.get(pf))).value;
            shaper.paramsF.put(pf, ((float)val)*0.01f);
        }
        scaper.setup(shaper, fillPicker.selection, floorPicker.selection, pruneRadio.on, wipeRadio.on, roundRadio.on, doorPicker.selection, (float)(doorSlider.value)/100f, (float)(lightChanceSlider.value)/100f, roomLights);
        shaper.build();
        scaper.buildArea(area, 1, new String[]{});

        commander.camera().renderLights();
        commander.detachModal(lmodal);
    }

    void quit() {
        escape();
        commander.game().setupTitleScreen();
    }

    void makeNewLight(int type) {
        ULight light = new ULight(UColor.WHITE, 100, 100);
        if (type == ULight.AMBIENT) {
            light.makeAmbient(1, 1);
        }
        light.setPermanent(true);
        roomLights.add(light);
        updateLightList();
    }

    void updateLightList() {
        String[] lightNames = new String[roomLights.size()];
        int i=0;
        for (ULight l : roomLights) {
            String n = "";
            if (l.type == ULight.AMBIENT)
                n = "ambient light";
            else
                n = "point light";
            lightNames[i] = n;
            i++;
        }
        lightList.setOptions(lightNames);
    }
}
