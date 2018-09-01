package ure.editors.vaulted;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ure.actors.UPlayer;
import ure.areas.UVault;
import ure.areas.UVaultSet;
import ure.commands.CommandQuit;
import ure.commands.UCommand;
import ure.math.UColor;
import ure.math.UPath;
import ure.sys.GLKey;
import ure.sys.LogFormatter;
import ure.terrain.UTerrain;
import ure.ui.Icons.Icon;
import ure.ui.ULight;
import ure.ui.modals.*;
import ure.ui.modals.widgets.*;

import static org.lwjgl.glfw.GLFW.*;
import javax.inject.Inject;
import java.util.Set;

public class VaultedModal extends UModal implements HearModalChoices {

    @Inject
    ObjectMapper objectMapper;

    public Log log = LogFactory.getLog(VaultedModal.class);

    String filename;
    UVault vault;
    UVaultSet vaultSet;
    int vaultSelection;

    int tool = 0;
    static int TOOL_DRAW = 0;
    static int TOOL_LINE = 1;
    static int TOOL_BOX = 2;
    static int TOOL_CROP = 3;
    static int TOOL_ACTOR = 4;
    static int TOOL_THING = 5;
    static int TOOL_LIGHT = 6;

    WidgetText headerWidget;
    WidgetListVert vaultListWidget;
    WidgetText terrainNameWidget;
    WidgetStringInput nameWidget;
    WidgetStringInput descWidget;
    WidgetVaulted vaultedWidget;
    WidgetRadio lightRadio;
    WidgetEntityPalette terrainWidget;

    WidgetButton drawButton;
    WidgetButton lineButton;
    WidgetButton boxButton;
    WidgetButton cropButton;
    WidgetButton actorButton;
    WidgetButton thingButton;
    WidgetButton lightButton;

    WidgetButton growButton;
    WidgetButton undoButton;
    WidgetButton saveButton;
    WidgetButton revertButton;
    WidgetButton deleteButton;
    WidgetButton quitButton;

    WidgetRadio areaUniqueRadio;
    WidgetRadio gameUniqueRadio;
    WidgetRadio mirrorRadio;
    WidgetRadio rotateRadio;

    WidgetListVert actorListWidget;
    WidgetListVert thingListWidget;
    WidgetListVert lightListWidget;

    boolean savedPaintUndo = false;

