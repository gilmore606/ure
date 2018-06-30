package ure.ui;

import ure.UColor;
import ure.URECamera;
import ure.URERenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class UIModal {
    JFrame frame;
    URERenderer renderer;
    URECamera camera;
    BufferedImage image;
    public int width, height;
    public int pixelWidth, pixelHeight;
    Dimension preferredSize;
    String frameTiles;
    public UColor bgColor;
    HashMap<String,TextFrag> texts;

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

    public UIModal(int theCharWidth, int theCharHeight, URERenderer theRenderer, URECamera theCamera, UColor thebgColor) {
        renderer = theRenderer;
        camera = theCamera;
        width = theCharWidth;
        height = theCharHeight;
        bgColor = thebgColor;
        texts = new HashMap<String,TextFrag>();
        pixelWidth = theRenderer.cellWidth() * width;
        pixelHeight = theRenderer.cellHeight() * height;
        image = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_RGB);
        Graphics g = getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0,0,pixelWidth,pixelHeight);
    }

    public static UIModal popMessage(String message, URERenderer theRenderer, URECamera theCamera, UColor thebgcolor) {
        UIModal m = new UIModal(message.length() + 4, 3, theRenderer, theCamera, thebgcolor);
        m.addText("message", message, 1, 1, theRenderer.UItextColor.makeAWTColor());
        return m;
    }
    public Graphics getGraphics() { return image.getGraphics(); }

    public void renderImage() {

        DrawFrame();
        DrawContent();
    }

    void DrawFrame() {
        renderer.renderUIFrame(this);
    }

    void DrawContent() {
        Graphics g = getGraphics();
        for (String textName : texts.keySet()) {
            TextFrag frag = texts.get(textName);
            g.setFont(renderer.font);
            g.setColor(frag.color);
            g.drawString(frag.text, frag.row * renderer.cellWidth(), ((frag.col + 1) * renderer.cellHeight()) + 0);
        }
    }
    public BufferedImage getImage() {
        return image;
    }

    public void hearCommand(String command) {
        Dismiss();
    }

    void Dismiss() {
        camera.detachModal();
    }

    public void addText(String name, String text, int row, int col) {
        addTextFrag(new TextFrag(name, text, row, col, renderer.UItextColor.makeAWTColor()));
    }
    public void addText(String name, String text, int row, int col, Color color) {
        addTextFrag(new TextFrag(name, text, row, col, color));
    }
    void addTextFrag(TextFrag frag) {
        texts.put(frag.name, frag);
    }

}
