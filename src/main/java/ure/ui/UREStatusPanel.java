package ure.ui;

import ure.UColor;
import ure.render.URERenderer;

import java.awt.*;
import java.util.HashMap;

public class UREStatusPanel /*extends JPanel */{

    Font font;
    UColor fgColor, bgColor;
    int textRows,textColumns;
    int pixelw, pixelh;
    int padX, padY;
    int charWidth, charHeight;
    HashMap<String,TextFrag> texts;
    URERenderer renderer;
    int xPos, yPos, width, height;

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

    public UREStatusPanel(URERenderer theRenderer, int rows, int columns, int cw, int ch, int px, int py, UColor fg, UColor bg) {
        super();
        texts = new HashMap<String,TextFrag>();
        textRows = rows;
        textColumns = columns;
        renderer = theRenderer;
        charWidth = cw;
        charHeight = ch;
        padX = px;
        padY = py;
        pixelw = textRows * cw;
        pixelh = textColumns * ch;
        fgColor = fg;
        bgColor = bg;
    }

    public void setBounds(int x, int y, int xx, int yy) {
        //Hacky?
        xPos = x;
        yPos = y;
        width = xx;
        height = yy;
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

    public void renderImage() {
        renderer.addQuad(xPos, yPos, width, height, fgColor);
        renderer.addQuad(xPos+1, yPos+1, width-2, height-2, bgColor);
        //renderer.addQuad(xPos, yPos, width, height, bgColor);
        for (String textName : texts.keySet()) {
            TextFrag frag = texts.get(textName);
            renderer.drawString(xPos + frag.row * charWidth + padX, yPos + (frag.col + 1) * charHeight + padY, frag.color, frag.text);
        }
    }
}
