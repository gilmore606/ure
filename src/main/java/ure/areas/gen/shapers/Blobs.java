package ure.areas.gen.shapers;

import ure.areas.UArea;
import ure.areas.gen.Layer;
import ure.areas.gen.Shape;

public class Blobs extends Shaper {

    public static final String TYPE = "Blobs";

    public Blobs() { super(TYPE); }

    @Override
    public void setupParams() {
        addParamI("blobsMin", 1, 1, 20);
        addParamI("blobsMax", 1, 3, 20);
        addParamI("sizeMin", 3, 6, 30);
        addParamI("sizeMax", 3, 12, 30);
        addParamF("rot", 0f, 0.2f, 0.5f);
        addParamI("erode", 0, 2, 5);
    }

    @Override
    public void build(Layer previousLayer, UArea area) {
        buildBlobs(getParamI("blobsMin"),getParamI("blobsMax"),getParamI("sizeMin"),getParamI("sizeMax"),getParamF("rot"),getParamI("erode"));
    }

    public void buildBlobs(int blobsMin, int blobsMax, int sizeMin, int sizeMax, float rot, int erode) {
        clear();
        int numBlobs = random.i(blobsMin,blobsMax);
        for (int i=0;i<numBlobs;i++) {
            int xsize = random.i(sizeMin,sizeMax);
            int ysize = random.i(sizeMin,sizeMax);
            int xpos = random.i(this.xsize-xsize);
            int ypos = random.i(this.ysize-ysize);
            Shape blob = shapeOval(xsize,ysize);
            blob.erode(rot,erode);
            maskWith(blob, MASK_OR, xpos, ypos);
        }
    }

    public Shape shapeOval(int xsize, int ysize) {
        Shape mask = new Shape(xsize, ysize);
        int ox = xsize/2; int oy = ysize/2;
        int width = ox; int height = oy;
        int hh = height * height;
        int ww = width * width;
        int hhww = hh * ww;
        int x0 = width;
        int dx = 0;
        for (int x=-width;x<=width;x++)
            mask.set(ox+x,oy);
        for (int y=1;y<=height;y++) {
            int x1=x0-(dx-1);
            for ( ;x1>0;x1--) {
                if (x1 * x1 * hh + y * y * ww <= hhww)
                    break;
            }
            dx = x0-x1;
            x0 = x1;
            for (int x=-x0;x<=x0;x++) {
                mask.set(ox+x,oy-y);
                mask.set(ox+x,oy+y);
            }
        }
        return mask;
    }
}