    public VaultedModal(String _filename) {
        super(null, "");
        filename = _filename;
        setPad(1,1);

        log.info("Attempting to load vaultset '" + filename + "'");
        vaultSet = commander.cartographer.loadVaultSet(filename);
        if (vaultSet == null) {
            log.info("Not found, initializing new blank vaultset");
            vaultSet = new UVaultSet();
            vaultSet.initialize();
            vaultSet.setFilename(filename);
            writeFile();
        }

        headerWidget = new WidgetText(this,0,0,filename);
        headerWidget.setDimensions(0,0,10,2);
        addWidget(headerWidget);

        vaultListWidget = new WidgetListVert(this, 0, 3, new String[]{""});
        vaultListWidget.setDimensions(0,3,8, 20);
        addWidget(vaultListWidget);

        terrainNameWidget = new WidgetText(this, 10, 5, "");
        terrainNameWidget.color = config.getTextGray();
        addWidget(terrainNameWidget);

        nameWidget = new WidgetStringInput(this,10,0, 20, "", 30);
        addWidget(nameWidget);
        descWidget = new WidgetStringInput(this, 10, 1, 20, "", 80);
        addWidget(descWidget);
        terrainWidget = new WidgetEntityPalette(this, 10,3, 25, 2);
        for (UTerrain t : terrainCzar.getAllTerrainTemplates())
            terrainWidget.add(t);
        addWidget(terrainWidget);

        vaultedWidget = new WidgetVaulted(this, 10, 7, 25,25);
        addChild(vaultedWidget.camera);
        addWidget(vaultedWidget);

        drawButton = new WidgetButton(this, 41, 7, " . draw ", null);
        drawButton.lit = true;
        lineButton = new WidgetButton(this, 41, 7, " / line ", null);
        boxButton = new WidgetButton(this, 41, 8, "[] box ", null);
        cropButton = new WidgetButton(this, 41, 9, "<> crop ", null);
        actorButton = new WidgetButton(this, 41, 9, " @ actor ", null);
        thingButton = new WidgetButton(this, 41, 9, " + thing ", null);
        lightButton = new WidgetButton(this, 41, 9, " ! light ", null);
        growButton = new WidgetButton(this, 41, 10, "[ Grow ]", null);
        undoButton = new WidgetButton(this, 41, 11, "[ Undo ]", null);
        saveButton = new WidgetButton(this, 41, 12, "[ Save ]", null);
        revertButton = new WidgetButton(this, 41, 13, "[ Revert ]", null);
        deleteButton = new WidgetButton(this, 41, 14, "[ Delete ]", null);
        quitButton = new WidgetButton(this, 41, 15, "[ Quit ]", null);
        addWidget(drawButton);
        addWidget(lineButton);
        addWidget(boxButton);
        addWidget(cropButton);
        addWidget(actorButton);
        addWidget(thingButton);
        addWidget(lightButton);
        addWidget(growButton);
        addWidget(undoButton);
        addWidget(saveButton);
        addWidget(revertButton);
        addWidget(deleteButton);
        addWidget(quitButton);

        Icon radioOn = new Icon(9787, UColor.WHITE, null);
        Icon radioOff = new Icon(9675, UColor.GRAY, null);
        lightRadio = new WidgetRadio(this, 0, 0, "lit", radioOff, radioOn, false);
        areaUniqueRadio = new WidgetRadio(this, 0, 0, "areaUnique", radioOff, radioOn, false);
        gameUniqueRadio = new WidgetRadio(this, 0, 0, "gameUnique", radioOff, radioOn, false);
        mirrorRadio = new WidgetRadio(this, 0, 0, "can mirror", radioOff, radioOn, false);
        rotateRadio = new WidgetRadio(this, 0, 0, "can rotate", radioOff, radioOn, false);
        addWidget(lightRadio);
        addWidget(areaUniqueRadio);
        addWidget(gameUniqueRadio);
        addWidget(mirrorRadio);
        addWidget(rotateRadio);

        actorListWidget = new WidgetListVert(this, 0, 0, new String[]{});
        addWidget(actorListWidget);
        thingListWidget = new WidgetListVert(this, 0, 0, new String[]{});
        addWidget(thingListWidget);
        lightListWidget = new WidgetListVert(this, 0, 0, new String[]{});
        addWidget(lightListWidget);

        escapable = false;
        setTitle("vaultEd");
        vaultedWidget.brushIcon = terrainWidget.entity().icon();
        terrainNameWidget.setText(terrainWidget.entity().name());
        updateVaultList();
        loadVault();
        updateLayout();
    }

    void updateLayout() {
        int bx = vaultedWidget.col + Math.max(vaultedWidget.cellw, 10) + 1;
        int by = 7;
        lightRadio.move(bx-lightRadio.cellw, vaultedWidget.row-2);
        drawButton.move(bx, by);
        lineButton.move(bx, by+1);
        boxButton.move(bx, by+2);
        cropButton.move(bx, by+3);
        actorButton.move(bx, by+4);
        thingButton.move(bx, by+5);
        lightButton.move(bx, by+6);
        growButton.move(bx, by+8);
        undoButton.move(bx, by+9);
        saveButton.move(bx, by+10);
        revertButton.move(bx, by+11);
        deleteButton.move(bx, by+12);
        quitButton.move(bx, by+14);

        int rx = bx + 6;
        int ry = by;
        areaUniqueRadio.move(rx,ry);
        gameUniqueRadio.move(rx,ry+1);
        mirrorRadio.move(rx,ry+2);
        rotateRadio.move(rx,ry+3);

        int lx = rx;
        int ly = rotateRadio.row + rotateRadio.cellh + 1;
        actorListWidget.move(lx,ly);
        thingListWidget.move(lx,actorListWidget.row+actorListWidget.cellh+1);
        lightListWidget.move(lx,thingListWidget.row+thingListWidget.cellh+1);

        sizeToWidgets();
    }

