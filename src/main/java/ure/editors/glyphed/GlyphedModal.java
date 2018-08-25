package ure.editors.glyphed;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.GLKey;
import ure.terrain.UTerrain;
import ure.ui.Icons.Icon;
import ure.ui.Icons.IconDeserializer;
import ure.ui.modals.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_UP;

public class GlyphedModal extends UModal implements HearModalChoices,HearModalStringPick {

    int gridspacex = 0;
    int gridspacey = 0;
    int gridposx = 1;
    int gridposy = 7;

    int meterx = 20;
    int metery = 8;

    String selection;
    Icon selectedIcon;
    int selectedGlyph;
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
    Set<Class<? extends Icon>> iconClasses;
    ArrayList<String> iconTypes;

    ArrayList<UTerrain> terrains;
    int refTerrain = 0;
    Icon refIcon;

    int cursorAscii = 0;

    UColor editColor;

    private Log log = LogFactory.getLog(GlyphedModal.class);

    public GlyphedModal() {
        super(null, "");
        setDimensions(46,36);
        terrains = terrainCzar.getAllTerrainTemplates();
        makeRefIcon();
        thingNames = thingCzar.getAllThings();
        actorNames = actorCzar.getAllActors();
        terrainNames = terrainCzar.getAllTerrains();
        getAllTypes();
        fillIconLists();
        setupDisplayIcons();
        selectIcon(0);
        setTitle("glyphEd");
    }

