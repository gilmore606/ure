package ure.editors.glyphed;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import ure.areas.UCell;
import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.GLKey;
import ure.terrain.UTerrain;
import ure.ui.Icons.Icon;
import ure.ui.Icons.UIconCzar;
import ure.ui.modals.HearModalChoices;
import ure.ui.modals.UModal;
import ure.ui.modals.UModalChoices;
import ure.ui.modals.UModalNotify;

import javax.inject.Inject;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_UP;

public class GlyphedModal extends UModal implements HearModalChoices {

    int gridspacex = 0;
    int gridspacey = 0;
    int gridposx = 1;
    int gridposy = 7;

    int meterx = 20;
    int metery = 8;

    String selection;
    Icon selectedIcon;
    int glyphType = 0;
    static int TERRAIN = 0;
    static int THING = 1;
    static int ACTOR = 2;
    int pageOffset = 0;
    boolean listUpEnabled, listDownEnabled;
    ArrayList<Icon> displayIcons;
    ArrayList<Integer> displayXs;
    ArrayList<Integer> displayYs;

    Set<String> thingNames;
    Set<String> actorNames;
    Set<String> terrainNames;
    ArrayList<Icon> thingIcons;
    ArrayList<Icon> actorIcons;
    ArrayList<Icon> terrainIcons;

    ArrayList<UTerrain> terrains;
    int refTerrain = 0;
    Icon refIcon;

    int cursorAscii = 0;

    UColor editColor;

    public GlyphedModal() {
        super(null, "", null);
        setDimensions(46,36);
        terrains = terrainCzar.getAllTerrainTemplates();
        makeRefIcon();
        thingNames = thingCzar.getAllThings();
        actorNames = actorCzar.getAllActors();
        terrainNames = terrainCzar.getAllTerrains();
        fillIconLists();
        setupDisplayIcons();
        selectIcon(0);
    }

    void makeRefIcon() {
        UTerrain t = terrains.get(refTerrain);
        UTerrain t2 = t.makeClone();
        t2.initializeAsCloneFrom(t);
        refIcon = t2.getIcon();
    }

    void fillIconLists() {
        thingIcons = new ArrayList<>();
        for (String name : thingNames) {
            Icon icon = iconCzar.getIconByName(name);
            if (icon == null) {
                icon = new Icon("blank");
                icon.setName(name);
            }
            thingIcons.add(icon);
        }
        actorIcons = new ArrayList<>();
        for (String name : actorNames) {
            Icon icon = iconCzar.getIconByName(name);
            if (icon == null) {
                icon = new Icon("blank");
                icon.setName(name);
            } else {
                System.out.println("GLYPHED: loaded existing icon " + name);
            }
            actorIcons.add(icon);
        }
        terrainIcons = new ArrayList<>();
        for (String name : terrainNames) {
            Icon icon = iconCzar.getIconByName(name);
            if (icon == null) {
                icon = new Icon("blank");
                icon.setName(name);
            }
            terrainIcons.add(icon);
        }
    }

    ArrayList<Icon> currentIconSet() {
        if (glyphType == TERRAIN)
            return terrainIcons;
        else if (glyphType == THING)
            return thingIcons;
        else
            return actorIcons;
    }

    void selectIcon(int i) {
        if (i+pageOffset >= currentIconSet().size()) return;
        Icon ic = currentIconSet().get(i+pageOffset);
        if (ic == null) return;
        selectedIcon = ic;
        selection = selectedIcon.getName();
        editColor = selectedIcon.fgColor;
        if (editColor == null) {
            selectedIcon.fgColor = new UColor(1f,1f,1f,1f);
            editColor = selectedIcon.fgColor;
        }
        setupDisplayIcons();
    }

    void selectRefIcon(int i) {
        if (i+pageOffset >= currentIconSet().size()) return;
        Icon ic = currentIconSet().get(i+pageOffset);
        if (ic == null) return;
        refIcon = ic;
    }

