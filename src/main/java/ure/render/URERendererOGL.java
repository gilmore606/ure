package ure.render;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import ure.UColor;
import ure.URECamera;
import ure.URECommander;
import ure.actors.UREActor;
import ure.terrain.URETerrain;
import ure.things.UREThing;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;


import java.awt.image.*;
import java.util.Iterator;

public class URERendererOGL implements URERenderer {

    //To store matrix data for uploading into OpenGLVille
    private FloatBuffer fb = BufferUtils.createFloatBuffer(16);
    private Matrix4f matrix = new Matrix4f();
    private Matrix4f dummyMatrix = new Matrix4f();

    private long window;

    // TODO: These should be customizable at a higher level
    private int screenWidth = 1400;
    private int screenHeight = 1000;

    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback   keyCallback;
    private GLFWFramebufferSizeCallback fbCallback;

    private int tris = 0;
    private int maxTris = 65536; // THIS CAN BE HIGHER IF NEEDED

    private float[] verts_pos = new float[3 * maxTris * 3];
    private float[] verts_col = new float[3 * maxTris * 4];
    private float[] verts_uv  = new float[3 * maxTris * 3];

    private int textureAtlas = -1;

    private static Font font;
    private int fontPadX = 3;
    private int fontPadY = 1;
    private int cellPadX = 0;
    private int cellPadY = 1;
    private int fontSize;
    private UColor UIframeColor;

    URECommander commander;

    private boolean rendering = false;

    public URERendererOGL(Font f){
        font = f;
        fontSize = font.getSize();
        UIframeColor = new UColor(1f, 1f, 0f);
    }

    // URERenderer methods

    @Override
    public boolean windowShouldClose() {
        return glfwWindowShouldClose(window);
    }

    @Override
    public void pollEvents() {
        glfwPollEvents();
    }

    @Override
    public void setCommander(URECommander cmdr) {
        commander = cmdr;
    }

    @Override
    public int cellWidth() {
        return fontSize + cellPadX;
    }

    @Override
    public int cellHeight() {
        return fontSize + cellPadY;
    }

