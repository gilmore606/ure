import java.awt.*;
import java.io.IOException;
import javax.swing.*;


public class ExampleGame {

    static UREArea area;
    static URECamera camera;

    private static void makeWindow() {
        JFrame frame = new JFrame("Rogue");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        camera = new URECamera(new URERenderer(), 400, 270 );
        camera.moveTo(area, 11,9);
        camera.renderImage();
        frame.setSize(400, 270);
        frame.add(camera);
        frame.setLocationRelativeTo(null);
        //frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args)  {
        URETerrainCzar terrainCzar = new URETerrainCzar();
        area = new UREArea("samplemap.txt", terrainCzar);
        makeWindow();
    }
}