    @Override
    public void mouseClick() {
        updateMouseGrid();

        // Glyph grid
        if (mousex >= gridposx && mousex < (gridposx+16)) {
            if (mousey >= gridposy && mousey < (gridposy + 16)) {
                selectedIcon.setGlyph(cp437toUnicode(cursorAscii));
                setupDisplayIcons();
            }
        }

        // Color sliders
        if (mousex >= meterx && mousex <= meterx+13) {
            if (mousey >= metery && mousey <= (metery+8)) {
                int meteri = (mousex-meterx);

                int mousepy = commander.mouseY() - (ypos+metery*gh());
                float level = (float)mousepy / ((float)8*gh());
                level = 1f-level;
                System.out.println(Float.toString(level));
                if (meteri == 0)
                    editColor.setR(level);
                else if (meteri == 2)
                    editColor.setG(level);
                else if (meteri == 4)
                    editColor.setB(level);
            }
        }

        // Icon select
        if (mousey >= 1 && mousey <= 3) {
            if (mousex >= 23 && mousex <= 29) {
                glyphType = mousey - 1;
                pageOffset = 0;
                selectIcon(0);
            }
        }
        if (mousex >= 31 && mousex <= 40) {
            if (mousey >= 8 && mousey <= 19) {
                selectIcon(mousey-8);
            }
        }
        if (mousex >= 31 && mousex <= 36) {
            if (mousey == 20) {
                pageOffset += 12;
                selectIcon(0);
            } else if (mousey == 7) {
                pageOffset -= 12;
                selectIcon(0);
            }
        }

        // Coloredit selector
        if (mousex >= 20 && mousex <= 28) {
            if (mousey == 20 || mousey == 22) {
                selectSwatch(mousex-20, mousey == 20 ? 0 : 1);
            }
        }

        // Reload / Save / Quit
        if (mousey == 34) {
            if (mousex >= 33 && mousex <= 36)
                doReload();
            else if (mousex >= 37 && mousex <= 39)
                doSave();
            else if (mousex >= 40 && mousex <= 42)
                doQuit();
        }
    }

    @Override
    public void mouseRightClick() {
        // Coloredit selector
        if (mousex >= 20 && mousex <= 28) {
            if (mousey == 20 || mousey == 22) {
                deleteSwatch(mousex-20, mousey == 20 ? 0 : 1);
            }
        }

        // Reficon select
        if (mousex >= 31 && mousex <= 40) {
            if (mousey >= 8 && mousey <= 19) {
                selectRefIcon(mousey-8);
            }
        }
    }

    void selectSwatch(int i, int type) {
        int fgVarCount, bgVarCount;
        if (selectedIcon.getFgVariants() == null)
            fgVarCount = 0;
        else
            fgVarCount = selectedIcon.getFgVariants().length;
        if (selectedIcon.getBgVariants() == null)
            bgVarCount = 0;
        else
            bgVarCount = selectedIcon.getBgVariants().length;
        if (type == 0) {
            if (i == 0)
                editColor = selectedIcon.fgColor;
            else if (i == fgVarCount+1)
                addFgSwatch();
            else if (i <= fgVarCount)
                selectFgSwatch(i-1);
        } else {
            if (i == 0) {
                if (selectedIcon.bgColor == null)
                    selectedIcon.bgColor = new UColor(0f,0f,0f);
                editColor = selectedIcon.bgColor;
            } else if (i == bgVarCount+1)
                addBgSwatch();
            else
                selectBgSwatch(i-1);
        }
    }

    void deleteSwatch(int i, int type) {
        if (type == 0 && i == 0)
            return;
        if (type == 1 && i == 0) {
            if (selectedIcon.getBgVariants() == null) {
                if (editColor == selectedIcon.bgColor)
                    editColor = selectedIcon.fgColor;
                selectedIcon.bgColor = null;
            }
        }
        if (i > 0) {
            UColor[] set = (type == 0 ? selectedIcon.getFgVariants() : selectedIcon.getBgVariants());
            i -= 1;
            if (i >= set.length) return;
            if (editColor == set[i]) editColor = type == 0 ? selectedIcon.fgColor : selectedIcon.bgColor;
            UColor[] nuset = new UColor[set.length-1];
            for (int j=0;j<i;j++)
                nuset[j] = set[j];
            for (int j=i;j<nuset.length;j++)
                nuset[j] = set[j+1];
            if (type == 0)
                selectedIcon.setFgVariants(nuset);
            else
                selectedIcon.setBgVariants(nuset);
        }
    }

    void addFgSwatch() {
        if (selectedIcon.getFgVariants() == null) {
            UColor[] f = new UColor[1];
            f[0] = new UColor(selectedIcon.fgColor);
            selectedIcon.setFgVariants(f);
            editColor = f[0];
        } else {
            UColor[] f = new UColor[selectedIcon.getFgVariants().length+1];
            for (int i=0;i<selectedIcon.getFgVariants().length;i++)
                f[i] = selectedIcon.getFgVariants()[i];
            f[f.length-1] = new UColor(selectedIcon.fgColor);
            editColor = f[f.length-1];
            selectedIcon.setFgVariants(f);
        }
    }