    @Override
    public void pressWidget(Widget widget) {
        if (widget == vaultedWidget) {
            if (tool == TOOL_DRAW) {
                if (!savedPaintUndo) {
                    vaultedWidget.saveUndo();
                    savedPaintUndo = true;
                }
                vaultedWidget.paint((UTerrain) (terrainWidget.entity()));
            } else {
                vaultedWidget.saveUndo();
                vaultedWidget.setToolStart(tool);
            }
        } else if (widget == vaultListWidget) {
            if (vaultListWidget.selection == vaultSet.size()) {
                makeNewVault();
            } else if (vaultListWidget.selection < vaultSet.size()) {
                vaultSelection = vaultListWidget.selection;
                loadVault();
            }
        } else if (widget == terrainWidget) {
            vaultedWidget.brushIcon = terrainWidget.entity().icon();
        } else if (widget == drawButton) {
            selectTool(TOOL_DRAW);
        } else if (widget == lineButton) {
            selectTool(TOOL_LINE);
        } else if (widget == boxButton) {
            selectTool(TOOL_BOX);
        } else if (widget == cropButton) {
            selectTool(TOOL_CROP);
        } else if (widget == thingButton) {
            selectTool(TOOL_THING);
        } else if (widget == actorButton) {
            selectTool(TOOL_ACTOR);
        } else if (widget == lightButton) {
            selectTool(TOOL_LIGHT);
        } else if (widget == growButton) {
            vaultedWidget.saveUndo();
            vaultedWidget.grow();
            updateLayout();
        } else if (widget == undoButton) {
            vaultedWidget.undo();
            updateLayout();
        } else if (widget == saveButton) {
            confirmModal("Save all changes to '" + filename + "' vault file?", "save");
        } else if (widget == revertButton) {
            confirmModal("Revert all changes to '" + filename + "' vault and reload?", "revert");
        } else if (widget == deleteButton) {
            confirmModal("Delete vault '" + vault.name + "' from set " + filename + "?", "delete");
        } else if (widget == quitButton) {
            confirmModal("Quit vaultEd?  Unsaved changes will be lost.", "quit");
        } else if (widget == lightListWidget) {
            editLight(vault.lights.get(lightListWidget.selection));
        } else {
            super.pressWidget(widget);
        }
    }

    void editLight(ULight light) {

    }

    void selectTool(int newtool) {
        drawButton.lit = false;
        lineButton.lit = false;
        boxButton.lit = false;
        cropButton.lit = false;
        thingButton.lit = false;
        actorButton.lit = false;
        lightButton.lit = false;
        tool = newtool;
        if (tool == TOOL_DRAW)
            drawButton.lit = true;
        else if (tool == TOOL_LINE)
            lineButton.lit = true;
        else if (tool == TOOL_BOX)
            boxButton.lit = true;
        else if (tool == TOOL_CROP)
            cropButton.lit = true;
        else if (tool == TOOL_THING)
            thingButton.lit = true;
        else if (tool == TOOL_ACTOR)
            actorButton.lit = true;
        else if (tool == TOOL_LIGHT)
            lightButton.lit = true;
    }

    @Override
    public void widgetChanged(Widget widget) {
        if (widget == terrainWidget) {
            terrainNameWidget.setText(terrainWidget.entity().name());
        } else if (widget == nameWidget) {
            vault.setName(nameWidget.text);
            updateVaultList();
        } else if (widget == descWidget) {
            vault.setDescription(descWidget.text);
        } else if (widget == vaultedWidget) {
            if (tool == TOOL_CROP) {
                doCrop(vaultedWidget.toolX, vaultedWidget.toolY, vaultedWidget.toolFinishX, vaultedWidget.toolFinishY);
                selectTool(TOOL_DRAW);
                updateLayout();
            } else if (tool == TOOL_BOX) {
                doBox(vaultedWidget.toolX, vaultedWidget.toolY, vaultedWidget.toolFinishX, vaultedWidget.toolFinishY);
            } else if (tool == TOOL_LINE) {
                doLine(vaultedWidget.toolX, vaultedWidget.toolY, vaultedWidget.toolFinishX, vaultedWidget.toolFinishY);
            } else if (tool == TOOL_LIGHT) {
                makeLight(vaultedWidget.toolX, vaultedWidget.toolY, vaultedWidget.toolFinishX, vaultedWidget.toolFinishY);
            }
        } else if (widget == areaUniqueRadio) {
            vault.areaUnique = areaUniqueRadio.on;
        } else if (widget == gameUniqueRadio) {
            vault.gameUnique = gameUniqueRadio.on;
        } else if (widget == mirrorRadio) {
            vault.mirror = mirrorRadio.on;
        } else if (widget == rotateRadio) {
            vault.rotate = rotateRadio.on;
        } else if (widget == lightRadio) {
            vaultedWidget.camera.setLightEnable(lightRadio.on);
        }
    }

    @Override
    public void mouseInside(Widget widget, int mousex, int mousey) {
        super.mouseInside(widget,mousex,mousey);
        if (widget == vaultedWidget) {
            if (tool == TOOL_DRAW && commander.mouseButton()) {
                vaultedWidget.paint((UTerrain)(terrainWidget.entity()));
            }
        }
        if (savedPaintUndo && !commander.mouseButton()) {
            savedPaintUndo = false;
        }
    }

