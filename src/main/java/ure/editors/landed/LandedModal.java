package ure.editors.landed;

import ure.areas.UArea;
import ure.areas.gen.Layer;
import ure.areas.gen.Metascaper;
import ure.areas.gen.shapers.*;
import ure.math.UColor;
import ure.ui.ULight;
import ure.ui.modals.UModalLoading;
import ure.ui.modals.UModalTabs;
import ure.ui.modals.widgets.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class LandedModal extends UModalTabs {

    UArea area;
    Metascaper scaper;

    String[] shaperNames;

    ArrayList<Layer> layers;
    ArrayList<HashMap<String,Shaper>> layerShapers;
    int layerIndex;
    Layer layer;

    ArrayList<ULight> roomLights;

    WidgetDropdown layerPicker;
    HashMap<String,Widget> shaperWidgets;

    WidgetHSlider densitySlider;
    WidgetRadio pruneRadio, wipeRadio, roundRadio, invertRadio;
    WidgetDropdown shaperPicker;

    WidgetHSlider areaWidthSlider, areaHeightSlider;
    WidgetTerrainpick fillPicker, terrainPicker, doorPicker;
    WidgetHSlider doorSlider;
    WidgetDropdown drawPicker;



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
        this.area = area;

        layers = new ArrayList<>();
        shaperNames = new String[]{
                "Caves",
                "Mines",
                "Growdungeon",
                "Chambers",
                "Ruins",
                "Convochain"
        };
        layerShapers = new ArrayList<>();
        shaperWidgets = new HashMap<>();

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


        changeTab("Layers");
        layerPicker = new WidgetDropdown(this, 0, 0, new String[]{"<new layer>"}, 0);
        addWidget(layerPicker);

        shaperPicker = new WidgetDropdown(this, 9, 0, shaperNames, 0);
        addWidget(shaperPicker);
        terrainPicker = new WidgetTerrainpick(this, 0, 2, "terrain:", "floor");
        addWidget(terrainPicker);
        addWidget(new WidgetText(this, 0, 3, "Draw on:"));
        drawPicker = new WidgetDropdown(this, 5, 3, new String[]{"All", "Walls only", "Floors only"}, 0);
        addWidget(drawPicker);

        makeShaperWidgets();

        densitySlider = new WidgetHSlider(this, 0, 27, "density", 6, 100, 0, 100, true);
        pruneRadio = new WidgetRadio(this, 0, 29, "prune dead ends", null, null, true);
        wipeRadio = new WidgetRadio(this,0,30,"wipe small regions", null, null, true);
        roundRadio = new WidgetRadio(this, 0, 31, "round corners", null, null, false);
        invertRadio = new WidgetRadio(this, 0, 32, "invert", null, null, false);
        addWidget(densitySlider);
        addWidget(pruneRadio);
        addWidget(wipeRadio);
        addWidget(roundRadio);
        addWidget(invertRadio);


        changeTab("Decorate");

        doorSlider = new WidgetHSlider(this,  0, 0,"door chance", 6, 0, 0, 100, true);
        addWidget(doorSlider);
        doorPicker = new WidgetTerrainpick(this, 0, 2, "door type:", "door");
        addWidget(doorPicker);

        lightChanceSlider = new WidgetHSlider(this, 0, 4, "room light chance", 6, 0, 0, 100, true);
        addWidget(lightChanceSlider);
        lightNewAmbient = new WidgetButton(this, 0, 6, "[ New Ambient ]", null);
        addWidget(lightNewAmbient);
        lightNewPoint = new WidgetButton(this, 10, 6, "[ New Point ]", null);
        addWidget(lightNewPoint);
        lightList = new WidgetListVert(this, 0, 7, new String[]{});
        addWidget(lightList);

        changeTab(null);

        tabSlider = new WidgetSlideTabs(this, 0, 36, 20, tabList(), 0);
        addWidget(tabSlider);

        sizeToWidgets();

        escapable = false;
        setChildPosition(commander.camera().columns - cellw - 2, commander.camera().rows - cellh - 2, commander.camera());

        scaper = new Metascaper();
        roomLights = new ArrayList<>();

        changeTab("Layers");
        makeNewLayer();
        changeTab("Global");
    }

    void removeShaperWidgets() {
        for (Widget w : shaperWidgets.values())
            removeWidget(w);
        shaperWidgets.clear();
    }
    void makeShaperWidgets() {
        if (layer == null) return;
        if (layer.shaper == null) return;
        int y = 6;
        ArrayList<String> ni = new ArrayList<>();
        for (String pi : layer.shaper.paramsI.keySet())
            ni.add(pi);
        Collections.sort(ni);
        for (String pi : ni) {
            Widget w = new WidgetHSlider(this, 0, y, pi, 8, layer.shaper.paramsI.get(pi), layer.shaper.paramsImin.get(pi), layer.shaper.paramsImax.get(pi), true);
            shaperWidgets.put(pi, w);
            addWidget(w);
            y += 1;
        }
        ArrayList<String> nf = new ArrayList<>();
        for (String pf : layer.shaper.paramsF.keySet())
            nf.add(pf);
        Collections.sort(nf);
        for (String pf : nf) {
            Widget w = new WidgetHSlider(this, 0, y, pf, 8, (int)(layer.shaper.paramsF.get(pf)*100), (int)(layer.shaper.paramsFmin.get(pf)*100), (int)(layer.shaper.paramsFmax.get(pf)*100), true);
            shaperWidgets.put(pf, w);
            addWidget(w);
            y += 1;
        }
    }

    void makeNewLayer() {
        Layer layer = new Layer();
        layers.add(layer);
        HashMap<String,Shaper> shapers = new HashMap<>();
        shapers.put("Caves", new Caves(area.xsize-2,area.ysize-2));
        shapers.put("Mines", new Mines(area.xsize-2,area.ysize-2));
        shapers.put("Growdungeon", new Growdungeon(area.xsize-2,area.ysize-2));
        shapers.put("Chambers", new Chambers(area.xsize-2,area.ysize-2));
        shapers.put("Convochain", new Convochain(area.xsize-2,area.ysize-2));
        layerShapers.add(shapers);
        layer.shaper = shapers.get("Caves");
        layer.terrain = "null";
        layer.density = 1f;
        updateLayerPicker();
        selectLayer(layers.indexOf(layer));
    }

    void updateLayerPicker() {
        String[] choices = new String[layers.size()+1];
        int i = 0;
        for (Layer layer : layers) {
            choices[i] = "Layer " + i + ": " + layer.terrain;
            i++;
        }
        choices[layers.size()] = "<new layer>";
        layerPicker.setChoices(choices);
    }

    @Override
    public void pressWidget(Widget widget) {
        if (widget == regenButton)
            regenerate();
        else if (widget == quitButton)
            quit();
        else if (widget == pruneRadio) {
            pruneRadio.on = !pruneRadio.on;
            layer.pruneDeadEnds = pruneRadio.on;
        } else if (widget == wipeRadio) {
            wipeRadio.on = !wipeRadio.on;
            layer.wipeSmallRegions = wipeRadio.on;
        } else if (widget == roundRadio) {
            roundRadio.on = !roundRadio.on;
            layer.roundCorners = roundRadio.on;
        } else if (widget == invertRadio) {
            invertRadio.on = !invertRadio.on;
            layer.invert = invertRadio.on;
        }else if (widget == lightNewAmbient)
            makeNewLight(ULight.AMBIENT);
        else if (widget == lightNewPoint)
            makeNewLight(ULight.POINT);
    }

    @Override
    public void widgetChanged(Widget widget) {
        if (widget == shaperPicker) {
            selectShaper(shaperPicker.selected());
        } else if (widget == tabSlider) {
            changeTab(tabSlider.tabs.get(tabSlider.selection));
        } else if (widget == layerPicker) {
            if (layerPicker.selected().equals("<new layer>"))
                makeNewLayer();
            else
                selectLayer(layerPicker.selection);
        } else if (widget == terrainPicker) {
            layer.terrain = terrainPicker.selection;
        } else if (widget == drawPicker) {
            layer.printMode = drawPicker.selection;
        } else if (widget == densitySlider) {
            layer.density = (float)densitySlider.value / 100f;
        } else if (shaperWidgets.containsValue(widget)) {
            updateShaperFromWidgets();
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

    void selectShaper(String selection) {
        removeShaperWidgets();
        layer.shaper = layerShapers.get(layerIndex).get(selection);
        makeShaperWidgets();
        regenerate();
    }

    void selectLayer(int selection) {
        layerPicker.selection = selection;
        removeShaperWidgets();
        layerIndex = selection;
        layer = layers.get(layerIndex);
        int shaperIndex = 0;
        shaperPicker.selectChoice(layer.shaper.name);
        makeShaperWidgets();
        pruneRadio.on = layer.pruneDeadEnds;
        wipeRadio.on = layer.wipeSmallRegions;
        roundRadio.on = layer.roundCorners;
        invertRadio.on = layer.invert;
        terrainPicker.selection = layer.terrain;
    }

    void updateShaperFromWidgets() {
        for (String pi : layer.shaper.paramsI.keySet()) {
            int val = ((WidgetHSlider)(shaperWidgets.get(pi))).value;
            layer.shaper.paramsI.put(pi, val);
        }
        for (String pf : layer.shaper.paramsF.keySet()) {
            int val = ((WidgetHSlider)(shaperWidgets.get(pf))).value;
            layer.shaper.paramsF.put(pf, ((float)val)*0.01f);
        }
    }

    void regenerate() {
        //if (true) return;
        UModalLoading lmodal = new UModalLoading();
        lmodal.setChildPosition(2,2,commander.camera());
        commander.showModal(lmodal);
        commander.renderer.render();

        if (area.xsize != areaWidthSlider.value || area.ysize != areaHeightSlider.value) {
            area.initialize(areaWidthSlider.value, areaHeightSlider.value, fillPicker.selection);
            layer.shaper.resize(areaWidthSlider.value-2, areaHeightSlider.value-2);
        }
        scaper.setup(layers, fillPicker.selection, doorPicker.selection, (float)(doorSlider.value)/100f, (float)(lightChanceSlider.value)/100f, roomLights);
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
