import java.awt.*;

/**
 * Created by gilmore on 6/20/2018.
 *
 * A type of terrain which can be in a cell.
 *
 */
import java.awt.image.BufferedImage;

public class URETerrain {
    public String name;
    public char filechar;
    public char icon;

    public int[] fgcolor;
    public int[] bgcolor;

    public Color fgColor;
    public Color bgColor;

    public boolean passable;
    public boolean opaque;

    public boolean isPassable() {
        return passable;
    }
    public boolean isOpaque() {
        return opaque;
    }

    public void initialize() {
        fgColor = new Color(fgcolor[0],fgcolor[1],fgcolor[2]);
        bgColor = new Color(bgcolor[0],bgcolor[1],bgcolor[2]);
    }
}
