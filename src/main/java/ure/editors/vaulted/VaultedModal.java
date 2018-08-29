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
import ure.sys.GLKey;
import ure.sys.LogFormatter;
import ure.terrain.UTerrain;
import ure.ui.modals.*;
import ure.ui.modals.widgets.*;

import static org.lwjgl.glfw.GLFW.*;
import javax.inject.Inject;
import java.util.Set;

public class VaultedModal extends UModal implements HearModalChoices {

    @Inject
    ObjectMapper objectMapper;

    public Log log = LogFactory.getLog(VaultedModal.class);

    VaultedArea area;
    int currentTerrain = 0;
    int nullTerrain;
    int[] terrainPalette;
    String[] terrains;
    String filename;
    UVault vault;
    UVaultSet vaultSet;
    int vaultCursor;

    int tool = 0;
    static int TOOL_DRAW = 0;
    static int TOOL_LINE = 1;
    static int TOOL_BOX = 2;
    static int TOOL_CROP = 3;

    WidgetText headerWidget;
    WidgetListVert vaultListWidget;
    WidgetText terrainNameWidget;
    WidgetStringInput nameWidget;
    WidgetVaulted vaultedWidget;
    WidgetEntityPalette terrainWidget;

    WidgetButton drawButton;
    WidgetButton lineButton;
    WidgetButton boxButton;
    WidgetButton cropButton;