    void selectFgSwatch(int i) {
        if (i < selectedIcon.getFgVariants().length)
            editColor = selectedIcon.getFgVariants()[i];
    }

    void addBgSwatch() {
        if (selectedIcon.getBgVariants() == null) {
            UColor[] f = new UColor[1];
            if (selectedIcon.bgColor == null)
                selectedIcon.bgColor = new UColor(0f,0f,0f);
            f[0] = new UColor(selectedIcon.bgColor);
            selectedIcon.setBgVariants(f);
            editColor = f[0];
        } else {
            UColor[] f = new UColor[selectedIcon.getBgVariants().length+1];
            for (int i=0;i<selectedIcon.getBgVariants().length;i++)
                f[i] = selectedIcon.getBgVariants()[i];
            f[f.length-1] = new UColor(selectedIcon.bgColor);
            editColor = f[f.length-1];
            selectedIcon.setBgVariants(f);
        }
    }

    void selectBgSwatch(int i) {
        if (i < selectedIcon.getBgVariants().length)
            editColor = selectedIcon.getBgVariants()[i];
    }

    @Override
    public void hearCommand(UCommand command, GLKey k) {
        if (k.k == GLFW_KEY_PAGE_UP) {
            refTerrain--;
            if (refTerrain < 0)
                refTerrain = terrains.size()-1;
            makeRefIcon();
        } else if (k.k == GLFW_KEY_PAGE_DOWN) {
            refTerrain++;
            if (refTerrain >= terrains.size())
                refTerrain = 0;
            makeRefIcon();
        }
    }

    void setupDisplayIcons() {
        displayIcons = new ArrayList<>();
        displayXs = new ArrayList<>();
        displayYs = new ArrayList<>();
        if (glyphType == TERRAIN) {
            addDisplayIcon(selectedIcon, 6,1);
            addDisplayIcon(selectedIcon,6,2);
            addDisplayIcon(selectedIcon,6,3);
            addDisplayIcon(selectedIcon,9,2);
            addDisplayIcon(selectedIcon,10,2);
            addDisplayIcon(selectedIcon,11,2);
            addDisplayIcon(selectedIcon,13,1);
            addDisplayIcon(selectedIcon,14,1);
            addDisplayIcon(selectedIcon,15, 1);
            addDisplayIcon(selectedIcon,13,2);
            addDisplayIcon(selectedIcon,14,2);
            addDisplayIcon(selectedIcon,15,2);
            addDisplayIcon(selectedIcon,13,3);
            addDisplayIcon(selectedIcon,14,3);
            addDisplayIcon(selectedIcon,15,3);
            addDisplayIcon(selectedIcon,17,3);
            addDisplayIcon(selectedIcon,18,3);
            addDisplayIcon(selectedIcon,19,3);
            addDisplayIcon(selectedIcon,18,2);
            addDisplayIcon(selectedIcon,19,2);
            addDisplayIcon(selectedIcon,19,1);
        } else {
            addDisplayIcon(selectedIcon,6,2);
            addDisplayIcon(selectedIcon,9,1);
            addDisplayIcon(selectedIcon,10,2);
            addDisplayIcon(selectedIcon,11,2);
            addDisplayIcon(selectedIcon,11,1);
            addDisplayIcon(selectedIcon,10,3);
            addDisplayIcon(selectedIcon,13,1);
            addDisplayIcon(selectedIcon,15,3);
            addDisplayIcon(selectedIcon,17,1);
            addDisplayIcon(selectedIcon,19,1);
            addDisplayIcon(selectedIcon,18,2);
            addDisplayIcon(selectedIcon,17,3);
            addDisplayIcon(selectedIcon,19,3);
        }
    }
    void addDisplayIcon(Icon template, int x, int y) {
        if (template == null) return;
        Icon icon = template.makeClone();
        icon.initialize();
        displayIcons.add(icon);
        displayXs.add(x);
        displayYs.add(y);
    }

