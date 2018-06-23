/**
 * A view pane into a UREArea
 *
 */
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.*;
import java.util.Iterator;

public class URECamera extends JPanel {
    public UREArea area;
    URERenderer renderer;
    BufferedImage image;
    float zoom = 1.0f;
    int pixelWidth, pixelHeight;
    int width, height;
    int centerX, centerY;
    int x1, y1, x2, y2;
    ULightcell lightcells[][];

    boolean allVisible = false;
    float seenOpacity = 0.5f;
    float lightHueToFloors = 0.8f;
    float lightHueToWalls = 0.6f;
    float lightHueToThings = 0.5f;
    float lightHueToActors = 0.3f;

    public URECamera(URERenderer theRenderer, int thePixW, int thePixH) {
        renderer = theRenderer;
        pixelWidth = thePixW;
        pixelHeight = thePixH;
        image = new BufferedImage(pixelWidth*8,pixelHeight*8, BufferedImage.TYPE_INT_RGB);
    }

    public void moveTo(UREArea theArea, int thex, int they) {
        area = theArea;
        moveTo(thex,they);
    }

    public void moveTo(int thex, int they) {
        centerX = thex;
        centerY = they;
        setBounds();
    }

    private void setBounds() {
        float cellWidth = (float)renderer.getCellWidth() * zoom;
        float cellHeight = (float)renderer.getCellHeight() * zoom;
        width = (int)(pixelWidth / cellWidth) + 1;
        height = (int)(pixelHeight / cellHeight) + 1;
        x1 = centerX - (width / 2);
        y1 = centerY - (height / 2);
        x2 = x1 + width;
        y2 = y1 + height;
        System.out.println("camera bounds are " + Integer.toString(x1) + "," + Integer.toString(y1) + " - " + Integer.toString(x2) + "," + Integer.toString(y2));
    }

    public int getWidthInCells() { return width; }
    public int getHeightInCells() { return height; }
    public Graphics getGraphics() { return image.getGraphics(); }
    public BufferedImage getImage() { return image; }

    public void renderLights() {
        for (int i=0;i<width;i++) {
            for (int j=0;j<height;j++) {
                lightcells[i][j].wipe();
            }
        }
        for (URELight light : area.lights()) {
            if (light.canTouch(this)) {
                light.renderInto(this);
            }
        }
    }

    public void receiveLight(int areax, int areay, URELight source, float intensity) {
        int cellx = x1 - areax;
        int celly = y1 - areay;
        if (cellx >= 0 && celly >= 0 && cellx < width && celly < height) {
            lightcells[cellx][celly].receiveLight(source, intensity);
        }
    }

    public URETerrain terrainAt(int localX, int localY) {
        return area.terrainAt(localX + x1, localY + y1);
    }

    public Iterator<UREThing> thingsAt(int x, int y) {
        return area.thingsAt(x + x1, y + y1);
    }

    public void renderImage() {
        renderer.renderCamera(this);
    }

    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }
}
