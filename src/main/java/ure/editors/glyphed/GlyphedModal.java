package ure.editors.glyphed;

import ure.commands.UCommand;
import ure.math.UColor;
import ure.sys.GLKey;
import ure.terrain.UTerrain;
import ure.ui.Icon;
import ure.ui.modals.UModal;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_UP;

public class GlyphedModal extends UModal {

    int gridspacex = 0;
    int gridspacey = 0;
    int gridposx = 1;
    int gridposy = 7;

    int glyphType = 0;
    static int TERRAIN = 0;
    static int THING = 1;
    static int ACTOR = 2;

    ArrayList<UTerrain> terrains;
    int refTerrain = 0;
    Icon refIcon;

    int currentAscii = 2;
    int cursorAscii = 0;
    String[] jsonLines;

    UColor bgColor;
    UColor fgColor;

    public GlyphedModal() {
        super(null, "", null);
        setDimensions(42,36);
        fgColor = new UColor(1f,1f,1f);
        bgColor = new UColor(0f,0f,0f);
        terrains = terrainCzar.getAllTerrainTemplates();
        makeRefIcon();
        updateJson();
    }

    void makeRefIcon() {
        UTerrain t = terrains.get(refTerrain);
        UTerrain t2 = t.makeClone();
        t2.initializeAsCloneFrom(t);
        refIcon = t2.getIcon();
    }

