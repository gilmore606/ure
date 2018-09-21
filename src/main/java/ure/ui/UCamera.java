package ure.ui;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ure.actors.UActor;
import ure.areas.UArea;
import ure.areas.UCell;
import ure.math.Dimap;
import ure.math.UColor;
import ure.math.SimplexNoise;
import ure.math.UPath;
import ure.render.URenderer;
import ure.sys.Injector;
import ure.sys.UAnimator;
import ure.sys.UCommander;
import ure.sys.UConfig;
import ure.sys.events.ResolutionChangedEvent;
import ure.terrain.UTerrain;
import ure.things.UThing;
import ure.ui.particles.UParticle;

import javax.inject.Inject;
import java.util.*;

/**
 * A view pane into a UREArea
 *
 */

public class UCamera extends View implements UAnimator {

    @Inject
    UCommander commander;
    @Inject
    UConfig config;
    @Inject
    URenderer renderer;
    @Inject
    EventBus bus;

    Dimap testmap;

    public UArea area;
    float zoom = 1.0f;
    public int columns, rows;
    private int centerColumn, centerRow;

    public int leftEdge, topEdge, rightEdge, bottomEdge;
    private ULightcell lightcells[][];
    private HashSet<UActor> visibilitySources;
    private boolean lightEnable = true;

    private int cursorX, cursorY;
    private int[][] cursorPath;

    public static int PINSTYLE_NONE = 0;
    public static int PINSTYLE_SOFT = 1;
    public static int PINSTYLE_SCREENS = 2;
    public static int PINSTYLE_HARD = 3;

    private SimplexNoise noise = new SimplexNoise();

    private Log log = LogFactory.getLog(UCamera.class);

