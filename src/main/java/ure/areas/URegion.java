package ure.areas;

import ure.actors.UActorCzar;
import ure.sys.Injector;
import ure.sys.UCommander;
import ure.terrain.UTerrainCzar;
import ure.things.UThingCzar;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class URegion {

    @Inject
    UCommander commander;
    @Inject
    UTerrainCzar terrainCzar;
    @Inject
    UThingCzar thingCzar;
    @Inject
    UActorCzar actorCzar;

    public String id;
    public String name;
    ULandscaper[] landscapers;
    String[] tags;
    int xsize, ysize;
    int maxlevel;
    String inwardExitType, outwardExitType;

    Random random;

    ArrayList<Link> links;

    class Link {
        int onLevel;
        String exitType;
        String label;

        public Link(int _onLevel, String _exitType, String _label) {
            onLevel = _onLevel;
            exitType = _exitType;
            label = _label;
        }
    }

    public URegion(String _id, String _name, ULandscaper[] _landscapers, String[] _tags, int _xsize, int _ysize,
                   int _maxlevel, String _inwardExitType, String _outwardExitType) {
        id = _id;
        name = _name;
        landscapers = _landscapers;
        tags = _tags;
        xsize = _xsize;
        ysize = _ysize;
        maxlevel = _maxlevel;
        inwardExitType = _inwardExitType;
        outwardExitType = _outwardExitType;
        random = new Random();
        links = new ArrayList<>();
        Injector.getAppComponent().inject(this);
    }

    public void addLink(int _onlevel, String _exitType, String _label) {
        links.add(new Link(_onlevel, _exitType, _label));
    }

    public UArea makeArea(int level, String label) {
        System.out.println("REGION " + id + " : making area " + Integer.toString(level));
        ULandscaper scaper = getLandscaperForLevel(level);
        UArea area = new UArea(xsize, ysize, terrainCzar, scaper.floorterrain);
        area.label = label;
        scaper.buildArea(area);

        HashMap<String,String> stairs = new HashMap<>();
        for (Link link : links)
            if (link.onLevel == level)
                stairs.put(link.label, link.exitType);
        if (level > 1) {
            stairs.put(id + " " + Integer.toString(level-1), outwardExitType);
        }
        if (level < maxlevel) {
            stairs.put(id + " " + Integer.toString(level+1), inwardExitType);
        }
        makeStairs(area, scaper, stairs);

        return area;
    }

    public ULandscaper getLandscaperForLevel(int level) {
        ULandscaper scaper = landscapers[random.nextInt(landscapers.length)];
        return scaper;
    }

    public void makeStairs(UArea area, ULandscaper scaper, HashMap<String,String> links) {
        for (String label : links.keySet()) {
            String exitType = links.get(label);
            scaper.placeStairs(area, exitType, label);
        }
    }

    public String describeLabel(String label, String labelname, int labeldata) {
        return name + " " + Integer.toString(labeldata*25) + "ft";
    }
}