    @Override
    public void drawContent() {

        drawString("Reload", 33,34, UColor.COLOR_YELLOW);
        drawString("Save", 37, 34, UColor.COLOR_YELLOW);
        drawString("Quit", 40, 34, UColor.COLOR_YELLOW);
        updateMouseGrid();



        // Sample displays
        renderer.drawRect(1 * gw() + xpos, 1 * gh() + ypos, 3 * gw(), 3 * gh(), UColor.COLOR_BLACK);
        drawIcon(selectedIcon, 2, 2);
        for (int i=0;i<4;i++) {
            for (int x=0;x<3;x++) {
                for (int y=0;y<3;y++) {
                    drawIcon(refIcon, 5+x+(i*4), 1+y);
                }
            }
        }
        if (displayIcons != null) {
            for (int i=0;i<displayIcons.size();i++) {
                drawIcon(displayIcons.get(i),displayXs.get(i),displayYs.get(i));
            }
        }


        // Type selector
        drawString("terrain", 23, 1, glyphType == TERRAIN ? UColor.COLOR_YELLOW : UColor.COLOR_GRAY);
        drawString("thing", 23,2, glyphType == THING ? UColor.COLOR_YELLOW : UColor.COLOR_GRAY);
        drawString("actor", 23, 3, glyphType == ACTOR ? UColor.COLOR_YELLOW : UColor.COLOR_GRAY);

        // Glyph grid
        for (int x=0;x<16;x++) {
            for (int y=0;y<16;y++) {
                int ascii = x+y*16;
                int unicode = cp437toUnicode(ascii);
                if (ascii == UnicodeToCp437(selectedIcon.getGlyph()))
                    renderer.drawRect((gridposx+x)*(gw()+gridspacex)+xpos, (gridposy+y)*(gh() + gridspacey)+ypos, gw(), gh(), UColor.COLOR_YELLOW);
                else if (ascii == cursorAscii)
                    renderer.drawRect((gridposx+x)*(gw()+gridspacex)+xpos, (gridposy+y)*(gh() + gridspacey)+ypos, gw(), gh(), UColor.COLOR_BLUE);
                renderer.drawTile(unicode, (gridposx+x)*(gw()+gridspacex)+xpos, (gridposy+y)*(gh() + gridspacey)+ypos, UColor.COLOR_GRAY);
            }
        }
        int u = selectedIcon.getGlyph();
        drawString("CP437", 1, 17+gridposy, UColor.COLOR_DARKGRAY);
        drawString(Integer.toString(UnicodeToCp437(u)), 4, 17+gridposy, UColor.COLOR_YELLOW);
        drawString("Unicode", 7,17+gridposy,UColor.COLOR_DARKGRAY);
        drawString(Integer.toString(u), 11, 17+gridposy, UColor.COLOR_YELLOW);

        // Color sliders
        drawMeter(meterx, metery, 1, 8, editColor.fR(), 1f, editColor);
        drawMeter(meterx+2,metery,1,8,editColor.fG(),1f, editColor);
        drawMeter(meterx+4,metery,1,8,editColor.fB(),1f, editColor);
        drawString("R", 20, 16, UColor.COLOR_RED);
        drawString("G", 22, 16, UColor.COLOR_GREEN);
        drawString("B", 24, 16, UColor.COLOR_BLUE);
        drawString(Integer.toString(editColor.iR()), 20, 17, UColor.COLOR_YELLOW);
        drawString(Integer.toString(editColor.iG()), 22, 17, UColor.COLOR_YELLOW);
        drawString(Integer.toString(editColor.iB()), 24, 17, UColor.COLOR_YELLOW);

        // Coloredit selector
        drawString("fg",18,20,UColor.COLOR_DARKGRAY);
        drawSwatch(selectedIcon.fgColor,20,20);
        UColor[] fgvar = selectedIcon.getFgVariants();
        if (fgvar != null) {
            for (int i = 0;i < fgvar.length;i++) {
                drawSwatch(fgvar[i], 21 + i, 20);
            }
            drawTile(43, 21 + fgvar.length, 20, UColor.COLOR_YELLOW);
        } else {
            drawTile(43,21,20,UColor.COLOR_YELLOW);
        }
        drawString("bg", 18, 22, UColor.COLOR_DARKGRAY);
        drawSwatch(selectedIcon.bgColor,20,22);
        UColor[] bgvar = selectedIcon.getBgVariants();
        if (bgvar != null) {
            for (int i = 0;i < bgvar.length;i++) {
                drawSwatch(bgvar[i], 21 + i, 22);
            }
            drawTile(43, 21 + bgvar.length, 22, UColor.COLOR_YELLOW);
        } else {
            drawTile(43, 21, 22, UColor.COLOR_YELLOW);
        }

        // Glyphedit selector
        drawString("glyph", 17, 24, UColor.COLOR_DARKGRAY);
        drawTile(selectedIcon.glyph, 20, 24, UColor.COLOR_WHITE);
        int[] gvar = selectedIcon.getGlyphVariants();
        if (gvar != null) {
            for (int i=0;i<gvar.length;i++) {
                drawTile(gvar[i], 21+i,24,UColor.COLOR_WHITE);
            }
            drawTile(43, 21+gvar.length,24, UColor.COLOR_YELLOW);
        } else {
            drawTile(43, 21, 24, UColor.COLOR_YELLOW);
        }
        drawString("plus to add variant", 21, 26, UColor.COLOR_DARKGRAY);
        drawString("rightClick to remove", 21, 27, UColor.COLOR_DARKGRAY);

        // Icon scroll list
        if (glyphType == TERRAIN)
            drawString("rightClick for background", 33, 6, UColor.COLOR_DARKGRAY);
        ArrayList<Icon> iconset = currentIconSet();
        for (int i=0;i<12;i++) {
            if (i+pageOffset < iconset.size()) {
                Icon icon = iconset.get(i + pageOffset);
                drawIcon(icon, 31, 8 + i);
                if (icon.getName().equals(selection)) {
                    drawString(icon.getName(), 33, 8 + i, UColor.COLOR_YELLOW);
                } else {
                    drawString(icon.getName(), 33, 8 + i, UColor.COLOR_GRAY);
                }
            }
        }
        listUpEnabled = false;
        listDownEnabled = false;
        if (pageOffset > 0) {
            renderer.drawTile(94, 33 * gw() + xpos, 7 * gh() + ypos, UColor.COLOR_YELLOW);
            renderer.drawTile(94, 34 * gw() + xpos, 7 * gh() + ypos, UColor.COLOR_YELLOW);
            listUpEnabled = true;
        }
        if (pageOffset+12 < iconset.size()) {
            renderer.drawTile(118, 33 * gw() + xpos, 20 * gh() + ypos, UColor.COLOR_YELLOW);
            renderer.drawTile(118, 34 * gw() + xpos, 20 * gh() + ypos, UColor.COLOR_YELLOW);
            listDownEnabled = true;
        }
    }