    @Override
    public void mouseClick() {
        if (mousex >= gridposx && mousex < (gridposx+16)) {
            if (mousey >= gridposy && mousey < (gridposy + 16)) {
                updateMouseGrid();
                currentAscii = cursorAscii;
                updateJson();
            }
        }
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

    @Override
    public void drawContent() {

        drawString("pgUp/pgDn: change terrain bg    ", 1, 34, UColor.COLOR_GRAY);
        updateMouseGrid();

        int u = cp437toUnicode(currentAscii);
        drawString("CP437 ASCII:", 33, 0, UColor.COLOR_GRAY);
        drawString(Integer.toString(currentAscii), 33, 1, UColor.COLOR_YELLOW);
        drawString(Integer.toString(u), 33, 3, UColor.COLOR_YELLOW);
        drawString("Unicode int:", 33,2,UColor.COLOR_GRAY);

        if (glyphType == TERRAIN) {
            renderer.drawRect(1 * gw() + xpos, 1 * gh() + ypos, 3 * gw(), 3 * gh(), UColor.COLOR_BLACK);

            for (int i=0;i<4;i++) {
                for (int x=0;x<3;x++) {
                    for (int y=0;y<3;y++) {
                        drawIcon(refIcon, 9+x+(i*4), 1+y);
                    }
                }
            }
            drawTile(u, 2, 2, fgColor, bgColor);
            drawTile(u, 10, 1, fgColor, bgColor);
            drawTile(u, 10, 2, fgColor, bgColor);
            drawTile(u, 10, 3, fgColor, bgColor);
            drawTile(u, 13, 2, fgColor, bgColor);
            drawTile(u, 14, 2, fgColor, bgColor);
            drawTile(u, 15, 2, fgColor, bgColor);
            drawTile(u, 17, 1, fgColor, bgColor);
            drawTile(u, 18, 1, fgColor, bgColor);
            drawTile(u, 19, 1, fgColor, bgColor);
            drawTile(u, 17, 2, fgColor, bgColor);
            drawTile(u, 18, 2, fgColor, bgColor);
            drawTile(u, 19, 2, fgColor, bgColor);
            drawTile(u, 17, 3, fgColor, bgColor);
            drawTile(u, 18, 3, fgColor, bgColor);
            drawTile(u, 19, 3, fgColor, bgColor);
            drawTile(u, 21, 3, fgColor, bgColor);
            drawTile(u, 22, 3, fgColor, bgColor);
            drawTile(u, 23, 3, fgColor, bgColor);
            drawTile(u, 22, 2, fgColor, bgColor);
            drawTile(u, 23, 2, fgColor, bgColor);
            drawTile(u, 23, 1, fgColor, bgColor);
        }

        for (int x=0;x<16;x++) {
            for (int y=0;y<16;y++) {
                int ascii = x+y*16;
                int unicode = cp437toUnicode(ascii);
                if (ascii == currentAscii)
                    renderer.drawRect((gridposx+x)*(gw()+gridspacex)+xpos, (gridposy+y)*(gh() + gridspacey)+ypos, gw(), gh(), UColor.COLOR_BLUE);
                else if (ascii == cursorAscii)
                    renderer.drawRect((gridposx+x)*(gw()+gridspacex)+xpos, (gridposy+y)*(gh() + gridspacey)+ypos, gw(), gh(), UColor.COLOR_YELLOW);
                renderer.drawTile(unicode, (gridposx+x)*(gw()+gridspacex)+xpos, (gridposy+y)*(gh() + gridspacey)+ypos, UColor.COLOR_LIGHTRED);
            }
        }

        for (int i=0;i<8;i++) {
            drawTile(' ', 20, 8+i, fgColor, meter(fgColor.fR(), 8-i, 8) ? UColor.COLOR_RED : UColor.COLOR_DARKGRAY);
            drawTile(' ', 22, 8+i, fgColor, meter(fgColor.fG(), 8-i, 8) ? UColor.COLOR_GREEN : UColor.COLOR_DARKGRAY);
            drawTile(' ', 24, 8+i, fgColor, meter(fgColor.fB(), 8-i, 8) ? UColor.COLOR_BLUE : UColor.COLOR_DARKGRAY);
            drawTile(' ', 28, 8+i, fgColor, meter(bgColor.fR(), 8-i, 8) ? UColor.COLOR_RED : UColor.COLOR_DARKGRAY);
            drawTile(' ', 30, 8+i, fgColor, meter(bgColor.fG(), 8-i, 8) ? UColor.COLOR_GREEN : UColor.COLOR_DARKGRAY);
            drawTile(' ', 32, 8+i, fgColor, meter(bgColor.fB(), 8-i, 8) ? UColor.COLOR_BLUE : UColor.COLOR_DARKGRAY);
        }
        drawString("fgColor",21,19,null);
        drawString("bgColor",29,19,null);
        drawTile(0,19,19,null, fgColor);
        drawTile(0,27,19,null,bgColor);
        drawString(Integer.toString(fgColor.iR()), 20, 17, UColor.COLOR_YELLOW);
        drawString(Integer.toString(fgColor.iG()), 22, 17, UColor.COLOR_YELLOW);
        drawString(Integer.toString(fgColor.iB()), 24, 17, UColor.COLOR_YELLOW);
        drawString(Integer.toString(bgColor.iR()), 28, 17, UColor.COLOR_YELLOW);
        drawString(Integer.toString(bgColor.iG()), 30, 17, UColor.COLOR_YELLOW);
        drawString(Integer.toString(bgColor.iB()), 32, 17, UColor.COLOR_YELLOW);

        renderer.drawRect((2*gw()+xpos)-gw()/2, (28*gh()+ypos)-gh()/2, gw()*26,gh()*4,UColor.COLOR_LIGHTGRAY);
        drawStrings(jsonLines, 2, 28, UColor.COLOR_BLACK);
    }

    boolean meter(float level, int cell, int maxcell) {
        if (level == 0f) return false;
        if (level >= ((float)cell/(float)maxcell))
            return true;
        return false;
    }

    void updateMouseGrid() {
        if (mousex >= gridposx && mousex < (gridposx+16)) {
            if (mousey >= gridposy && mousey < (gridposy+16)) {
                cursorAscii = (mousex - gridposx) + (mousey - gridposy)*16;
            }
        }
    }

    void updateJson() {
        String json = "\"glyph\": \"\\u" + Integer.toString(cp437toUnicode(currentAscii)) + "\"";
        json = json + "\n ";
        json = json + "\"fgcolor\": [ " + Integer.toString(fgColor.iR()) + "," + Integer.toString(fgColor.iG()) + "," + Integer.toString(fgColor.iB()) + " ]";
        json = json + "\n ";
        json = json + "\"bgcolor\": [ " + Integer.toString(bgColor.iR()) + "," + Integer.toString(bgColor.iG()) + "," + Integer.toString(bgColor.iB()) + " ]";
        jsonLines = splitLines(json);
        StringSelection stringSelection = new StringSelection(json);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public void drawTile(int u, int x, int y, UColor color, UColor bgcolor) {
        if (bgcolor != null)
            renderer.drawRect(x*gw()+xpos,y*gh()+ypos,gw(),gh(), bgcolor);
        renderer.drawTile(u, x*gw()+xpos,y*gh()+ypos,color);
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
}
