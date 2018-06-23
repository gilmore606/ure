import java.awt.*;
import java.io.IOException;
import javax.swing.*;


public class ExampleGame {

    static UREArea area;
    static URECamera camera;
    static UREThing player;

    private static void makeWindow() {
        JFrame frame = new JFrame("Rogue");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        camera = new URECamera(new URERenderer(), 1000, 700 );
        camera.moveTo(area, 11,9);
        camera.renderImage();
        frame.setSize(1000, 700);
        frame.add(camera);
        frame.setLocationRelativeTo(null);
        //frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args)  {
        URETerrainCzar terrainCzar = new URETerrainCzar();
        area = new UREArea("samplemap.txt", terrainCzar);
        player = new UREThing("Player", '@', Color.WHITE, true);
        player.moveToCell(area, 11, 9);

        makeWindow();
    }
}