    void getAllTypes() {
        Reflections reflections = new Reflections("ure", new SubTypesScanner());
        iconClasses = reflections.getSubTypesOf(Icon.class);
        iconTypes = new ArrayList<>();
        for (Class<? extends Icon> iclass : iconClasses) {
            try {
                iconTypes.add(((Icon)(iclass.newInstance())).getTYPE());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            Icon icon = iconCzar.getTemplateByName(name);
            if (icon == null) {
                icon = new Icon("blank");
                icon.setName(name);
            }
            icon.setEntity(thingCzar.getThingByName(name));
            thingIcons.add(icon);
        }
        actorIcons = new ArrayList<>();
        boolean foundplayer = false;
        for (String name : actorNames) {
            if (name.equals("player")) foundplayer = true;
            Icon icon = iconCzar.getTemplateByName(name);
            if (icon == null) {
                icon = new Icon("blank");
                icon.setName(name);
                log.debug("created new icon for " + name);
            } else {
                log.debug("loaded existing icon for " + name);
            }
            icon.setEntity(actorCzar.getActorByName(name));
            actorIcons.add(icon);
        }
        if (!foundplayer) {
            Icon playericon = new Icon("blank");
            playericon.setName("player");
            playericon.setGlyph(64);
            playericon.setFgColor(new UColor(UColor.WHITE));
            actorIcons.add(playericon);
        }
        terrainIcons = new ArrayList<>();
        for (String name : terrainNames) {
            Icon icon = iconCzar.getTemplateByName(name);
            if (icon == null) {
                icon = new Icon("blank");
                icon.setName(name);
            }
            icon.setEntity(terrainCzar.getTerrainByName(name));
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
        selectedGlyph = 0;
        if (editColor == null) {
            selectedIcon.fgColor = new UColor(1f,1f,1f,1f);
            editColor = selectedIcon.fgColor;
        }
        setupDisplayIcons();
    }

    int selectedUnicode() {
        if (selectedGlyph == 0)
            return selectedIcon.getGlyph();
        return selectedIcon.getGlyphVariants()[selectedGlyph-1];
    }

    void selectRefIcon(int i) {
        if (i+pageOffset >= currentIconSet().size()) return;
        Icon ic = currentIconSet().get(i+pageOffset);
        if (ic == null) return;
        refIcon = ic;
    }

    void changeGlyph(int unicode) {
        if (selectedGlyph == 0) {
            selectedIcon.setGlyph(unicode);
        } else {
            int[] var = selectedIcon.getGlyphVariants();
            var[selectedGlyph-1] = unicode;
        }
        setupDisplayIcons();
    }

    @Override
    public void mouseClick() {
        updateMouseGrid();

        // Glyph grid
        if (mousex >= gridposx && mousex < (gridposx+16)) {
            if (mousey >= gridposy && mousey < (gridposy + 16)) {
                changeGlyph(cp437toUnicode(cursorAscii));
            }
        }

        // Type select
        if (mousex >= 3 && mousex <= 9) {
            if (mousey == 19+gridposy) {
                selectType();
            }
        }

        // Color sliders
        if (mousex >= meterx && mousex <= meterx+13) {
            if (mousey >= metery && mousey <= (metery+8)) {
                int meteri = (mousex-meterx);

                int mousepy = commander.mouseY() - (absoluteY()+metery*gh());
                float level = (float)mousepy / ((float)8*gh());
                level = 1f-level;
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

        // Glyphedit selector
        if (mousex >= 20 && mousex <= 28) {
            if (mousey == 24) {
                selectGlyphSwatch(mousex-20);
            }
        }

        // Type param sliders
        if (mousex >= 6 && mousex <= 16) {
            if (mousey >= 21+gridposy && mousey <= 25+gridposy) {
                int meteri = (mousey-(21+gridposy));
                int mousepx = commander.mouseX() - (absoluteX()+6*gh());
                float level = (float)mousepx / ((float)10*gw());
                if (meteri == 0)
                    selectedIcon.setAnimAmpX(level);
                else if (meteri == 2)
                    selectedIcon.setAnimAmpY(level);
                else if (meteri == 4)
                    selectedIcon.setAnimFreq(level);
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

        // Glyphedit selector
        if (mousex >= 20 && mousex <= 28) {
            if (mousey == 24) {
                deleteGlyphSwatch(mousex-20);
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

    void selectGlyphSwatch(int i) {
        int varCount;
        if (selectedIcon.getGlyphVariants() == null)
            varCount = 0;
        else
            varCount = selectedIcon.getGlyphVariants().length;
        if (i == 0) {
            selectedGlyph = 0;
        } else if (i == varCount+1) {
            addGlyphSwatch();
        } else if (i <= varCount) {
            selectedGlyph = i;
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
            if (set == null) return;
            i -= 1;
            if (i >= set.length) return;
            if (editColor == set[i]) editColor = type == 0 ? selectedIcon.fgColor : selectedIcon.bgColor;
            UColor[] nuset = new UColor[set.length-1];
            for (int j=0;j<i;j++)
                nuset[j] = set[j];
            for (int j=i;j<nuset.length;j++)
                nuset[j] = set[j+1];
            if (nuset.length == 0)
                nuset = null;
            if (type == 0)
                selectedIcon.setFgVariants(nuset);
            else
                selectedIcon.setBgVariants(nuset);
        }
    }

    void deleteGlyphSwatch(int i) {
        if (i == 0) return;
        int[] set = selectedIcon.getGlyphVariants();
        if (set == null) return;
        i -= 1;
        if (i >= set.length) return;
        if (selectedGlyph == i+1) selectedGlyph -= 1;
        int[] nuset = new int[set.length-1];
        for (int j=0;j<i;j++)
            nuset[j] = set[j];
        for (int j=i;j<nuset.length;j++)
            nuset[j] = set[j+1];
        selectedIcon.setGlyphVariants(nuset);
        if (selectedGlyph > nuset.length)
            selectedGlyph = nuset.length;
    }

    void addGlyphSwatch() {
        if (selectedIcon.getGlyphVariants() == null) {
            int[] gv = new int[1];
            gv[0] = 33;
            selectedIcon.setGlyphVariants(gv);
            selectedGlyph = 1;
        } else {
            int[] gv = new int[selectedIcon.getGlyphVariants().length+1];
            for (int i=0;i<selectedIcon.getGlyphVariants().length;i++)
                gv[i] = selectedIcon.getGlyphVariants()[i];
            gv[gv.length-1] = 33;
            selectedIcon.setGlyphVariants(gv);
            selectedGlyph++;
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
        if (selectedIcon.getFgVariants() == null)
            return;
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
        if (selectedIcon.getBgVariants() == null)
            return;
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

        drawString("Reload", 33,34, UColor.YELLOW);
        drawString("Save", 37, 34, UColor.YELLOW);
        drawString("Quit", 40, 34, UColor.YELLOW);
        updateMouseGrid();


        drawString(selectedIcon.getName(), 1, 5, null);
        // Sample displays
        renderer.drawRectBorder(1 * gw() + absoluteX(), 1 * gh() + absoluteY(), 3 * gw(), 3 * gh(), 1, UColor.BLACK, UColor.GRAY);
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
        drawString("terrain", 23, 1, glyphType == TERRAIN ? UColor.YELLOW : UColor.GRAY);
        drawString("entity", 23,2, glyphType == THING ? UColor.YELLOW : UColor.GRAY);
        drawString("actor", 23, 3, glyphType == ACTOR ? UColor.YELLOW : UColor.GRAY);

        // Glyph grid
        for (int x=0;x<16;x++) {
            for (int y=0;y<16;y++) {
                int ascii = x+y*16;
                int unicode = cp437toUnicode(ascii);
                if (unicode == selectedUnicode())
                    renderer.drawRect((gridposx+x)*(gw()+gridspacex)+absoluteX(), (gridposy+y)*(gh() + gridspacey)+absoluteY(), gw(), gh(), UColor.YELLOW);
                else if (ascii == cursorAscii)
                    renderer.drawRect((gridposx+x)*(gw()+gridspacex)+absoluteX(), (gridposy+y)*(gh() + gridspacey)+absoluteY(), gw(), gh(), UColor.BLUE);
                renderer.drawTile(unicode, (gridposx+x)*(gw()+gridspacex)+absoluteX(), (gridposy+y)*(gh() + gridspacey)+absoluteY(), UColor.GRAY);
            }
        }
        int u = selectedIcon.getGlyph();
        drawString("CP437", 1, 17+gridposy, UColor.DARKGRAY);
        drawString(Integer.toString(UnicodeToCp437(u)), 4, 17+gridposy, UColor.YELLOW);
        drawString("Unicode", 7,17+gridposy,UColor.DARKGRAY);
        drawString(Integer.toString(u), 11, 17+gridposy, UColor.YELLOW);

        // Color sliders
        drawMeter(meterx, metery, 1, 8, editColor.fR(), 1f, editColor);
        drawMeter(meterx+2,metery,1,8,editColor.fG(),1f, editColor);
        drawMeter(meterx+4,metery,1,8,editColor.fB(),1f, editColor);
        drawString("R", 20, 16, UColor.RED);
        drawString("G", 22, 16, UColor.GREEN);
        drawString("B", 24, 16, UColor.BLUE);
        drawString(Integer.toString(editColor.iR()), 20, 17, UColor.YELLOW);
        drawString(Integer.toString(editColor.iG()), 22, 17, UColor.YELLOW);
        drawString(Integer.toString(editColor.iB()), 24, 17, UColor.YELLOW);

        // Coloredit selector
        drawString("fg",18,20,UColor.DARKGRAY);
        drawSwatch(selectedIcon.fgColor,20,20);
        UColor[] fgvar = selectedIcon.getFgVariants();
        if (fgvar != null) {
            for (int i = 0;i < fgvar.length;i++) {
                drawSwatch(fgvar[i], 21 + i, 20);
            }
            drawTile(43, 21 + fgvar.length, 20, UColor.YELLOW);
        } else {
            drawTile(43,21,20,UColor.YELLOW);
        }
        drawString("bg", 18, 22, UColor.DARKGRAY);
        drawSwatch(selectedIcon.bgColor,20,22);
        UColor[] bgvar = selectedIcon.getBgVariants();
        if (bgvar != null) {
            for (int i = 0;i < bgvar.length;i++) {
                drawSwatch(bgvar[i], 21 + i, 22);
            }
            drawTile(43, 21 + bgvar.length, 22, UColor.YELLOW);
        } else {
            drawTile(43, 21, 22, UColor.YELLOW);
        }

        // Glyphedit selector
        drawString("glyph", 17, 24, UColor.DARKGRAY);
        drawTileSwatch(selectedIcon.glyph, 20, 24, 0);
        int[] gvar = selectedIcon.getGlyphVariants();
        if (gvar != null) {
            for (int i=0;i<gvar.length;i++) {
                drawTileSwatch(gvar[i], 21+i, 24, i+1);
            }
            drawTile(43, 21+gvar.length,24, UColor.YELLOW);
        } else {
            drawTile(43, 21, 24, UColor.YELLOW);
        }
        drawString("plus to add variant", 21, 26, UColor.DARKGRAY);
        drawString("rightClick to remove", 21, 27, UColor.DARKGRAY);

        // Icon scroll list
        if (glyphType == TERRAIN)
            drawString("rightClick for background", 33, 6, UColor.DARKGRAY);
        ArrayList<Icon> iconset = currentIconSet();
        for (int i=0;i<12;i++) {
            if (i+pageOffset < iconset.size()) {
                Icon icon = iconset.get(i + pageOffset);
                drawIcon(icon, 31, 8 + i);
                if (icon.getName().equals(selection)) {
                    drawString(icon.getName(), 33, 8 + i, UColor.YELLOW);
                } else {
                    drawString(icon.getName(), 33, 8 + i, UColor.GRAY);
                }
            }
        }
        listUpEnabled = false;
        listDownEnabled = false;
        if (pageOffset > 0) {
            renderer.drawTile(8593, 33 * gw() + absoluteX(), 7 * gh() + absoluteY(), UColor.YELLOW);
            renderer.drawTile(8593, 34 * gw() + absoluteX(), 7 * gh() + absoluteY(), UColor.YELLOW);
            listUpEnabled = true;
        }
        if (pageOffset+12 < iconset.size()) {
            renderer.drawTile(8595, 33 * gw() + absoluteX(), 20 * gh() + absoluteY(), UColor.YELLOW);
            renderer.drawTile(8595, 34 * gw() + absoluteX(), 20 * gh() + absoluteY(), UColor.YELLOW);
            listDownEnabled = true;
        }

        // Type selector
        drawString("type", 1, 19+gridposy, UColor.DARKGRAY);
        drawString(selectedIcon.getTYPE(), 4, 19+gridposy, UColor.YELLOW);

        // Type params
        drawString("animAmpX", 1, 21+gridposy, UColor.GRAY);
        drawSlider(6,21+gridposy,10,selectedIcon.getAnimAmpX(), UColor.YELLOW);
        drawString("animAmpY", 1, 23+gridposy, UColor.GRAY);
        drawSlider(6,23+gridposy,10,selectedIcon.getAnimAmpY(), UColor.YELLOW);
        drawString("animFreq", 1, 25+gridposy, UColor.GRAY);
        drawSlider(6,25+gridposy,10,selectedIcon.getAnimFreq(), UColor.YELLOW);
    }

    void drawSlider(int x, int y, int length, float val, UColor color) {
        renderer.drawRectBorder(x*gw()+absoluteX(), y*gw()+absoluteY(), length*gw(),gh(),1,UColor.DARKGRAY, color);
        renderer.drawRect(x*gw()+absoluteX(), y*gw()+absoluteY(), (int)((length*val)*gw()), gh(), color);
    }

    void drawMeter(int x, int y, int width, int height, float val, float maxval, UColor color) {
        renderer.drawRect(x*gw()+absoluteX(),y*gh()+absoluteY(),width*gw(),height*gh(),UColor.DARKGRAY);
        int dh = (int)((height*gh()) * (val/maxval));
        renderer.drawRect(x*gw()+absoluteX(),y*gh()+absoluteY()+(height*gh()-dh), width*gw(), dh, color);
    }

    void updateMouseGrid() {
        if (mousex >= gridposx && mousex < (gridposx+16)) {
            if (mousey >= gridposy && mousey < (gridposy+16)) {
                cursorAscii = (mousex - gridposx) + (mousey - gridposy)*16;
                return;
            }
        }
        cursorAscii = -1;
    }

    void drawTileSwatch(int unicode, int x, int y, int swatchi) {
        if (swatchi == selectedGlyph)
            renderer.drawRectBorder(x*gw()+absoluteX()-2,y*gw()+absoluteY()-2, gw()+4,gh()+4,2,UColor.BLACK, UColor.YELLOW);
        drawTile(unicode,x,y,UColor.WHITE);
    }

    void drawSwatch(UColor color, int x, int y) {
        UColor fillcolor = color;
        if (color == null) fillcolor = UColor.BLACK;
        UColor bordercolor = UColor.BLACK;
        if (color == editColor) bordercolor = UColor.YELLOW;
        renderer.drawRectBorder(x*gw()+absoluteX()-2,y*gh()+absoluteY()-2,gw()+4,gh()+4,2,fillcolor,bordercolor);
        if (color == null)
            renderer.drawTile(88,x*gw()+absoluteX(),y*gh()+absoluteY(), UColor.RED);
    }

    public void drawTile(int u, int x, int y, UColor color, UColor bgcolor) {
        if (bgcolor != null)
            renderer.drawRect(x*gw()+absoluteX(),y*gh()+absoluteY(),gw(),gh(), bgcolor);
        renderer.drawTile(u, x*gw()+absoluteX(),y*gh()+absoluteY(),color);
    }
    public void drawTile(int u, int x, int y, UColor color) { drawTile(u,x,y,color,null); }

    public void drawTile(int u, int x, int y, UColor color, boolean actor) {
        int pixy = y*gh()+absoluteY();
        if (actor && commander.config.getActorBounceAmount() > 0f) {
            pixy = y*gh()+absoluteY()-(int)(Math.abs(Math.sin((commander.frameCounter+x*4+y*5)*commander.config.getActorBounceSpeed()*0.1f))*commander.config.getActorBounceAmount()*5f);
        }
        if (actor && commander.config.isOutlineActors())
            renderer.drawTileOutline(u, x*gw()+absoluteX(),pixy,UColor.BLACK);
        if (actor && commander.config.getActorBounceAmount() > 0f) {
            renderer.drawTile(u,x*gw()+absoluteX(), pixy, color);
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
        UModalChoices m = new UModalChoices("Reload all icons? \nYou will lose any unsaved changes.", new String[]{"Yes", "No"},this, "reload");
        commander.showModal(m);
    }

    void doSave() {
        writeJson(terrainIcons, "terrain-icons.json");
        writeJson(thingIcons, "entity-icons.json");
        writeJson(actorIcons, "actor-icons.json");
        UModalNotify m = new UModalNotify("Saved all changes!");
        commander.showModal(m);
    }

    void doQuit() {
        UModalChoices m = new UModalChoices("Quit? \nYou will lose any unsaved changes.", new String[]{"Yes", "No"}, this, "quit");
        commander.showModal(m);
    }

    public void hearModalChoices(String context, String choice) {
        if (context.equals("quit") && choice.equals("Yes")) {
            dismiss();
            iconCzar.loadIcons();
        }
        if (context.equals("reload") && choice.equals("Yes")) {

        }
    }

    void writeJson(ArrayList<Icon> icons, String filename) {
        log.info("writing " + filename);
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

    void selectType() {
        UModalStringPick modal = new UModalStringPick(null, iconTypes.toArray(new String[iconTypes.size()]), this, "type");
        modal.setChildPosition(4,19+gridposy, this);
        commander.showModal(modal);
    }

    public void hearModalStringPick(String context, String pick) {
        if (context.equals("type"))
            changeType(pick);
    }

    void changeType(String type) {
        IconDeserializer iconDeserializer = new IconDeserializer(objectMapper);
        Class<? extends Icon> newClass = iconDeserializer.classForType(type);
        try {
            Icon newIcon = newClass.newInstance();
            newIcon.copyFrom(selectedIcon);
            newIcon.setTYPE(type);
            iconCzar.replaceTemplate(selectedIcon.getName(), newIcon);
            ArrayList<Icon> iconset = currentIconSet();
            for (int i=0;i<iconset.size();i++) {
                if (iconset.get(i) == selectedIcon) {
                    iconset.set(i, newIcon);
                }
            }
            selectedIcon = newIcon;
            selection = selectedIcon.getName();
            editColor = selectedIcon.fgColor;
            setupDisplayIcons();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
