package ure.ui;

import ure.UColor;
import ure.URERendererOGL;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class UREScrollPanel {

    Font font;
    UColor fgColor, bgColor;
    int textRows, textColumns;
    int pixelw, pixelh;
    int padX, padY;
    int spacing = 1;
    int charWidth, charHeight;
    boolean suppressDuplicates = true;
    String lastMessage;
    ArrayList<String> lines;
    ArrayList<UColor> lineFades;
    BufferedImage image;
    URERendererOGL renderer;

    int xPos, yPos, width, height;
    public void setBounds(int x, int y, int xx, int yy) {
        //Hacky?
        xPos = x;
        yPos = y;
        width = xx - x;
        height = yy - y;
    }
    public UREScrollPanel(URERendererOGL theRenderer, int rows, int columns, int cw, int ch, int px, int py, UColor fg, UColor bg) {
        lines = new ArrayList<String>();
        lineFades = new ArrayList<UColor>();
        textRows = rows;
        textColumns = columns;
        charWidth = cw;
        charHeight = ch;
        padX = px;
        padY = py;
        pixelw = textColumns * cw + padX;
        pixelh = textRows * (ch + spacing) + padY;
        fgColor = fg;
        bgColor = bg;
        renderer = theRenderer;
    }

    public void addLineFade(UColor fade) {
        lineFades.add(fade);
    }

    public void print(String line) {
        if (line != "") {
            if (line != lastMessage || !suppressDuplicates) {
                lines.add(0, line);
                lastMessage = line;
            }
        }
    }

    public void renderImage() {
        int i = 0;
        while (i < textRows) {
            if (i < lines.size()) {
                UColor col;
                //MM FINISH THIS
                if (i < lineFades.size())
                    col = lineFades.get(i);
                else
                    col = lineFades.get(lineFades.size() - 1);
                renderer.renderString(xPos + padX, yPos + (padY + pixelh + 4) - ((i + 1) * (charHeight + spacing)), col, lines.get(i));
                //g.drawString(lines.get(i), padX, (padY + pixelh + 4) - ((i + 1) * (charHeight + spacing)));

            }
            i++;
        }
    }
}
