package ure;

import ure.terrain.URETerrain;

import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * A view pane into a UREArea
 *
 */

public class URECamera extends Canvas implements UAnimator {
    public UREArea area;
    JFrame frame;
    URERenderer renderer;
    BufferedImage image;
    float zoom = 1.0f;
    int pixelWidth, pixelHeight;
    Dimension preferredSize;
    int width, height;
    int centerX, centerY;
    int x1, y1, x2, y2;
    ULightcell lightcells[][];
    HashSet<UREActor> visibilitySources;

    UIModal modal;

    boolean allVisible = false;
    boolean allLit = true;
    float seenOpacity = 0.35f;
    float lightHueToFloors = 0.8f;
    float lightHueToWalls = 0.6f;
    float lightHueToThings = 0.5f;
    float lightHueToActors = 0.3f;

    public static int PINSTYLE_NONE = 0;
    public static int PINSTYLE_SOFT = 1;
    public static int PINSTYLE_SCREENS = 2;
    public static int PINSTYLE_HARD = 3;

    public boolean rendering;
    private BufferStrategy frameBuffer;

    private class UShadow {
        float start, end;
        public UShadow(float thes, float thee) {
            start = thes;
            end = thee;
        }
        public boolean contains(UShadow other) {
            if (start <= other.start && end >= other.end)
                return true;
            return false;
        }
        public void projectTile(int row, int col) {
            float topLeft = (float)col / ((float)row + 2f);
            float bottomRight = ((float)col + 1f) / ((float)row + 1f);
            start = topLeft;
            end = bottomRight;
        }
    }
    private class UShadowLine {
        LinkedList<UShadow> _shadows;
        public UShadowLine() {
            _shadows = new LinkedList<UShadow>();
        }
        public void add(UShadow shadow) {
            int index = 0;
            for (index = 0;index < _shadows.size();index++)
                if (_shadows.get(index).start >= shadow.start) break;
            UShadow overlappingPrevious = null;
            if (index > 0 && _shadows.get(index - 1).end > shadow.start)
                overlappingPrevious = _shadows.get(index - 1);
            UShadow overlappingNext = null;
            if (index < _shadows.size() && _shadows.get(index).start < shadow.end)
                overlappingNext = _shadows.get(index);
            if (overlappingNext != null)
                if (overlappingPrevious != null) {
                    overlappingPrevious.end = overlappingNext.end;
                    _shadows.remove(index);
                } else {
                    overlappingNext.start = shadow.start;
                }
                else {
                if (overlappingPrevious != null)
                    overlappingPrevious.end = shadow.end;
                else
                    if (index < _shadows.size())
                        _shadows.add(index, shadow);
                    else
                        _shadows.addLast(shadow);
            }
        }
        public boolean isInShadow(UShadow projection) {
            for (UShadow shadow : _shadows)
                if (shadow.contains(projection)) return true;
            return false;
        }
        public boolean isFullShadow() {
            if (_shadows.size() == 1) {
                for (UShadow shadow: _shadows)
                    if (shadow.start == 0f && shadow.end == 1f)
                        return true;
            }
            return false;
        }
    }

    public URECamera(URERenderer theRenderer, int thePixW, int thePixH, JFrame theframe) {
        setFocusable(false);
        renderer = theRenderer;
        pixelWidth = thePixW;
        pixelHeight = thePixH;
        preferredSize = new Dimension(pixelWidth, pixelHeight);
        frame = theframe;
        image = new BufferedImage(pixelWidth*8,pixelHeight*8, BufferedImage.TYPE_INT_RGB);
        visibilitySources = new HashSet<UREActor>();
        setBounds();
        lightcells = new ULightcell[width][height];
        for (int x=0;x<width;x++)
            for (int y=0;y<height;y++)
                lightcells[x][y] = new ULightcell(this);
    }

    @Override
    public Dimension getPreferredSize() {
        return preferredSize;
    }


    public void moveTo(UREArea theArea, int thex, int they) {
        if (theArea != area)
            if (area != null)
                area.unRegisterCamera(this);
        area = theArea;
        moveTo(thex,they);
        area.registerCamera(this);
    }

    public void moveTo(int thex, int they) {
        centerX = thex;
        centerY = they;
        setBounds();
    }

    private void setBounds() {
        float cellWidth = (float)renderer.cellWidth() * zoom;
        float cellHeight = (float)renderer.cellHeight() * zoom;
        width = (int)(pixelWidth / cellWidth) + 2;
        height = (int)(pixelHeight / cellHeight) + 2;
        x1 = centerX - (width / 2);
        y1 = centerY - (height / 2);
        x2 = x1 + width;
        y2 = y1 + height;
    }

