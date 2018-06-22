import java.awt.image.BufferedImage;
import java.awt.*;
import javax.swing.*;

public class URERenderer {
    private int fontSize = 12;

    public int getCellWidth() {
        return fontSize+1;
    }
    public int getCellHeight() {
        return fontSize+2;
    }

    public void renderCamera(URECamera camera) {
        int cellw = getCellWidth();
        int cellh = getCellHeight();
        int camw = camera.getWidthInCells();
        int camh = camera.getHeightInCells();
        Font font = new Font("MorePerfectDOSVGA", Font.PLAIN, fontSize);
        Graphics g = camera.getGraphics();
        g.setColor(Color.GRAY);
        g.fillRect(0,0,camw*cellw, camh*cellh);
        for (int x=0;x<camw;x++) {
            for (int y=0;y<camh;y++) {
                g.setColor(Color.GRAY);
                g.fillRect(x*cellw, y*cellh, cellw - 1, cellh - 1);
                URETerrain t = camera.terrainAt(x,y);
                if (t != null) {
                    g.setColor(t.bgColor);
                    g.fillRect(x*cellw, y*cellh, cellw, cellh);
                    g.setColor(t.fgColor);
                    g.setFont(font);
                    g.drawString(Character.toString(t.icon), x*cellw + 1, y*cellh + cellh - 1);
                    System.out.println(Integer.toString(x) + "," + Integer.toString(y) + ": drawing a " + Character.toString(t.icon));
                } else {
                    System.out.println(Integer.toString(x) + "," + Integer.toString(y) + ": null");
                }
            }
        }
    }
}