    private UShadowLine shadowLine;

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
        public void reset() {
            _shadows.clear();
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

    public UCamera(int x, int y, int width, int height) {
        Injector.getAppComponent().inject(this);
        visibilitySources = new HashSet<>();
        shadowLine = new UShadowLine();
        setBounds(x, y, width, height);
        setupGrid();
    }

    public void resizeView(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        resize(w,h);
        renderLights();
    }

    public void resize(int newwidth, int newheight) {
        setBounds(x, y, newwidth, newheight);
        setupGrid();
    }

    public int getCenterColumn() {
        return centerColumn;
    }

    public int getCenterRow() {
        return centerRow;
    }

    public void moveTo(UArea theArea, int thex, int they) {
        boolean areachange = false;
        if (area != null && theArea != area) {
            areachange = true;
        }
        area = theArea;
        moveTo(thex,they);
        if (areachange) {
            testmap = new Dimap(area, commander);
            renderer.render();
        }
    }

    public void moveTo(int thex, int they) {
        centerColumn = thex;
        centerRow = they;
        setupGrid();
    }

    public void setupGrid() {
        float cellWidth = (float)config.getTileWidth() * zoom;
        float cellHeight = (float)config.getTileHeight() * zoom;
        columns = (int)(width / cellWidth);
        rows = (int)(height / cellHeight);
        lightcells = new ULightcell[columns][rows];
        float[] scales = new float[]{15f,22f};
        for (int col = 0; col<columns; col++) {
            for (int row = 0;row < rows;row++) {
                float cloud = noise.multi(col*2,row,scales) - 0.3f;
                if (cloud < 0.3f) cloud = 0f;
                lightcells[col][row] = new ULightcell(this, col, row, cloud);
            }
        }
        log.trace("cell: " + cellWidth + "," + cellHeight + "  cols: " + columns + " rows: " + rows);
        leftEdge = centerColumn - (columns / 2);
        topEdge = centerRow - (rows / 2);
        rightEdge = leftEdge + columns;
        bottomEdge = topEdge + rows;
    }

    public int getWidthInCells() { return columns; }
    public int getHeightInCells() { return rows; }

    public void renderLights() {
        if (area == null) return;
        for (int i = 0; i< columns; i++) {
            for (int j = 0; j< rows; j++) {
                lightcells[i][j].wipe();
                lightcells[i][j].setSunBrightness(area.sunBrightnessAt(leftEdge +i, topEdge +j));
            }
        }
        renderSun();
        renderVisible();
        for (ULight light : area.lights()) {
            if (light.canTouch(this)) {
                projectLight(light);
            }
        }
        renderSeen();
    }

    void renderSun() {
        for (int col = 0; col < columns; col++) {
            for (int row = 0; row < rows; row++) {
                float sun = getSunBrightnessAt(col,row);
                if (sun < 0.1f) {
                    int litnear = 0;
                    for (int k = -1;k < 2;k++) {
                        for (int l = -1;l < 2;l++) {
                            if (getSunBrightnessAt(col + k, row + l) > 0.9f) {
                                litnear++;
                            }
                        }
                    }
                    if (litnear > 0) {
                        if (area.blocksLight(col + leftEdge, row + topEdge))
                            sun = 1f;
                        else
                            sun = 0.5f;
                    }
                }
                lightcells[col][row].setRenderedSun(sun);
            }
        }
    }
    float getSunBrightnessAt(int col, int row) {
        if (isValidCell(col, row))
            return lightcells[col][row].getSunBrightness();
        return 0f;
    }

    float cloudPatternAt(int cx, int cy) {
        while (cx >= getWidthInCells())
            cx = cx - getWidthInCells();
        while (cx < 0)
            cx = cx + getWidthInCells();
        while (cy >= getHeightInCells())
            cy = cy - getHeightInCells();
        while (cy < 0)
            cy = cy + getHeightInCells();
        return lightcells[cx][cy].cloudPattern();
    }

    void renderVisible() {
        for (int col = 0; col < columns; col++) {
            for (int row = 0; row < rows; row++) {
                setVisibilityAt(col, row, 0f);
            }
        }
        for (UActor p : visibilitySources)
            renderVisibleFor(p);

    }

    void renderSeen() {
        for (int col = 0; col < columns; col++) {
            for (int row = 0; row < rows; row++) {
                float vis = visibilityAt(col, row);
                if (vis > config.getVisibilityThreshold() && lightAt(col,row).grayscale() > config.getVisibilityThreshold())
                    area.setSeen(col + leftEdge, row + topEdge);
            }
        }
    }


    void renderVisibleFor(UActor actor) {
        for (int i=-1;i<2;i++) {
            for (int j=-1;j<2;j++) {
                int dx = (actor.areaX() - leftEdge) + i;
                int dy = (actor.areaY() - topEdge) + j;
                setVisibilityAt(dx, dy, 1.0f);
            }
        }
        projectVisibility(actor.areaX() - leftEdge, actor.areaY() - topEdge);
    }


    public float visibilityAt(int col, int row) {
        if (!config.isVisibilityEnable())
            return 1.0f;
        if (!isValidCell(col, row))
            return 0f;
        return lightcells[col][row].visibility;
    }

    void setVisibilityAt(int col, int row, float vis) {
        if (isValidCell(col, row)) {
            lightcells[col][row].setVisibility(vis);

        }
    }

    public void addVisibilitySource(UActor actor) {
        visibilitySources.add(actor);
    }
    public void removeVisibilitySource(UActor actor) {
        visibilitySources.remove(actor);
    }


    void projectVisibility(int ox, int oy) {
        projectLight(ox, oy, null, true);
    }
    void projectLight(ULight light) {
        projectLight(light.x - leftEdge, light.y - topEdge, light, false);
    }

    void projectLight(int ox, int oy, ULight light, boolean projectVisibility) {
        projectToCell(ox, oy, light, projectVisibility, 1f);
        boolean isAmbient = true;
        if (light == null) isAmbient = false;
        else if (light.type == ULight.POINT) isAmbient = false;
        if (projectVisibility || !isAmbient) {
            for (int octant = 0;octant < 8;octant++) {
                shadowLine.reset();
                boolean fullShadow = false;
                int row = 0;
                boolean inFrame = true;
                while (inFrame) {
                    row++;
                    if (!isValidCell(ox + transformOctantCol(row, 0, octant), oy + transformOctantRow(row, 0, octant)))
                        inFrame = false;
                    else {
                        boolean inRow = true;
                        for (int col = 0;col <= row;col++) {
                            int dy = oy + transformOctantRow(row, col, octant);
                            int dx = ox + transformOctantCol(row, col, octant);
                            if (!isValidCell(dx, dy))
                                inRow = false;
                            else {
                                if (fullShadow) {
                                    // projectToCell(dx, dy, light, projectVisibility, 0f);
                                } else {
                                    UShadow projection = new UShadow(0f, 0f);
                                    projection.projectTile(row, col);
                                    boolean visible = !shadowLine.isInShadow(projection);
                                    if (visible) {
                                        projectToCell(dx, dy, light, projectVisibility, 1f);
                                        if (area.blocksLight(dx + leftEdge, dy + topEdge)) {
                                            shadowLine.add(projection);
                                            fullShadow = shadowLine.isFullShadow();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (config.isSmoothLightCones()) {
                for (int col = 0;col < columns;col++) {
                    for (int row = 0;row < rows;row++) {
                        float v = visibilityAt(col, row);
                        if (v == 0f) {
                            int neigh = 0;
                            if (visibilityAt(col - 1, row) == 1f && !area.blocksLight(col + leftEdge - 1, row + topEdge))
                                neigh++;
                            if (visibilityAt(col + 1, row) == 1f && !area.blocksLight(col + leftEdge + 1, row + topEdge))
                                neigh++;
                            if (visibilityAt(col, row - 1) == 1f && !area.blocksLight(col + leftEdge, row + topEdge - 1))
                                neigh++;
                            if (visibilityAt(col, row + 1) == 1f && !area.blocksLight(col + leftEdge, row + topEdge + 1))
                                neigh++;
                            if (neigh > 2)
                                projectToCell(col, row, light, projectVisibility, 0.75f);
                            else if (neigh > 1)
                                projectToCell(col, row, light, projectVisibility, 0.6f);
                        }
                    }
                }
            }
        } else {
            projectAmbient(ox,oy,light);
        }
    }

    void projectAmbient(int x1, int y1, ULight light) {
        for (int ix=x1-1;ix<=x1+light.width;ix++) {
            for (int iy=y1-1;iy<=y1+light.height;iy++) {
                projectToCell(ix,iy,light,false,1f);
            }
        }
        float fall = 0.15f;
        float val = 0f;
        int w = light.width + 4;
        int h = light.height + 4;
        int sx1 = x1-2;
        int sy1 = y1-2;
        if (true) {
            for (int ix = sx1;ix < sx1 + w;ix++) {
                val = spreadAmbient(light, ix, sy1, 0, -1, fall);
                projectToCell(ix, sy1, light, false, val);
                projectToCell(ix-1, sy1, light, false, val);
                projectToCell(ix+1,sy1,light,false,val);
                val = spreadAmbient(light, ix, sy1 + h-1, 0, 1, fall);
                projectToCell(ix, sy1 + h-1, light, false, val);
                projectToCell(ix-1,sy1+h-1,light,false,val);
                projectToCell(ix+1,sy1+h-1,light,false,val);
            }
            for (int iy = sy1;iy < sy1 + h;iy++) {
                val = spreadAmbient(light, sx1, iy, -1, 0, fall);
                projectToCell(sx1, iy, light, false, val);
                projectToCell(sx1,iy-1,light,false,val);
                projectToCell(sx1,iy+1,light,false,val);
                val = spreadAmbient(light, sx1 + w-1, iy, 1, 0, fall);
                projectToCell(sx1 + w-1, iy, light, false, val);
                projectToCell(sx1+w-1,iy-1,light,false,val);
                projectToCell(sx1+w-1,iy+1,light,false,val);
            }
        }
    }

    float spreadAmbient(ULight light, int x, int y, int dx, int dy, float fall) {
            if (!area.blocksLight(leftEdge+x-dx,topEdge+y-dy))
                return fall * 3f;
            return 0f;
    }

    void projectToCell(int col, int row, ULight light, boolean projectVisibility, float intensity) {
        if (projectVisibility)
            setVisibilityAt(col, row, intensity);
        else if (intensity <= 0f)
            return;
        else {
            if (light.canTouch(col + leftEdge, row + topEdge)) {
                boolean blockedWallGlow = false;
                if (area.blocksLight(col+ leftEdge, row+ topEdge)) {
                    if (!light.lightsWall(col + leftEdge, row + topEdge, this))
                        blockedWallGlow = true;
                }
                if (!blockedWallGlow) {
                    receiveLight(col, row, light, intensity * light.intensityAtOffset((col + leftEdge) - light.x, (row + topEdge) - light.y));
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
    boolean isValidCell(int col, int row) {
        if (col >= 0 && row >= 0 && col < columns && row < rows)
            return true;
        return false;
    }

    public ULightcell lightcellAt(int col, int row) {
        if (isValidCell(col, row))
            return lightcells[col][row];
        return null;
    }

    void receiveLight(int col, int row, ULight source, float intensity) {
        if (isValidCell(col, row)) {
            lightcells[col][row].receiveLight(source, intensity);
        }
    }

    public UColor lightAt(int col, int row) {
        UColor total;
        if (!isValidCell(col,row))
            return UColor.BLACK;
        if (!config.isLightEnable() || !isLightEnable()) {
            total = UColor.WHITE;
        } else if (lightcells[col][row] == null) {
            log.warn("nonexistent lightcell at " + col + "," + row);
            total = UColor.BLACK;
        } else {
            total = lightcells[col][row].light(commander.frameCounter);
            // TODO : this is a dumb implementation for glow, or at least a dumb place for it
            for (int i = -1;i < 2;i++) {
                for (int j = -1;j < 2;j++) {
                    UTerrain t = area.terrainAt(col + leftEdge + i, row + topEdge + j);
                    if (t != null)
                        if (t.isGlow())
                            total.addLights(t.icon().getBgColor(), 0.5f);
                }
            }
        }
        return total;
    }

    public UColor fogAt(int col, int row) {
        if (!isValidCell(col,row))
            return UColor.CLEAR;
        return lightcells[col][row].fog(commander.frameCounter);
    }

    public UTerrain terrainAt(int localCol, int localRow) {
        return area.terrainAt(localCol + leftEdge, localRow + topEdge);
    }

    public ArrayList<UThing> thingsAt(int localCol, int localRow) {
        return area.thingsAt(localCol + leftEdge, localRow + topEdge);
    }
    public UActor actorAt(int localCol, int localRow) { return area.actorAt(localCol+ leftEdge,localRow+ topEdge); }
    public UCell cellAt(int localCol, int localRow) { return area.cellAt(localCol+ leftEdge, localRow+ topEdge); }

    @Override
    public void draw() {

        //renderLights();

        // Render Cells.
        for (int col=0; col<columns; col++) {
            for (int row=0; row<rows; row++) {
                drawCell(col, row);
            }
        }
        for (int col=0; col<columns; col++) {
            for (int row=0; row<rows; row++) {
                drawCellActor(col, row);
            }
        }
        for (int col=0; col<columns; col++) {
            for (int row=0; row<rows; row++) {
                drawCellParticle(col, row);
            }
        }
        int fogDistance = area.getFogDistance();
        int fogDistanceFull = area.getFogDistanceFull();
        for (int col = 0;col < columns;col++) {
            for (int row = 0;row < rows;row++) {
                drawCellFog(col, row, fogDistance, fogDistanceFull);
            }
        }

        drawCursor();
    }

    private void drawCell(int col, int row) {
        int cellw = config.getTileWidth();
        int cellh = config.getTileHeight();
        float vis = visibilityAt(col,row);
        UColor light = lightAt(col,row);
        UTerrain t = terrainAt(col,row);
        if (t != null) {
            t.icon().setAnimate(true);
            float tOpacity = vis;
            float tSaturation = 1f;
            if ((vis < config.getVisibilityThreshold()) && area.seenCell(col + leftEdge, row + topEdge)) {
                t.icon().setAnimate(false);
                tOpacity = config.getSeenOpacity();
                tSaturation = config.getSeenSaturation();
                if (config.isSeenLightGray())
                    light = UColor.GRAY;
            }
            UColor terrainLight = light;
            if (t.isGlow())
                terrainLight.set(1f,1f,1f);
            t.icon().draw(col * cellw, row * cellh, terrainLight, tOpacity, tSaturation);
            t.icon().setAnimate(true);
        } else {
            renderer.drawRect(col * cellw, row * cellh, cellw, cellh, config.getCameraBgColor());
        }

        if (vis > 0f && config.isAmbientOcclusion())
            drawCellAO(col, row);

        if (vis < config.getVisibilityThreshold())
            return;

        if (thingsAt(col,row) != null) {
            for (UThing thing : thingsAt(col,row)) {
                thing.icon().draw(col*cellw, row*cellh, light, vis, 1f);
            }
        }

    }

    private void drawCellActor(int col, int row) {
        UActor actor = actorAt(col, row);
        if (actor == null) return;
        float vis = visibilityAt(col,row);
        if (vis < config.getVisibilityThreshold()) return;

        UColor light = lightAt(col,row);
        actor.icon().draw(col*config.getTileWidth()+actor.getMoveAnimX(), row*config.getTileHeight()+actor.getMoveAnimY(), light, vis, 1f);
    }
    private void drawCellParticle(int col, int row) {
        UCell cell = cellAt(col,row);
        if (cell != null) {
            ArrayList<UParticle> particles = cellAt(col, row).getParticles();
            if (particles != null) {
                for (UParticle particle : particles) {
                    if (particle != null) {
                        UColor light = lightAt(col, row);
                        float vis = visibilityAt(col, row);
                        if (vis < config.getVisibilityThreshold()) return;
                        particle.draw(this, light, vis);
                    }
                }
            }
        }
    }
    private void drawCellFog(int col, int row, int distance, int distanceFull) {
        if (commander.player() == null) return;
        if (!config.isFog()) return;
        UColor light = lightAt(col,row);
        float vis = visibilityAt(col,row);
        if (vis > 0f) {
            int range = (int) UPath.dist(commander.player().areaX() - leftEdge, commander.player().areaY() - topEdge, col, row);
            if (range > distance) {
                float fog;
                if (range > distanceFull) {
                    fog = 1f;
                } else {
                    fog = (float) (range - distance) / (float) (distanceFull - distance);
                }
                UColor fogcolor = fogAt(col,row);
                fogcolor.setAlpha(fog * vis);
                renderer.drawRect(col * config.getTileWidth(), row * config.getTileHeight(), config.getTileWidth(), config.getTileHeight(), fogcolor);
            }
        }
    }

    void drawCellAO(int col, int row) {
        if (!area.canSeeThrough(col+leftEdge,row+topEdge)) return;
        if (col+leftEdge <=0 || row+topEdge <= 0 || col+leftEdge >= area.xsize-1 || row+topEdge >= area.ysize-1) return;

        boolean nn = !area.canSeeThrough(col+leftEdge,row+topEdge-1) || !area.terrainAt(col+leftEdge,row+topEdge-1).isPassable();
        boolean ns = !area.canSeeThrough(col+leftEdge,row+topEdge+1) || !area.terrainAt(col+leftEdge,row+topEdge+1).isPassable();
        boolean nw = !area.canSeeThrough(col+leftEdge-1,row+topEdge) || !area.terrainAt(col+leftEdge-1,row+topEdge).isPassable();
        boolean ne = !area.canSeeThrough(col+leftEdge+1,row+topEdge) || !area.terrainAt(col+leftEdge+1,row+topEdge).isPassable();
        int x = col * config.getTileWidth();
        int y = row * config.getTileHeight();
        int w = config.getTileWidth();
        int h = config.getTileHeight();
        int cw = (int)(((float)w/4f)*3f);
        int ch = (int)(((float)h/4f)*3f);
        if (nn) {
            renderer.drawRect(x, y, w, h / 4, UColor.SHADE);
            renderer.drawRect(x, y, w, 3, UColor.DARKSHADE);
            renderer.drawRect(x,y,w,1, UColor.DARKERSHADE);
        }
        if (!config.isAmbientOcclusionIso()) {
            if (ns) {
                renderer.drawRect(x, y + ch, w, h / 4, UColor.SHADE);
                renderer.drawRect(x, y + h - 3, w, 3, UColor.DARKSHADE);
                renderer.drawRect(x,y+h-1,w,1,UColor.DARKERSHADE);
            }
            if (nw) {
                renderer.drawRect(x, y, w / 4, h, UColor.SHADE);
                renderer.drawRect(x, y, 3, h, UColor.DARKSHADE);
                renderer.drawRect(x,y,1,h,UColor.DARKERSHADE);
            }
            if (ne) {
                renderer.drawRect(x + cw, y, w / 4, h, UColor.SHADE);
                renderer.drawRect(x + w - 3, y, 3, h, UColor.DARKSHADE);
                renderer.drawRect(x+w-1,y,1,h,UColor.DARKERSHADE);
            }
        }
    }

    public void animationTick() {
        updateCursor();
        float visThreshold;
        if (config.isVisibilityEnable())
            visThreshold = config.getVisibilityThreshold();
        else
            visThreshold = -1f;
        for (int col = leftEdge; col< rightEdge; col++) {
            for (int row = topEdge; row< bottomEdge; row++) {
                if (area.isValidXY(col,row) && lightcells[col- leftEdge][row- topEdge].visibility > visThreshold)
                    area.cellAt(col,row).animationTick();
            }
        }
    }

    void updateCursor() {
        if (commander.player() == null) return;
        int mouseCol = mouseX() / config.getTileWidth();
        int mouseRow = mouseY() / config.getTileHeight();
        if (mouseCol < 0 || mouseRow < 0 || mouseCol >= columns || mouseRow >= rows) {
            cursorX = -1;
            cursorY = -1;
            cursorPath = null;
        } else if (cursorX != mouseCol || cursorY != mouseRow) {
            cursorX = mouseCol;
            cursorY = mouseRow;
        }
        testmap.valueAt(0,0);
    }

    void drawCursor() {
        if (cursorX < 0) return;
        if (testmap == null) return;
        if (commander.modal() != null) return;
        renderer.drawRect(cursorX*config.getTileWidth(), cursorY*config.getTileHeight(), config.getTileWidth(), config.getTileHeight(), config.getHiliteColor());
        int linepos[] = new int[]{cursorX+leftEdge,cursorY+topEdge};
        float steps = testmap.valueAt(linepos[0],linepos[1]);
        while (steps > 0) {
            renderer.drawRect((linepos[0]-leftEdge)*config.getTileWidth(),(linepos[1]-topEdge)*config.getTileHeight(),config.getTileWidth(),config.getTileHeight(), config.getHiliteColor());
            linepos = testmap.stepDown(linepos);
            if (linepos == null) break;
            steps = testmap.valueAt(linepos[0],linepos[1]);
        }
        if (config.isTelemetry()) {
            for (int i = 0;i < columns;i++) {
                for (int j = 0;j < rows;j++) {
                    int mx = i + leftEdge;
                    int my = j + topEdge;
                    if (area.isValidXY(mx, my)) {
                        float v = testmap.valueAt(mx, my);
                        if (v > 0f)
                            renderer.drawString(i * config.getTileWidth(), j * config.getTileHeight(), config.getHiliteColor(), Integer.toString((int) v));
                    }
                }
            }
        }
    }

    public boolean isLightEnable() { return lightEnable; }
    public void setLightEnable(boolean b) { lightEnable = b; }
}