    void drawMeter(int x, int y, int width, int height, float val, float maxval, UColor color) {
        renderer.drawRect(x*gw()+xpos,y*gh()+ypos,width*gw(),height*gh(),UColor.COLOR_DARKGRAY);
        int dh = (int)((height*gh()) * (val/maxval));
        renderer.drawRect(x*gw()+xpos,y*gh()+ypos+(height*gh()-dh), width*gw(), dh, color);
    }

    void updateMouseGrid() {
        if (mousex >= gridposx && mousex < (gridposx+16)) {
            if (mousey >= gridposy && mousey < (gridposy+16)) {
                cursorAscii = (mousex - gridposx) + (mousey - gridposy)*16;
            }
        }
    }

    public void drawSwatch(UColor color, int x, int y) {
        UColor fillcolor = color;
        if (color == null) fillcolor = UColor.COLOR_BLACK;
        UColor bordercolor = UColor.COLOR_BLACK;
        if (color == editColor) bordercolor = UColor.COLOR_YELLOW;
        renderer.drawRectBorder(x*gw()+xpos-2,y*gh()+ypos-2,gw()+4,gh()+4,2,fillcolor,bordercolor);
        if (color == null)
            renderer.drawTile(88,x*gw()+xpos,y*gh()+ypos, UColor.COLOR_RED);
    }

    public void drawTile(int u, int x, int y, UColor color, UColor bgcolor) {
        if (bgcolor != null)
            renderer.drawRect(x*gw()+xpos,y*gh()+ypos,gw(),gh(), bgcolor);
        renderer.drawTile(u, x*gw()+xpos,y*gh()+ypos,color);
    }
    public void drawTile(int u, int x, int y, UColor color) { drawTile(u,x,y,color,null); }

    public void drawTile(int u, int x, int y, UColor color, boolean actor) {
        int pixy = y*gh()+ypos;
        if (actor && commander.config.getActorBounceAmount() > 0f) {
            pixy = y*gh()+ypos-(int)(Math.abs(Math.sin((commander.frameCounter+x*4+y*5)*commander.config.getActorBounceSpeed()*0.1f))*commander.config.getActorBounceAmount()*5f);
        }
        if (actor && commander.config.isOutlineActors())
            renderer.drawTileOutline(u, x*gw()+xpos,pixy,UColor.COLOR_BLACK);
        if (actor && commander.config.getActorBounceAmount() > 0f) {
            renderer.drawTile(u,x*gw()+xpos, pixy, color);
        } else {
            drawTile(u, x, y, color);
        }
    }

