package ure;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class UIModal {
    JFrame frame;
    URERenderer renderer;
    URECamera camera;
    BufferedImage image;
    public int width, height;
    int pixelWidth, pixelHeight;
    Dimension preferredSize;
    String frameTiles;
    public UColor bgColor;

    public UIModal(int theCharWidth, int theCharHeight, URERenderer theRenderer, URECamera theCamera, UColor thebgColor) {
        renderer = theRenderer;
        camera = theCamera;
        width = theCharWidth;
        height = theCharHeight;
        bgColor = thebgColor;
        pixelWidth = theRenderer.cellWidth() * width;
        pixelHeight = theRenderer.cellHeight() * height;
        image = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_RGB);
        image.getGraphics().setColor(Color.BLACK);
        image.getGraphics().fillRect(0,0,pixelWidth,pixelHeight);
    }

    public Graphics getGraphics() { return image.getGraphics(); }

    public void renderImage() {
        DrawFrame();
    }

    void DrawFrame() {
        renderer.renderUIFrame(this);
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
}
