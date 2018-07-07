package ure.ui;

import ure.UColor;
import ure.render.URenderer;

import java.util.HashMap;

public class UREStatusPanel extends View {

    UColor fgColor, bgColor, borderColor;
    int textRows,textColumns;
    int pixelw, pixelh;
    int padX, padY;
    int charWidth, charHeight;
    HashMap<String,TextFrag> texts;

    class TextFrag {
        String name;
        String text;
        int row;
        int col;
        UColor color;

        public TextFrag(String tname, String ttext, int trow, int tcol, UColor tcolor) {
            name = tname;
            text = ttext;
            row = trow;
            col = tcol;
            color = tcolor;
        }
    }

    public UREStatusPanel(int rows, int columns, int cw, int ch, int px, int py, UColor fg, UColor bg, UColor borderc) {
        super();
        texts = new HashMap<String,TextFrag>();
        textRows = rows;
        textColumns = columns;
        charWidth = cw;
        charHeight = ch;
        padX = px;
        padY = py;
        pixelw = textRows * cw;
        pixelh = textColumns * ch;
        fgColor = fg;
        bgColor = bg;
        borderColor = borderc;
    }

    public void addText(String name, String text, int row, int col) {
        addTextFrag(new TextFrag(name, text, row, col, fgColor));
    }
    public void addText(String name, String text, int row, int col, UColor color) {
        addTextFrag(new TextFrag(name, text, row, col, color));
    }
    void addTextFrag(TextFrag frag) {
        texts.put(frag.name, frag);
    }

    public void setText(String name, String text) {
        TextFrag frag = texts.get(name);
        frag.text = text;
    }

    @Override
    public void draw(URenderer renderer) {
        renderer.drawRectBorder(1, 1, width-2, height-2, 1, bgColor, borderColor);
        //renderer.addQuad(xPos, yPos, width, height, bgColor);
        for (String textName : texts.keySet()) {
            TextFrag frag = texts.get(textName);
            renderer.drawString(frag.row * charWidth + padX, (frag.col + 1) * charHeight + padY, frag.color, frag.text);
        }
    }
}