    @Override
    public void initialize(){

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        // Configure our window
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 1);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, 0);
        //glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(screenWidth, screenHeight, "UREasonable example!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key,
                               int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    glfwSetWindowShouldClose(window, true);
                    System.out.println("OMG I SHOULD EXIT NOW!");
                }
                if(key < 256 && key >= 0 && (action == GLFW_PRESS || action == GLFW_REPEAT)){
                    commander.keyPressed((char)key);
                }
            }
        });
        glfwSetFramebufferSizeCallback(window,
                fbCallback = new GLFWFramebufferSizeCallback() {
                    @Override
                    public void invoke(long window, int w, int h) {
                        if (w > 0 && h > 0) {
                            resize(w, h);
                        }
                    }
                });

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (vidmode.width() - screenWidth) / 2, (vidmode.height() - screenHeight) / 2);

        glfwMakeContextCurrent(window);
        glfwSwapInterval(0);
        glfwShowWindow(window);

        resize(screenWidth, screenHeight);

        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        GL.createCapabilities();
        GLUtil.setupDebugMessageCallback();

        //Lets quickly blank the screen.
        glViewport(0, 0, screenWidth, screenHeight);
        glClearColor(0.03f, 0.05f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        //The 2d stuff we're doing doens't need depth, or culling of faces.
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);

        //Flush a blank image to the screen quickly.
        glfwSwapBuffers(window);

        //Lets create our glyph atlas
        /*try {
            font = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/Px437_Phoenix_BIOS-2y.ttf")).deriveFont(Font.PLAIN, 16);
        } catch (Exception e) {
            System.out.println("Failed to load font");
        }*/
        BufferedImage bitmap = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bitmap.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        g.setPaint(Color.WHITE);

        for(int c = 32; c < 256; c+=1){
            if(c == 127) continue;
            g.drawString(String.valueOf((char)c), (c % 16) * 32 + 8, (c / 16) * 32 - 10 + 32);
        }
        g.fillRect(0,0,32,32);

        //From: http://www.java-gaming.org/index.php?topic=25516.0
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getRGB(0, 0, bitmap.getWidth(), bitmap.getHeight(), pixels, 0, bitmap.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(bitmap.getWidth() * bitmap.getHeight() * 4);

        for(int y = 0; y < bitmap.getHeight(); y++){
            for(int x = 0; x < bitmap.getWidth(); x++){
                int pixel = pixels[y * bitmap.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >>  8) & 0xFF));     // Green component
                buffer.put((byte) (pixel & 0xFF));             // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF));     // Alpha component. Only for RGBA
            }
        }

        buffer.flip();

        textureAtlas = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureAtlas);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bitmap.getWidth(), bitmap.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        //glBindTexture(GL_TEXTURE_2D, 0);

    }

    @Override
    public void drawString(int x, int y, UColor col, String str){
        //TODO: HANDLE FONT CHANGES
        for(int i = 0; i < str.length(); i++){
            addQuad(x, y, cellWidth(), cellHeight(), col, str.charAt(i));
            x += 8;
        }
    }

    @Override
    public void drawCamera(URECamera camera) {

        camera.rendering = true;
        rendering = true;
        int cellw = cellWidth();
        int cellh = cellHeight();
        int camw = camera.getWidthInCells();
        int camh = camera.getHeightInCells();

        // Render Cells.
        for (int x=0;x<camw;x++) {
            for (int y=0;y<camh;y++) {
                renderCell(camera, x, y, cellw, cellh);
            }
        }

        camera.rendering = false;
        rendering = false;
    }

    @Override
    public void drawGlyph(char glyph, int destx, int desty, UColor tint, int offX, int offY) {
        //tint.r = 1.0f;
        addQuad(destx + offX, desty + offY, cellWidth(), cellHeight(), tint, glyph);
    }

    @Override
    public void drawGlyphOutline(char glyph, int destx, int desty, UColor tint, int offX, int offY) {
        //tint.r = 1.0f;
        for(int y = -1; y < 2; y += 1)
            for(int x = -1; x < 2; x += 1)
                if(x != 0 && y != 0)
                    addQuad(destx + offX + x, desty + offY + y, cellWidth(), cellHeight(), tint, glyph);
    }

    static boolean slowMethod = false;
    public void render(){

        glViewport(0, 0, screenWidth, screenHeight);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (tris == 0) return; //Nothing to draw

        glLoadIdentity();

        glMatrixMode(GL_PROJECTION);
        glLoadMatrixf(matrix.get(fb));

        glMatrixMode(GL_MODELVIEW);
        glLoadMatrixf(dummyMatrix.get(fb));

        glBindTexture(GL_TEXTURE_2D, textureAtlas);

        if(!slowMethod) {
            FloatBuffer v = BufferUtils.createFloatBuffer(tris * 3 * 3);
            FloatBuffer c = BufferUtils.createFloatBuffer(tris * 3 * 4);
            FloatBuffer u = BufferUtils.createFloatBuffer(tris * 3 * 2);

            v.put(verts_pos, 0, v.capacity());
            c.put(verts_col, 0, c.capacity());
            u.put(verts_uv, 0, u.capacity());

            v.flip();
            c.flip();
            u.flip();

            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
            GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

            GL11.glVertexPointer(3, GL_FLOAT, 0, v);
            GL11.glColorPointer(4, GL_FLOAT, 0, c);
            GL11.glTexCoordPointer(2, GL_FLOAT, 0, u);


            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, tris * 3);
        }else{
            glBegin(GL_TRIANGLES);
            int pp = 0;
            int pc = 0;
            int pu = 0;
            for(int t = 0; t < tris * 3; t++){
                GL11.glVertex3f(verts_pos[pp++], verts_pos[pp++], verts_pos[pp++]);
                GL11.glColor4f(verts_col[pc++], verts_col[pc++], verts_col[pc++], verts_col[pc++]);
                GL11.glTexCoord2f(verts_uv[pu++], verts_uv[pu++]);
            }
            glEnd();
        }
        glFinish();

        glfwSwapBuffers(window);

        tris = 0;
    }

    // internals

    private void resize(int width, int height){
        screenWidth = width;
        screenHeight = height;
        matrix.setOrtho2D(0, screenWidth, screenHeight, 0);
    }

    private void destroy(){
        glfwDestroyWindow(window);
        glfwTerminate();
        errorCallback.free();
    }

    @Override
    public void drawRect(int x, int y, int w, int h, UColor col){
        addQuad(x, y, w, h, col);
    }
    @Override
    public void drawRectBorder(int x, int y, int w, int h, int borderThickness, UColor bgColor, UColor borderColor){
        addQuad(x, y, w, h, borderColor);
        addQuad(x + borderThickness, y + borderThickness, w - borderThickness * 2, h - borderThickness * 2, bgColor);
    }


    private void addQuad(int x, int y, int w, int h, UColor col){
        addQuad(x, y, w, h, col, 0.0f, 0.0f, 32.f / 1024.f, 32.f / 1024.f);
    }

    private void addQuad(int x, int y, int w, int h, UColor col, char glyph){
        float u = (float)(glyph % 16) / 32.0f + 0.00390625f;
        float v = (float)(glyph / 16) / 32.0f + 0.0078125f;
        addQuad(x, y, w, h, col, u, v, (float)cellWidth() / 1024.f, (float)cellHeight() / 1024.f);
    }

    // I don't think we'll have any triangle data for awhile/ever, so I'm just gonna do quads.
    private void addQuad(int x, int y, int w, int h, UColor col, float u, float v, float uw, float vh){
        //   1---2
        //   | / |
        //   3---4
        if(tris + 2 >= maxTris) return; // Should probably dump a message here, we're all full up!

        // Current indices for our data;
        int ip = 3 * 3 * tris;
        int ic = 4 * 3 * tris;
        int iu = 2 * 3 * tris;


        //pos, tri0
        //v1
        verts_pos[ip++] = x;
        verts_pos[ip++] = y;
        verts_pos[ip++] = 0;
        //v2
        verts_pos[ip++] = x + w;
        verts_pos[ip++] = y;
        verts_pos[ip++] = 0;
        //v3
        verts_pos[ip++] = x;
        verts_pos[ip++] = y + h;
        verts_pos[ip++] = 0;
        //pos, tri1
        //v2
        verts_pos[ip++] = x + w;
        verts_pos[ip++] = y;
        verts_pos[ip++] = 0;
        //v4
        verts_pos[ip++] = x + w;
        verts_pos[ip++] = y + h;
        verts_pos[ip++] = 0;
        //v3
        verts_pos[ip++] = x;
        verts_pos[ip++] = y + h;
        verts_pos[ip++] = 0;

        //cols;
        int i;
        for(i = 0; i < 3 * 2; i++){
            verts_col[ic++] = col.r;
            verts_col[ic++] = col.g;
            verts_col[ic++] = col.b;
            verts_col[ic++] = 1.0f;
        }

        //UVs, tri 0
        //v1
        verts_uv[iu++] = u;
        verts_uv[iu++] = v;
        //v2
        verts_uv[iu++] = u + uw;
        verts_uv[iu++] = v;
        //v3
        verts_uv[iu++] = u;
        verts_uv[iu++] = v + vh;
        //UVs, tri 1
        //v2
        verts_uv[iu++] = u + uw;
        verts_uv[iu++] = v;
        //v4
        verts_uv[iu++] = u + uw;
        verts_uv[iu++] = v + vh;
        //v3
        verts_uv[iu++] = u;
        verts_uv[iu++] = v + vh;


        tris += 2;
    }


    private void renderCell(URECamera camera, int x, int y, int cellw, int cellh) {
        float vis = camera.visibilityAt(x,y);
        float visSeen = camera.getSeenOpacity();
        UColor light = camera.lightAt(x,y);
        URETerrain t = camera.terrainAt(x,y);
        if (t != null) {
            float tOpacity = vis;
            if ((vis < visSeen) && camera.area.seenCell(x + camera.x1, y + camera.y1))
                tOpacity = visSeen;
            UColor terrainLight = light;
            if (t.glow)
                terrainLight.set(1f,1f,1f);
            t.bgColorBuffer.set(t.bgColor.r, t.bgColor.g, t.bgColor.b);
            t.bgColorBuffer.illuminateWith(terrainLight, tOpacity);

            addQuad(x * cellw, y * cellh, cellw, cellh, t.bgColorBuffer);
            t.fgColorBuffer.set(t.fgColor.r, t.fgColor.g, t.fgColor.b);
            t.fgColorBuffer.illuminateWith(terrainLight, tOpacity);
            drawGlyph(t.glyph(x,y), x * cellw, y * cellh, t.fgColorBuffer, t.glyphOffsetX(), t.glyphOffsetY() + 2);
        }

        //TODO: Define this magic value somewhere?
        if (vis < 0.3f)
            return;
        Iterator<UREThing> things = camera.thingsAt(x,y);
        if (things != null) {
            while (things.hasNext()) {
                things.next().render(this, x * cellw, y * cellh, light, vis);
            }
        }
        UREActor actor = camera.actorAt(x,y);
        if (actor != null) {
            actor.render(this, x * cellw, y * cellh, light, vis);
        }
    }

}