    void updateVaultList() {
        String[] names = vaultSet.vaultNames();
        String[] options = new String[names.length+1];
        for (int i=0;i<names.length;i++) {
            options[i] = Integer.toString(i) + ". " + names[i];
        }
        options[names.length] = "<new vault>";
        vaultListWidget.setOptions(options);
        vaultListWidget.lightOption(vaultListWidget.selection);
    }

    void loadVault() {
        vault = vaultSet.vaultAt(vaultSelection);
        vaultListWidget.dimAll();
        nameWidget.text = vault.name;
        descWidget.text = vault.description;
        areaUniqueRadio.on = vault.areaUnique;
        gameUniqueRadio.on = vault.gameUnique;
        mirrorRadio.on = vault.mirror;
        rotateRadio.on = vault.rotate;
        vaultedWidget.loadVault(vault);
        vaultListWidget.lightOption(vaultSelection);
        updateLayout();
        //updateVaultList();
        updateLightList();
        log.info("Loaded vault '" + vault.name + "' " + Integer.toString(vault.cols) + " by " + Integer.toString(vault.rows));
    }

    void makeNewVault() {
        vaultSet.addVault();
        vaultListWidget.select(vaultSet.size() - 1);
        loadVault();
    }

    void writeFile() {
        vaultSet.persist(commander.config.getResourcePath() + "vaults/" + filename + ".json");
        commander.printScroll("Saved vaultset " + filename + ".json to resources.");
        log.info("Saved vault file '" + filename + "'.");
    }

    void revertFile() {
        vaultSet = commander.cartographer.loadVaultSet(filename);
        vaultListWidget.select(0);
        loadVault();
    }

    void confirmModal(String query, String context) {
        UModalChoices modal = new UModalChoices(query, new String[]{"OK", "Cancel"}, this, context);
        commander.showModal(modal);
    }

    public void hearModalChoices(String context, String selection) {
        if (context.equals("quit") && selection.equals("OK")) {
            dismiss();
        } else if (context.equals("save") && selection.equals("OK")) {
            writeFile();
        } else if (context.equals("delete") && selection.equals("OK")) {
            deleteVault();
        } else if (context.equals("revert") && selection.equals("OK")) {
            revertFile();
        }
    }

    void deleteVault() {
        vaultSet.removeVault(vault);
        vaultListWidget.select(Math.min(vaultSet.vaults.length-1, Math.max(0,vaultListWidget.selection-1)));
        loadVault();
    }

    void updateLightList() {
        if (vault.lights == null || vault.lights.size() < 1) {
            lightListWidget.setOptions(new String[]{});
        } else {
            String[] options = new String[vault.lights.size()];
            for (int i=0;i<vault.lights.size();i++) {
                ULight l = vault.lights.get(i);
                if (l.type == ULight.AMBIENT) {
                    options[i] = Integer.toString(i) + ". ambient light";
                } else {
                    options[i] = Integer.toString(i) + ". point light";
                }
            }
            lightListWidget.setOptions(options);
        }
    }

    void doCrop(int x1, int y1, int x2, int y2) {
        System.out.println(Integer.toString(vaultListWidget.selection));
        if (x2-x1<2 && y2-y1<2) return;
        vaultedWidget.doCrop(x1,y1,x2+1,y2+1);
        for (ULight l : vault.lights)
            l.moveTo(vaultedWidget.area, l.x - x1, l.y - y1);
        loadVault();
        vaultedWidget.camera.renderLights();
    }

    void doBox(int x1, int y1, int x2, int y2) {
        for (int i=x1;i<=x2;i++) {
            for (int j=y1;j<=y2;j++) {
                vaultedWidget.paint((UTerrain)(terrainWidget.entity()), i, j);
            }
        }
    }

    void doLine(int x1, int y1, int x2, int y2) {
        for (int[] point : UPath.line(x1,y1,x2,y2)) {
            vaultedWidget.paint((UTerrain)(terrainWidget.entity()), point[0], point[1]);
        }
    }

    void makeLight(int x1, int y1, int x2, int y2) {
        ULight l = new ULight(UColor.WHITE, 100, 100);
        l.makeAmbient(x2-x1+1,y2-y1+1);
        vault.addLight(l, x1, y1);
        l.moveTo(vaultedWidget.area, x1, y1);
        vaultedWidget.camera.renderLights();
        lightRadio.on = true;
        vaultedWidget.camera.setLightEnable(true);
        selectTool(TOOL_DRAW);
        updateLightList();
    }
}