    public int cp437toUnicode(int ascii) {
        int[] lookup = {
                0, 9786, 9787, 9829, 9830, 9827, 9824,
                8226, 9688, 9675, 9689, 9794, 9792, 9834, 9835,
                9788, 9658, 9668, 8597, 8252, 182, 167, 9644,
                8616, 8593, 8595, 8594, 8592, 8735, 8596, 9650,
                9660, 32, 33, 34, 35, 36, 37, 38,
                39, 40, 41, 42, 43, 44, 45, 46,
                47, 48, 49, 50, 51, 52, 53, 54,
                55, 56, 57, 58, 59, 60, 61, 62,
                63, 64, 65, 66, 67, 68, 69, 70,
                71, 72, 73, 74, 75, 76, 77, 78,
                79, 80, 81, 82, 83, 84, 85, 86,
                87, 88, 89, 90, 91, 92, 93, 94,
                95, 96, 97, 98, 99, 100, 101, 102,
                103, 104, 105, 106, 107, 108, 109, 110,
                111, 112, 113, 114, 115, 116, 117, 118,
                119, 120, 121, 122, 123, 124, 125, 126,
                8962, 199, 252, 233, 226, 228, 224, 229,
                231, 234, 235, 232, 239, 238, 236, 196,
                197, 201, 230, 198, 244, 246, 242, 251,
                249, 255, 214, 220, 162, 163, 165, 8359,
                402, 225, 237, 243, 250, 241, 209, 170,
                186, 191, 8976, 172, 189, 188, 161, 171,
                187, 9617, 9618, 9619, 9474, 9508, 9569, 9570,
                9558, 9557, 9571, 9553, 9559, 9565, 9564, 9563,
                9488, 9492, 9524, 9516, 9500, 9472, 9532, 9566,
                9567, 9562, 9556, 9577, 9574, 9568, 9552, 9580,
                9575, 9576, 9572, 9573, 9561, 9560, 9554, 9555,
                9579, 9578, 9496, 9484, 9608, 9604, 9612, 9616,
                9600, 945, 223, 915, 960, 931, 963, 181,
                964, 934, 920, 937, 948, 8734, 966, 949,
                8745, 8801, 177, 8805, 8804, 8992, 8993, 247,
                8776, 176, 8729, 183, 8730, 8319, 178, 9632,
                160
        };
        ascii = ascii % 256;
        return lookup[ascii];
    }
    public int UnicodeToCp437(int u) {
        for (int i=0;i<256;i++) {
            if (cp437toUnicode(i) == u) {
                return i;
            }
        }
        return 0;
    }

    void doReload() {
        ArrayList<String> choices = new ArrayList<>();
        choices.add("Yes"); choices.add("No");
        UModalChoices m = new UModalChoices("Reload all icons? \nYou will lose any unsaved changes.", choices, 1, 1, true, null, this, "reload");
        commander.showModal(m);
    }

    void doSave() {
        writeJson(terrainIcons, "terrain-icons.json");
        writeJson(thingIcons, "thing-icons.json");
        writeJson(actorIcons, "actor-icons.json");
        UModalNotify m = new UModalNotify("Saved all changes!", null, 0, 0);
        commander.showModal(m);
    }

    void doQuit() {
        ArrayList<String> choices = new ArrayList<>();
        choices.add("Yes"); choices.add("No");
        UModalChoices m = new UModalChoices("Quit? \nYou will lose any unsaved changes.", choices, 1, 1, true, null, this, "quit");
        commander.showModal(m);
    }

    public void hearModalChoices(String context, String choice) {
        if (context.equals("quit") && choice.equals("Yes")) {
            dismiss();
        }
        if (context.equals("reload") && choice.equals("Yes")) {

        }
    }

    void writeJson(ArrayList<Icon> icons, String filename) {
        System.out.println("GLYPHED: writing " + filename);
        File file = new File(commander.config.getResourcePath() + "icons/" + filename);
        try (FileOutputStream stream = new FileOutputStream(file);) {
            JsonFactory jfactory = new JsonFactory();
            JsonGenerator jGenerator = jfactory.createGenerator(stream, JsonEncoding.UTF8);
            jGenerator.setCodec(objectMapper);
            jGenerator.writeObject(icons);
            jGenerator.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
