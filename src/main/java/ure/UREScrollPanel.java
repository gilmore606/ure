package ure;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class UREScrollPanel extends JPanel {

    Font font;
    Color fgColor, bgColor;
    int textRows, textColumns;
    int pixelw, pixelh;
    int padX, padY;
    int charWidth, charHeight;
    ArrayList<String> lines;
    ArrayList<Color> lineFades;
    BufferedImage image;

    public UREScrollPanel(Font thefont, int rows, int columns, int cw, int ch, int px, int py, UColor fg, UColor bg) {
        super();
        setLayout(null);
        setFocusable(false);
        image = new BufferedImage(rows * cw, columns * ch, BufferedImage.TYPE_INT_RGB);
        lines = new ArrayList<String>();
        lineFades = new ArrayList<Color>();
        textRows = rows;
        textColumns = columns;
        font = thefont;
        charWidth = cw;
        charHeight = ch;
        padX = px;
        padY = py;
        pixelw = textRows * cw;
        pixelh = textColumns * ch;
        fgColor = fg.makeAWTColor();
        bgColor = bg.makeAWTColor();
        setFont(font);
        setBackground(bgColor);
    }

    public void addLineFade(Color fade) {
        lineFades.add(fade);
    }

    public void print(String line) {
        lines.add(0, line);
    }

    public void renderImage() {
        Graphics g = image.getGraphics();
        g.setColor(bgColor);
        g.fillRect(0,0,pixelw,pixelh);
        g.setFont(font);
        int i = 0;
        while (i < textRows) {
            if (i < lines.size()) {
                if (i < lineFades.size())
                    g.setColor(lineFades.get(i));
                else
                    g.setColor(lineFades.get(lineFades.size()));
                g.drawString(lines.get(i), padX, padY - (i * charHeight));
            }
            i++;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(textRows * charWidth, textColumns * charHeight);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderImage();
        g.drawImage(image, 0, 0, this);
    }
}