    public int getWidthInCells() { return width; }
    public int getHeightInCells() { return height; }
    public Graphics getGraphics() { return image.getGraphics(); }
    public BufferedImage getImage() { return image; }
    public float getSeenOpacity() { return seenOpacity; }

    void renderLights() {
        for (int i=0;i<width;i++) {
            for (int j=0;j<height;j++) {
                lightcells[i][j].wipe();
                lightcells[i][j].setSunBrightness(area.sunBrightnessAt(x1+i,y1+j));
            }
        }
        RenderSun();
        RenderVisible();
        for (URELight light : area.lights()) {
            if (light.canTouch(this)) {
                projectLight(light);
            }
        }
    }

    void RenderSun() {
        for (int x=0;x<width;x++) {
            for (int y=0;y<height;y++) {
                float sun = getSunBrightnessAt(x,y);
                if (sun < 0.1f) {
                    int litnear = 0;
                    for (int k = -1;k < 2;k++) {
                        for (int l = -1;l < 2;l++) {
                            if (getSunBrightnessAt(x + k, y + l) > 0.9f) {
                                litnear++;
                            }
                        }
                    }
                    if (litnear > 0) {
                        if (area.blocksLight(x + x1, y + y1))
                            sun = 1f;
                        else
                            sun = 0.5f;
                    }
                }
                lightcells[x][y].setRenderedSun(sun);
            }
        }
    }
    float getSunBrightnessAt(int x, int y) {
        if (isValidXY(x, y))
            return lightcells[x][y].getSunBrightness();
        return 0f;
    }

    void RenderVisible() {
        for (int x=0;x<width;x++) {
            for (int y=0;y<height;y++) {
                setVisibilityAt(x,y,0f);
            }
        }
        Iterator<UREActor> players = visibilitySources.iterator();
        while (players.hasNext()) {
            renderVisibleFor(players.next());
        }

    }


    void renderVisibleFor(UREActor actor) {
        for (int i=-1;i<2;i++) {
            for (int j=-1;j<2;j++) {
                int dx = (actor.areaX() - x1) + i;
                int dy = (actor.areaY() - y1) + j;
                setVisibilityAt(dx, dy, 1.0f);
            }
        }
        projectVisibility(actor.areaX() - x1, actor.areaY() - y1);
    }


    public float visibilityAt(int x, int y) {
        if (allVisible)
            return 1.0f;
        if (!isValidXY(x, y))
            return 0f;
        return lightcells[x][y].visibility();
    }
    void setVisibilityAt(int x, int y, float vis) {
        if (isValidXY(x, y)) {
            lightcells[x][y].setVisibility(vis);
            if (vis > 0.5f)
                area.setSeen(x+x1, y+y1);
        }
    }

    public void addVisibilitySource(UREActor actor) {
        visibilitySources.add(actor);
    }
    public void removeVisibilitySource(UREActor actor) {
        visibilitySources.remove(actor);
    }


    void projectVisibility(int ox, int oy) {
        projectLight(ox, oy, null, true);
    }
    void projectLight(URELight light) {
        projectLight(light.x - x1, light.y - y1, light, false);
    }

