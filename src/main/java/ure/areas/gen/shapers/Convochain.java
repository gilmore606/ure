package ure.areas.gen.shapers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ure.areas.UArea;
import ure.areas.gen.Layer;
import ure.areas.gen.Shape;
import ure.areas.gen.UVault;
import ure.areas.gen.UVaultSet;

import java.util.HashMap;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

public class Convochain extends Shaper {

    private Log log = LogFactory.getLog(Chambers.class);

    class Pattern {
        public boolean[][] data;

        private void Set(BiPredicate<Integer, Integer> f) {
            for (int j = 0; j < data.length; j++) for (int i = 0; i < data.length; i++) data[i][j] = f.test(i, j);
        }
        public Pattern(int size, BiPredicate<Integer, Integer> f) {
            data = new boolean[size][size];
            Set(f);
        }
        public Pattern(boolean[][] field, int x, int y, int size) {
            this(size, (i, j) -> false);
            Set((i, j) ->
                    field[(x + i + field.length) % field.length]
                            [(y + j + field[0].length) % field[0].length]);
        }
        public Pattern getRotated() {
            return new Pattern(data.length, (x, y) -> data[data.length - 1 - y][x]);
        }
        public Pattern getReflected() {
            return new Pattern(data.length, (x, y) -> data[data.length - 1 - x][y]);
        }
        public int getIndex() {
            int result = 0;
            for (boolean[] row : data)
                for (boolean datum : row) {
                    result <<= 1;
                    result += datum ? 1 : 0;
                }
            return result;
        }
    }

    @JsonIgnore
    public double temperature;
    public final double DEFAULT_WEIGHT = 0.01;
    @JsonIgnore
    private int N;

    @JsonIgnore
    private HashMap<Integer, Double> weights;
    @JsonIgnore
    protected UVaultSet seedVault;

    public Convochain(int xsize, int ysize) {
        super(xsize,ysize);
        name = "Convochain";
    }

    @Override
    public void setupParams() {

        addParamI("sampletype", 0, 0, 5);
        addParamI("N", 2, 3, 6);
        addParamF("temperature", 0f, 1.2f, 4f);
        addParamI("iterations",1,1,10);
    }

    @Override
    public void build(Layer previousLayer, UArea area) {
        if (seedVault == null)
            seedVault = commander.cartographer.loadVaultSet("convochain");
        UVault seed = seedVault.vaultAt(getParamI("sampletype"));
        boolean[][] sample = new boolean[seed.cols][seed.rows];
        for (int i=0;i<seed.cols;i++) {
            for (int j=0;j<seed.rows;j++) {
                String t = seed.terrainAt(i,j);
                if (t == null)
                    sample[i][j] = false;
                else if (t.equals("null"))
                    sample[i][j] = false;
                else
                    sample[i][j] = true;
            }
        }
        buildConvochain(sample, getParamI("N"),(double)getParamF("temperature"),getParamI("iterations"));
    }

    public void buildConvochain(boolean[][] sample, int N, double temperature, int iterations) {
        clear();
        this.N = N;
        this.temperature = temperature;
        this.weights = new HashMap<>();

        for (int y = 0; y < sample[0].length; y++)
            for (int x = 0; x < sample.length; x++) {
                Pattern[] p = new Pattern[8];
                p[0] = new Pattern(sample, x, y, N);
                p[1] = p[0].getRotated();
                p[2] = p[1].getRotated();
                p[3] = p[2].getRotated();
                p[4] = p[0].getReflected();
                p[5] = p[1].getReflected();
                p[6] = p[2].getReflected();
                p[7] = p[3].getReflected();
                for (int k = 0; k < 8; k++)
                    weights.put(p[k].getIndex(), weights.getOrDefault(p[k].getIndex(), 0.) + 1.);
            }

        weights.replaceAll((pattern, weight) -> {
            if (weight <= 0.)
                return DEFAULT_WEIGHT;
            else
                return weight;
        });

        for (int y = 0; y < ysize; y++) {
            for (int x = 0;x < xsize;x++)
                write(x, y, random.nextBoolean());
        }

        for (int i=0;i<iterations;i++) {
            iterate();
        }
    }

    void iterate() {
        for (int k = 0; k < xsize * ysize; k++)
            metropolis(random.i(xsize), random.i(ysize));
    }
    void metropolis(int i, int j) {
        double p = energyExp(i, j);
        write(i,j,!value(i,j));
        double q = energyExp(i, j);

        if (Math.pow(q / p, 1.0 / temperature) < random.nextDouble())
            write(i,j,!value(i,j));
    }
    double energyExp(int i, int j) {
        double value = 1.0;
        for (int y = j - N + 1; y <= j + N - 1; y++)
            for (int x = i - N + 1; x <= i + N - 1; x++)
                value *= weights.getOrDefault(new Pattern(this.cells, x, y, N).getIndex(), DEFAULT_WEIGHT);
        return value;
    }
}
