package ure.editors.landed;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import ure.areas.UArea;
import ure.areas.gen.*;
import ure.areas.gen.shapers.*;
import ure.math.UColor;
import ure.ui.ULight;
import ure.ui.modals.UModalLoading;
import ure.ui.modals.UModalTabs;
import ure.ui.modals.widgets.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class LandedModal extends UModalTabs {

    UArea area;
    Metascaper scaper;

    String[] shaperNames;

    ArrayList<Layer> layers;
    int layerIndex;
    Layer layer;

    ArrayList<Roomgroup> groups;
    int groupIndex;
    Roomgroup group;

    ArrayList<ULight> roomLights;

    WidgetDropdown vaultSetPicker;
    WidgetStringInput nameWidget;
    WidgetButton layerUpButton, layerDownButton, layerDeleteButton;
    WidgetDropdown layerPicker;
    HashMap<String,Widget> shaperWidgets;

    WidgetHSlider densitySlider;
    WidgetRadio pruneRadio, roundRadio, invertRadio;
    WidgetDropdown shaperPicker;

    WidgetHSlider areaWidthSlider, areaHeightSlider;
    WidgetTerrainpick terrainPicker;
    WidgetDropdown drawPicker;

    WidgetDropdown groupPicker;
    WidgetRadio roomHallsRadio;
    WidgetHSlider roomMinSizeSlider, roomMaxSizeSlider, roomFrequencySlider, roomSeparationSlider, roomCountSlider;
    WidgetTerrainpick roomFloorPicker;
    WidgetButton groupDeleteButton;

    WidgetHSlider lightChanceSlider;
    WidgetButton lightNewAmbient, lightNewPoint;
    WidgetListVert lightList;

    WidgetSlideTabs tabSlider;
    WidgetButton regenButton;
    WidgetRadio autoRegenRadio;
    WidgetButton quitButton;

    UColor hiliteScratch;

    boolean dragging = false;
    int dragStartX, dragStartY;
    int dragCenterX, dragCenterY;

    float hilitePulse;
    boolean hilitePulseReverse;

    public LandedModal(UArea area) {
        super(null, "");
        this.area = area;

        hiliteScratch = new UColor(config.getHiliteColor());
        hilitePulse = 0f;
        hilitePulseReverse = false;
        layers = new ArrayList<>();
        groups = new ArrayList<>();
        shaperNames = new String[]{
                "Fill",
                "Blobs",
                "Roads",
                "Caves",
                "Mines",
                "Growdungeon",
                "Chambers",
                "Ruins",
                "Convochain",
                "Outline",
                "Connector",
                "Doors",
                "Stairs"
        };
        shaperWidgets = new HashMap<>();

        regenButton = new WidgetButton(this, 0, 34, "[ Regenerate ]", null);
        addWidget(regenButton);
        autoRegenRadio = new WidgetRadio(this, 7, 34, "auto", null, null, true);
        addWidget(autoRegenRadio);
        quitButton = new WidgetButton(this, 16, 34, "[ Quit ]", null);
        addWidget(quitButton);


        changeTab("File");
        nameWidget = new WidgetStringInput(this, 0, 0, 15, "???", 40);
        setTitle("???");
        addWidget(nameWidget);
        areaWidthSlider = new WidgetHSlider(this, 0, 2, "width", 6, 100, 40, 200, true);
        areaHeightSlider = new WidgetHSlider(this, 0, 3, "height", 6, 100, 40, 200, true);
        addWidget(areaWidthSlider);
        addWidget(areaHeightSlider);

        changeTab("Layers");
        layerPicker = new WidgetDropdown(this, 0, 0, new String[]{"<new layer>"}, 0);
        addWidget(layerPicker);
        layerUpButton = new WidgetButton(this, 16, 1, "[ Up ]", null);
        layerDownButton = new WidgetButton(this,16,0, "[ Down ]", null);
        layerDeleteButton = new WidgetButton(this, 16, 3, "[ Delete ]", null);
        addWidget(layerUpButton);
        addWidget(layerDownButton);
        addWidget(layerDeleteButton);

        shaperPicker = new WidgetDropdown(this, 0, 1, shaperNames, 0);
        addWidget(shaperPicker);
        terrainPicker = new WidgetTerrainpick(this, 0, 2, "terrain:", "floor");
        addWidget(terrainPicker);
        addWidget(new WidgetText(this, 0, 3, "Draw:"));
        drawPicker = new WidgetDropdown(this, 5, 3, new String[]{"All", "In blocked only", "In unblocked only", "None"}, 0);
        addWidget(drawPicker);

        makeShaperWidgets();

        densitySlider = new WidgetHSlider(this, 0, 27, "density", 6, 100, 0, 100, true);
        pruneRadio = new WidgetRadio(this, 0, 29, "prune dead ends", null, null, true);
        roundRadio = new WidgetRadio(this, 0, 31, "round corners", null, null, false);
        invertRadio = new WidgetRadio(this, 0, 32, "invert", null, null, false);
        addWidget(densitySlider);
        addWidget(pruneRadio);
        addWidget(roundRadio);
        addWidget(invertRadio);


        changeTab("Rooms");
        groupPicker = new WidgetDropdown(this, 0, 0, new String[]{"<new group>"}, 0);
        addWidget(groupPicker);
        groupDeleteButton = new WidgetButton(this, 16, 0, "[ Delete ]", null);
        addWidget(groupDeleteButton);
        roomHallsRadio = new WidgetRadio(this, 0, 2, "+hallways", null, null, false);
        addWidget(roomHallsRadio);
        roomCountSlider = new WidgetHSlider(this, 0, 3, "count", 8, 100, 1, 100, true);
        addWidget(roomCountSlider);
        roomMinSizeSlider = new WidgetHSlider(this, 0, 4, "minSize", 8, 6, 4, 100, true);
        addWidget(roomMinSizeSlider);
        roomMaxSizeSlider = new WidgetHSlider(this, 0, 5, "maxSize", 8, 200, 4, 200, true);
        addWidget(roomMaxSizeSlider);
        roomFrequencySlider = new WidgetHSlider(this, 0, 6, "frequency", 8, 100, 1, 100, false);
        addWidget(roomFrequencySlider);
        roomSeparationSlider = new WidgetHSlider(this, 0, 7, "separation", 8, 0, 0, 40, true);
        addWidget(roomSeparationSlider);
        roomFloorPicker = new WidgetTerrainpick(this, 0, 8, "floorType", "null");
        addWidget(roomFloorPicker);

        changeTab("Decorate");
        addWidget(groupPicker);
        lightChanceSlider = new WidgetHSlider(this, 0, 4, "room light chance", 6, 0, 0, 100, true);
        addWidget(lightChanceSlider);
        lightNewAmbient = new WidgetButton(this, 0, 6, "[ New Ambient ]", null);
        addWidget(lightNewAmbient);
        lightNewPoint = new WidgetButton(this, 10, 6, "[ New Point ]", null);
        addWidget(lightNewPoint);
        lightList = new WidgetListVert(this, 0, 7, new String[]{});
        addWidget(lightList);

        addWidget(new WidgetText(this, 0, 12, "vault set:"));
        vaultSetPicker = new WidgetDropdown(this, 6, 12, commander.getResourceList("vaults"), 0);
        addWidget(vaultSetPicker);


        changeTab(null);

        tabSlider = new WidgetSlideTabs(this, 0, 36, 20, tabList(), 0);
        addWidget(tabSlider);

        sizeToWidgets();

        escapable = false;
        clipsToBounds = false;
        setChildPosition(commander.camera().columns - cellw - 2, commander.camera().rows - cellh - 2, commander.camera());

        loadScaper("testscaper");
        //scaper = new Metascaper();  makeNewLayer(); makeNewGroup();
        roomLights = new ArrayList<>();

        changeTab("Layers");
        changeTab("File");
        config.setLightEnable(false);
    }

    void loadScaper(String filename) {
        String path = commander.savePath();
        File file = new File(path + filename);
        scaper = null;
        try (
                FileInputStream stream = new FileInputStream(file);
        ) {
            scaper = objectMapper.readValue(stream, Metascaper.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        layers = scaper.layers;
        layerIndex = 0;
        layer = layers.get(0);
        groups = scaper.groups;
        groupIndex = 0;
        group = groups.get(0);
        roomLights = scaper.getRoomLights();
        areaWidthSlider.value = scaper.xsize;
        areaHeightSlider.value = scaper.ysize;
        lightChanceSlider.value = (int)(scaper.getLightChance()*100f);
        nameWidget.text = scaper.name;
        setTitle(scaper.name);
        updateLayerPicker();
        updateGroupPicker();
        regenerate();
    }

    void saveScaper(Metascaper scaper, String filename) {
        String path = commander.savePath();
        File file = new File(path + filename);
        try (
                FileOutputStream stream = new FileOutputStream(file);
        ) {
            JsonFactory jfactory = new JsonFactory();
            JsonGenerator jGenerator = jfactory
                    .createGenerator(stream, JsonEncoding.UTF8);
            jGenerator.setCodec(objectMapper);
            jGenerator.writeObject(scaper);
            jGenerator.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        ArrayList<String> nb = new ArrayList<>();
        for (String pb : layer.shaper.paramsB.keySet())
            nb.add(pb);
        Collections.sort(nb);
        for (String pb : nb) {
            Widget w = new WidgetRadio(this, 0, y, pb, null, null, (boolean)(layer.shaper.paramsB.get(pb)));
            shaperWidgets.put(pb, w);
            addWidget(w);
            y += 1;
        }
        ArrayList<String> nt = new ArrayList<>();
        for (String pt : layer.shaper.paramsT.keySet())
            nt.add(pt);
        Collections.sort(nt);
        for (String pt : nt) {
            Widget w = new WidgetTerrainpick(this, 0, y, pt, layer.shaper.paramsT.get(pt));
            shaperWidgets.put(pt, w);
            addWidget(w);
            y += 1;
        }
    }

    void makeNewGroup() {
        Roomgroup g = new Roomgroup();
        if (groups.size() == 0)
            g.editorColor = new UColor(config.getHiliteColor());
        else if (groups.size() == 1)
            g.editorColor = new UColor(UColor.GREEN);
        else if (groups.size() == 2)
            g.editorColor = new UColor(UColor.RED);
        else if (groups.size() == 3)
            g.editorColor = new UColor(UColor.CYAN);
        else if (groups.size() == 4)
            g.editorColor = new UColor(UColor.BLUE);
        else if (groups.size() == 5)
            g.editorColor = new UColor(UColor.LIGHTRED);
        else
            g.editorColor = new UColor(UColor.YELLOW);
        groups.add(g);
        updateGroupPicker();
        selectGroup(groups.indexOf(g));
    }

    void makeNewLayer() {
        Layer layer = new Layer();
        layers.add(layer);
        layer.shaper = makeShaper("Fill");
        layer.shaper.initialize(areaWidthSlider.value, areaHeightSlider.value);
        layer.terrain = "null";
        layer.density = 1f;
        updateLayerPicker();
        selectLayer(layers.indexOf(layer));
    }

    Shaper makeShaper(String name) {
        Shaper s = null;
        if (name.equals("Caves"))
            s = new Caves();
        else if (name.equals("Mines"))
            s =  new Mines();
        else if (name.equals("Growdungeon"))
            s =  new Growdungeon();
        else if (name.equals("Chambers"))
            s =  new Chambers();
        else if (name.equals("Convochain"))
            s =  new Convochain();
        else if (name.equals("Ruins"))
            s =  new Ruins();
        else if (name.equals("Blobs"))
            s =  new Blobs();
        else if (name.equals("Roads"))
            s =  new Roads();
        else if (name.equals("Outline"))
            s =  new Outline();
        else if (name.equals("Connector"))
            s =  new Connector();
        else if (name.equals("Doors"))
            s = new Doors();
        else if (name.equals("Fill"))
            s = new Fill();
        else if (name.equals("Stairs"))
            s = new Stairs();
        else
            return null;
        s.initialize(areaWidthSlider.value, areaHeightSlider.value);
        return s;
    }

    void deleteLayer() {
        if (layers.size() < 2) return;
        layers.remove(layerIndex);
        if (layerIndex >= layers.size()) layerIndex = layers.size() - 1;
        updateLayerPicker();
        selectLayer(layerIndex);
        autoRegenerate();
    }

    void deleteGroup() {
        if (groups.size() < 2) return;
        groups.remove(groupIndex);
        if (groupIndex >= groups.size()) groupIndex = groups.size() - 1;
        updateGroupPicker();
        selectGroup(groupIndex);
        autoRegenerate();
    }

    void moveLayer(int by) {
        int destIndex = layerIndex + by;
        Layer temp = layers.get(destIndex);
        layers.set(destIndex, layers.get(layerIndex));
        layers.set(layerIndex, temp);
        layerIndex = destIndex;
        updateLayerPicker();
        selectLayer(layerIndex);
        autoRegenerate();
    }

    void updateLayerPicker() {
        String[] choices = new String[layers.size()+1];
        int i = 0;
        for (Layer layer : layers) {
            choices[i] = "Layer " + i + ": " + layer.shaper.type + " " + layer.terrain;
            i++;
        }
        choices[layers.size()] = "<new layer>";
        layerPicker.setChoices(choices);
    }

    void updateGroupPicker() {
        String[] choices = new String[groups.size()+1];
        int i = 0;
        for (Roomgroup g : groups) {
            int size = 0;
            if (g != null)
                if (g.rooms != null)
                    size = g.rooms.size();
            choices[i] = "Group " + i + " (" + size + " rooms)";
            i++;
        }
        choices[groups.size()] = "<new group>";
        groupPicker.setChoices(choices);
    }

    @Override
    public void pressWidget(Widget widget) {
        if (widget == regenButton)
            regenerate();
        else if (widget == quitButton)
            quit();
        else if (widget == autoRegenRadio)
            autoRegenRadio.on = !autoRegenRadio.on;
        else if (widget == pruneRadio) {
            pruneRadio.on = !pruneRadio.on;
            layer.pruneDeadEnds = pruneRadio.on;
            autoRegenerate();
        } else if (widget == roundRadio) {
            roundRadio.on = !roundRadio.on;
            layer.roundCorners = roundRadio.on;
            autoRegenerate();
        } else if (widget == invertRadio) {
            invertRadio.on = !invertRadio.on;
            layer.invert = invertRadio.on;
            autoRegenerate();
        }else if (widget == lightNewAmbient)
            makeNewLight(ULight.AMBIENT);
        else if (widget == lightNewPoint)
            makeNewLight(ULight.POINT);
        else if (widget == groupDeleteButton)
            deleteGroup();
        else if (widget == layerDeleteButton) {
            deleteLayer();
        } else if (widget == layerUpButton) {
            moveLayer(1);
        } else if (widget == layerDownButton) {
            moveLayer(-1);
        } else if (shaperWidgets.containsValue(widget) && widget instanceof WidgetRadio) {
            ((WidgetRadio)widget).on = !((WidgetRadio)widget).on;
            updateShaperFromWidgets();
            autoRegenerate();
        } else if (tabWidgetSets.get("Rooms").contains(widget) && widget instanceof WidgetRadio) {
            ((WidgetRadio)widget).on = !((WidgetRadio)widget).on;
            updateGroupFromWidgets();
        }
    }

    @Override
    public void widgetChanged(Widget widget) {
        if (widget == shaperPicker) {
            selectShaper(shaperPicker.selected());
            autoRegenerate();
        } else if (widget == tabSlider) {
            changeTab(tabSlider.tabs.get(tabSlider.selection));
        } else if (widget == layerPicker) {
            if (layerPicker.selected().equals("<new layer>"))
                makeNewLayer();
            else
                selectLayer(layerPicker.selection);
        } else if (widget == groupPicker) {
            if (groupPicker.selected().equals("<new group>"))
                makeNewGroup();
            else
                selectGroup(groupPicker.selection);
        } else if (widget == terrainPicker) {
            layer.terrain = terrainPicker.selection;
            autoRegenerate();
        } else if (widget == drawPicker) {
            layer.printMode = drawPicker.selection;
            autoRegenerate();
        } else if (widget == nameWidget) {
            setTitle(nameWidget.text);
        } else if (widget == densitySlider) {
            layer.density = (float)densitySlider.value / 100f;
            autoRegenerate();
        } else if (shaperWidgets.containsValue(widget)) {
            updateShaperFromWidgets();
            autoRegenerate();
        } else if (tabWidgetSets.get("Rooms").contains(widget) && widget != groupPicker) {
            updateGroupFromWidgets();
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
        setChildPosition(commander.camera().columns - cellw - 2, commander.camera().rows - cellh - 2, commander.camera());
    }
    @Override
    public void draw() {
        if (widgets.contains(groupPicker)) {
            for (Roomgroup group : groups) {
                if (groupPicker.selection == 0 || groupPicker.selection == groups.indexOf(group)) {
                    if (group.rooms != null) {
                        for (Shape.Room r : group.rooms) {
                            int rx = ((r.x - commander.camera().leftEdge) * gw()) - absoluteX();
                            int ry = ((r.y - commander.camera().topEdge) * gh()) - absoluteY();
                            float f = (float) (commander.frameCounter / 22f);
                            f = f + (float) ((r.x + r.y) / 26f + (r.x - r.y) / 25f) + (r.y / 11f);
                            f = 0.14f + (float) Math.sin(f * 2.7f) * 0.03f;
                            group.editorColor.setAlpha(f);
                            renderer.drawRectBorder(rx, ry, r.width * gw(), r.height * gh(), 2, group.editorColor, config.getHiliteColor());
                        }
                    }
                }
            }
        }
        super.draw();
    }

    void selectShaper(String selection) {
        removeShaperWidgets();
        layer.shaper = makeShaper(selection);
        makeShaperWidgets();
        autoRegenerate();
    }

    void selectLayer(int selection) {
        layerPicker.selection = selection;
        removeShaperWidgets();
        layerIndex = selection;
        layer = layers.get(layerIndex);
        shaperPicker.selectChoice(layer.shaper.type);
        makeShaperWidgets();
        pruneRadio.on = layer.pruneDeadEnds;
        roundRadio.on = layer.roundCorners;
        invertRadio.on = layer.invert;
        terrainPicker.selection = layer.terrain;
        drawPicker.selection = layer.printMode;
        densitySlider.value = (int)(layer.density * 100f);

        removeWidget(layerUpButton);
        removeWidget(layerDownButton);
        if (layerIndex > 0)
            addWidget(layerDownButton);
        if (layerIndex < layers.size() - 1)
            addWidget(layerUpButton);
    }

    void selectGroup(int selection) {
        groupPicker.selection = selection;
        groupIndex = selection;
        group = groups.get(groupIndex);
        roomHallsRadio.on = group.includeHallways;
        roomCountSlider.value = group.maxCount;
        roomMinSizeSlider.value = group.minRoomSize;
        roomMaxSizeSlider.value = group.maxRoomSize;
        roomFrequencySlider.value = (int)(group.frequency * 100f);
        roomSeparationSlider.value = group.separation;
        roomFloorPicker.selection = group.floorType;
        removeWidget(groupDeleteButton);
        if (selection > 0)
            addWidget(groupDeleteButton);
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
        for (String pb : layer.shaper.paramsB.keySet()) {
            boolean val = ((WidgetRadio)(shaperWidgets.get(pb))).on;
            layer.shaper.paramsB.put(pb, val);
        }
        for (String pt : layer.shaper.paramsT.keySet()) {
            String val = ((WidgetTerrainpick)(shaperWidgets.get(pt))).selection;
            layer.shaper.paramsT.put(pt, val);
        }
    }

    void updateGroupFromWidgets() {
        group.includeHallways = roomHallsRadio.on;
        group.maxCount = roomCountSlider.value;
        group.minRoomSize = roomMinSizeSlider.value;
        group.maxRoomSize = roomMaxSizeSlider.value;
        group.frequency = (float)(roomFrequencySlider.value) / 100f;
        group.separation = roomSeparationSlider.value;
        group.floorType = roomFloorPicker.selection;
    }

    void autoRegenerate() {
        if (autoRegenRadio.on)
            regenerate();
    }

    void regenerate() {
        //if (true) return;
        updateLayerPicker();
        UModalLoading lmodal = new UModalLoading();
        lmodal.setChildPosition(2,2,commander.camera());
        commander.showModal(lmodal);
        commander.renderer.render();

        if (area.xsize != areaWidthSlider.value || area.ysize != areaHeightSlider.value) {
            area.initialize(areaWidthSlider.value, areaHeightSlider.value, "null");
            for (Layer l : layers) {
                l.shaper.resize(areaWidthSlider.value - 2, areaHeightSlider.value - 2);
            }
        }
        scaper.setup(nameWidget.text, areaWidthSlider.value, areaHeightSlider.value, layers, groups,(float)(lightChanceSlider.value)/100f, roomLights, vaultSetPicker.selected());
        scaper.buildArea(area, 1, new String[]{});

        commander.camera().renderLights();
        commander.detachModal(lmodal);
    }

    void quit() {
        saveScaper(scaper, "testscaper");
        config.setLightEnable(true);
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