    void projectLight(int ox, int oy, URELight light, boolean projectVisibility) {
        projectToCell(ox, oy, light, projectVisibility, 1f);
        for (int octant=0;octant<8;octant++) {
            UShadowLine line = new UShadowLine();
            boolean fullShadow = false;
            int row = 0;
            boolean inFrame = true;
            while (inFrame) {
                row++;
                if (!isValidXY(ox + transformOctantCol(row, 0, octant),oy + transformOctantRow(row, 0, octant)))
                    inFrame = false;
                else {
                    boolean inRow = true;
                    for (int col = 0; col <= row; col++) {
                        int dy = oy + transformOctantRow(row, col, octant);
                        int dx = ox + transformOctantCol(row, col, octant);
                        if (!isValidXY(dx, dy))
                            inRow = false;
                        else {
                            if (fullShadow) {
                                // projectToCell(dx, dy, light, projectVisibility, 0f);
                            } else {
                                UShadow projection = new UShadow(0f, 0f);
                                projection.projectTile(row, col);
                                boolean visible = !line.isInShadow(projection);
                                if (visible) {
                                    projectToCell(dx, dy, light, projectVisibility, 1f);
                                    if (area.blocksLight(dx + x1, dy + y1)) {
                                        line.add(projection);
                                        fullShadow = line.isFullShadow();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int x=0;x<width;x++) {
            for (int y=0;y<height;y++) {
                float v = visibilityAt(x,y);
                if (v == 0f) {
                    int neigh = 0;
                    if (visibilityAt(x-1,y) == 1f && !area.blocksLight(x+x1-1, y+y1)) neigh++;
                    if (visibilityAt(x+1, y) == 1f && !area.blocksLight(x+x1+1, y+y1)) neigh++;
                    if (visibilityAt(x,y-1) == 1f && !area.blocksLight(x+x1, y+y1-1)) neigh++;
                    if (visibilityAt(x, y+1) == 1f && !area.blocksLight(x+x1, y+y1+1)) neigh++;
                    if (neigh > 2)
                        projectToCell(x, y, light, projectVisibility, 0.75f);
                    else if (neigh > 1)
                        projectToCell(x, y, light, projectVisibility, 0.6f);
                }
            }
        }

    }
    void projectToCell(int x, int y, URELight light, boolean projectVisibility, float intensity) {
        if (projectVisibility)
            setVisibilityAt(x, y, intensity);
        else {
            if (light.canTouch(x + x1, y + y1)) {
                boolean blockedWallGlow = false;
                if (area.blocksLight(x+x1, y+y1)) {
                    if (visibilityAt(light.x - x1, light.y - y1) < 0.1f) {
                        blockedWallGlow = true;
                    }
                }
                if (!blockedWallGlow) {
                    receiveLight(x, y, light, intensity * light.intensityAtOffset((x + x1) - light.x, (y + y1) - light.y));
                }
            }
        }
    }
    int transformOctantRow(int row, int col, int octant) {
        switch (octant) {
            case 0:
                return col;
            case 1:
                return row;
            case 2:
                return row;
            case 3:
                return col;
            case 4:
                return -col;
            case 5:
                return -row;
            case 6:
                return -row;
            case 7:
                return -col;
        }
        return 0;
    }
    int transformOctantCol(int row, int col, int octant) {
        switch (octant) {
            case 0:
                return -row;
            case 1:
                return -col;
            case 2:
                return col;
            case 3:
                return row;
            case 4:
                return row;
            case 5:
                return col;
            case 6:
                return -col;
            case 7:
                return -row;
        }
        return 0;
    }
    boolean isValidXY(int x, int y) {
        if (x >= 0 && y >= 0 && x < width && y < height)
            return true;
        return false;
    }

    void receiveLight(int x, int y, URELight source, float intensity) {
        if (isValidXY(x, y)) {
            lightcells[x][y].receiveLight(source, intensity);
        }
    }

    UColor lightAt(int x, int y) {
        if (!isValidXY(x,y))
            return UColor.COLOR_BLACK;
        if (allLit)
            return UColor.COLOR_WHITE;
        UColor total = lightcells[x][y].light();
        for (int i=-1;i<2;i++) {
            for (int j=-1;j<2;j++) {
                URETerrain t = area.terrainAt(x+x1+i,y+y1+j);
                if (t != null)
                    if (t.glow)
                        total.addLights(t.bgColor, 0.5f);
            }
        }
        return total;
    }

    URETerrain terrainAt(int localX, int localY) {
        return area.terrainAt(localX + x1, localY + y1);
    }

    public Iterator<UREThing> thingsAt(int x, int y) {
        return area.thingsAt(x + x1, y + y1);
    }

    public void renderImage() {
        if (modal != null)
            modal.renderImage();
        long startTime = System.nanoTime();
        renderLights();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        System.out.println("lighttime " + Long.toString(duration) + "ms");
        startTime = System.nanoTime();
        renderer.renderCamera(this);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000;
        System.out.println("drawtime " + Long.toString(duration) + "ms");
        paintFrameBuffer();
    }

    public void redrawAreaCell(int ax, int ay) {
        redrawCell(ax - x1, ay - y1);
    }
    public void redrawCell(int x, int y) {
        renderer.renderCell(this, x, y);
    }

    public void paintFrameBuffer() {
        if (frameBuffer == null) {
            createBufferStrategy(2);
            frameBuffer = getBufferStrategy();
        }
        Graphics g = frameBuffer.getDrawGraphics();
        g.drawImage(image, 0, 0, null);
        if (modal != null) {
            g.drawImage(modal.getImage(), (pixelWidth / 2) - (modal.pixelWidth / 2), (pixelHeight / 2) - (modal.pixelHeight / 2), null);
        }
        frameBuffer.show();
    }

    public void animationTick() {
        for (int x=x1;x<x2;x++) {
            for (int y=y1;y<y2;y++) {
                if (area.isValidXY(x,y) && lightcells[x-x1][y-y1].visibility() > 0.1f)
                    area.cellAt(x,y).animationTick();
            }
        }
    }

    public void attachModal(UIModal modal) {
        if (this.modal != null) {
            this.detachModal();
        }
        this.modal = modal;
        renderImage();
    }

    public void detachModal() {
        this.modal = null;
        renderImage();
    }
}
