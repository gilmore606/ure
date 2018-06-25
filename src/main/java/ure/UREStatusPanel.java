package ure;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

public class UREStatusPanel extends JPanel {

    Font font;
    Color fgColor, bgColor;
    int textRows,textColumns;
    int pixelw, pixelh;
    int padX, padY;
    int charWidth, charHeight;
    HashMap<String,TextFrag> texts;
    BufferedImage image;

    class TextFrag {
        String name;
        String text;
        int row;
        int col;
        Color color;

        public TextFrag(String tname, String ttext, int trow, int tcol, Color tcolor) {
            name = tname;
            text = ttext;
            row = trow;
            col = tcol;
            color = tcolor;
        }
    }

    public UREStatusPanel(Font thefont, int rows, int columns, int cw, int ch, int px, int py, UColor fg, UColor bg) {
        super();
        image = new BufferedImage(rows * cw, columns * ch, BufferedImage.TYPE_INT_RGB);
        texts = new HashMap<String,TextFrag>();
        textRows = rows;
        textColumns = columns;
        font = thefont;
        charWidth = cw;
        charHeight = ch;
        padX = px;
        padY = py;
        pixelw = textRows * font.getSize();
        pixelh = textColumns * font.getSize();
        fgColor = fg.makeAWTColor();
        bgColor = bg.makeAWTColor();
        setLayout(null);
        setFocusable(false);
        setFont(font);
        setBackground(bgColor);
    }

    public void addText(String name, String text, int row, int col) {
        addTextFrag(new TextFrag(name, text, row, col, fgColor));
    }
    public void addText(String name, String text, int row, int col, Color color) {
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
        Graphics g = image.getGraphics();
        g.setColor(bgColor);
        g.fillRect(0,0,pixelw,pixelh);
        for (String textName : texts.keySet()) {
            TextFrag frag = texts.get(textName);
            g.setFont(font);
            g.setColor(frag.color);
            g.drawString(frag.text, frag.row * charWidth + padX, ((frag.col + 1) * charHeight) + padY);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(textRows * font.getSize(), textColumns * font.getSize());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderImage();
        g.drawImage(image, 0, 0, this);
    }
}