    WidgetButton growButton;
    WidgetButton undoButton;
    WidgetButton saveButton;
    WidgetButton revertButton;
    WidgetButton deleteButton;
    WidgetButton quitButton;

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
        }
        vaultCursor = 0;

        headerWidget = new WidgetText(this,0,0,filename);
        headerWidget.setDimensions(0,0,30,2);
        addWidget(headerWidget);

        vaultListWidget = new WidgetListVert(this, 0, 3, new String[]{""});
        vaultListWidget.setDimensions(0,3,8, 20);
        addWidget(vaultListWidget);

        terrainNameWidget = new WidgetText(this, 10, 4, "");
        terrainNameWidget.color = config.getTextGray();
        addWidget(terrainNameWidget);

        nameWidget = new WidgetStringInput(this,10,0, 20, "", 30);
        addWidget(nameWidget);

        terrainWidget = new WidgetEntityPalette(this, 10,2, 25, 2);
        for (UTerrain t : terrainCzar.getAllTerrainTemplates())
            terrainWidget.add(t);
        addWidget(terrainWidget);

        vaultedWidget = new WidgetVaulted(this, 10, 6, 25,25);
        addWidget(vaultedWidget);

        drawButton = new WidgetButton(this, 41, 6, "[ Tool: draw ]", null);
        drawButton.lit = true;
        lineButton = new WidgetButton(this, 41, 7, "[ Tool: line ]", null);
        boxButton = new WidgetButton(this, 41, 8, "[ Tool: box ]", null);
        cropButton = new WidgetButton(this, 41, 9, "[ Tool: crop ]", null);
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
        addWidget(growButton);
        addWidget(undoButton);
        addWidget(saveButton);
        addWidget(revertButton);
        addWidget(deleteButton);
        addWidget(quitButton);

        sizeToWidgets();
        escapable = false;
        setTitle("vaultEd");

        updateVaultList();
        loadVault();
        updateLayout();
    }

    void updateLayout() {
        int bx = vaultedWidget.col + vaultedWidget.cellw + 1;
        drawButton.move(bx, 6);
        lineButton.move(bx, 7);
        boxButton.move(bx, 8);
        cropButton.move(bx, 9);
        growButton.move(bx, 11);
        undoButton.move(bx, 12);
        saveButton.move(bx, 13);
        revertButton.move(bx, 14);
        deleteButton.move(bx, 15);
        quitButton.move(bx, 16);
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
                int tooltype = 0;
                if (tool == TOOL_BOX || tool == TOOL_CROP)
                    tooltype = WidgetVaulted.TOOLTYPE_BOX;
                else
                    tooltype = WidgetVaulted.TOOLTYPE_LINE;
                vaultedWidget.setToolStart(tooltype);
            }
        } else if (widget == vaultListWidget) {
            if (vaultListWidget.selection == vaultSet.size()) {
                makeNewVault();
            } else if (vaultListWidget.selection < vaultSet.size()) {
                saveVault();
                loadVault();
            }
        } else if (widget == drawButton) {
            selectTool(TOOL_DRAW);
        } else if (widget == lineButton) {
            selectTool(TOOL_LINE);
        } else if (widget == boxButton) {
            selectTool(TOOL_BOX);
        } else if (widget == cropButton) {
            selectTool(TOOL_CROP);
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
            confirmModal("Delete vault '" + vault.name + "' from vaultset?", "delete");
        } else if (widget == quitButton) {
            confirmModal("Quit vaultEd?  Unsaved changes will be lost.", "quit");
        }
    }

    void selectTool(int newtool) {
        drawButton.lit = false;
        lineButton.lit = false;
        boxButton.lit = false;
        cropButton.lit = false;
        tool = newtool;
        if (tool == TOOL_DRAW)
            drawButton.lit = true;
        else if (tool == TOOL_LINE)
            lineButton.lit = true;
        else if (tool == TOOL_BOX)
            boxButton.lit = true;
        else if (tool == TOOL_CROP)
            cropButton.lit = true;
    }

    @Override
    public void widgetChanged(Widget widget) {
        if (widget == terrainWidget) {
            terrainNameWidget.setText(terrainWidget.entity().name());
        } else if (widget == nameWidget) {
            vault.setName(nameWidget.text);
            updateVaultList();
        } else if (widget == vaultedWidget) {
            if (tool == TOOL_CROP) {
                doCrop(vaultedWidget.toolX, vaultedWidget.toolY, vaultedWidget.toolFinishX, vaultedWidget.toolFinishY);
                selectTool(TOOL_DRAW);
                updateLayout();
            } else if (tool == TOOL_BOX) {
                doBox(vaultedWidget.toolX, vaultedWidget.toolY, vaultedWidget.toolFinishX, vaultedWidget.toolFinishY);
            } else if (tool == TOOL_LINE) {
                doLine(vaultedWidget.toolX, vaultedWidget.toolY, vaultedWidget.toolFinishX, vaultedWidget.toolFinishY);
            }
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
    }

    void saveVault() {
        vaultedWidget.saveVault(vault);
    }

    void loadVault() {
        vault = vaultSet.vaultAt(vaultListWidget.selection);
        vaultListWidget.dimAll();
        vaultListWidget.lightOption(vaultListWidget.selection);
        nameWidget.text = vault.name;
        vaultedWidget.loadVault(vault);
        updateLayout();
        updateVaultList();
        log.info("Loaded vault '" + vault.name + "' " + Integer.toString(vault.cols) + " by " + Integer.toString(vault.rows));
    }

    void makeNewVault() {
        saveVault();
        vaultSet.addVault();
        vaultListWidget.selection = vaultSet.size() - 1;
        loadVault();
    }

    void writeFile() {
        vaultSet.persist(commander.config.getResourcePath() + "vaults/" + filename + ".json");
        commander.printScroll("Saved vaultset " + filename + ".json to resources.");
        log.info("Saved vault file '" + filename + "'.");
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
        }
    }

    void deleteVault() {

    }

    void doCrop(int x1, int y1, int x2, int y2) {
        if (x2-x1<2 && y2-y1<2) return;
        vaultedWidget.doCrop(x1,y1,x2,y2);
        vault.initialize(x2-x1,y2-y1);
        saveVault();
    }

    void doBox(int x1, int y1, int x2, int y2) {

    }

    void doLine(int x1, int y1, int x2, int y2) {

    }
}
