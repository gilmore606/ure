package ure.ui.particles;

import ure.math.UColor;

public class ParticleBlood  extends UParticle {

    public static String frames_NW = ",..``    ";
    public static String frames_N  = "vii```   ";
    public static String frames_NE = ",,''.    ";
    public static String frames_E  = "=-~..    ";
    public static String frames_SE = "``..     ";
    public static String frames_S  = "^i,..    ";
    public static String frames_SW = "''..     ";
    public static String frames_W = "=-~`..    ";
    public ParticleBlood(int _x, int _y, int xdir, int ydir, UColor fgColor, float intensity) {
        super(_x + xdir,_y + ydir,6,fgColor,intensity,true,0,0,0,0);
        if (xdir == -1 && ydir == -1)
            glyphFrames = frames_NW;
        else if (xdir == -1 && ydir == 0)
            glyphFrames = frames_W;
        else if (xdir == -1 && ydir == 1)
            glyphFrames = frames_SW;
        else if (xdir == 0 && ydir == 1)
            glyphFrames = frames_S;
        else if (xdir == 1 && ydir == 1)
            glyphFrames = frames_SE;
        else if (xdir == 1 && ydir == 0)
            glyphFrames = frames_E;
        else if (xdir == 1 && ydir == -1)
            glyphFrames = frames_NE;
        else
            glyphFrames = frames_N;
    }
}
