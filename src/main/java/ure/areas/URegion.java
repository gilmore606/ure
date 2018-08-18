package ure.areas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.actors.UActorCzar;
import ure.math.URandom;
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
    @JsonIgnore
    UCommander commander;

    @Inject
    @JsonIgnore
    UTerrainCzar terrainCzar;

    @Inject
    @JsonIgnore
    UThingCzar thingCzar;

    @Inject
    @JsonIgnore
    UActorCzar actorCzar;

    @Inject
    @JsonIgnore
    URandom random;

    protected String id;
    protected String name;
    protected ULandscaper[] landscapers;
    protected String[] tags;
    protected int xsize;
    protected int ysize;
    protected int maxlevel;
    protected String inwardExitType;
    protected String outwardExitType;
    protected String defaultBGM;

    protected ArrayList<Link> links;

    static class Link {
        public int onLevel;
        public String exitType;
        public String label;

        public Link() {}

        public Link(int _onLevel, String _exitType, String _label) {
            onLevel = _onLevel;
            exitType = _exitType;
            label = _label;
        }
    }

    public URegion() {
        Injector.getAppComponent().inject(this);
    }

    public URegion(String _id, String _name, ULandscaper[] _landscapers, String[] _tags, int _xsize, int _ysize,
                   int _maxlevel, String _inwardExitType, String _outwardExitType, String _defaultBGM) {
        this();
        setId(_id);
        setName(_name);
        setLandscapers(_landscapers);
        setTags(_tags);
        setXsize(_xsize);
        setYsize(_ysize);
        setMaxlevel(_maxlevel);
        setInwardExitType(_inwardExitType);
        setOutwardExitType(_outwardExitType);
        setDefaultBGM(_defaultBGM);
        setLinks(new ArrayList<>());
    }

    public void addLink(int _onlevel, String _exitType, String _label) {
        getLinks().add(new Link(_onlevel, _exitType, _label));
    }

    public UArea makeArea(int level, String label) {
        System.out.println("REGION " + getId() + " : making area " + Integer.toString(level));
        ULandscaper scaper = getLandscaperForLevel(level);
        UArea area = new UArea(getXsize(), getYsize(), scaper.floorterrain);
        area.label = label;
        scaper.buildArea(area, level, getTags());

        HashMap<String,String> stairs = new HashMap<>();
        for (Link link : getLinks())
            if (link.onLevel == level)
                stairs.put(link.label, link.exitType);
        if (level > 1) {
            String outExit = getOutwardExitType();
            if (outExit != null)
                stairs.put(getId() + " " + Integer.toString(level-1), outExit);
        }
        if (level < getMaxlevel()) {
            String inExit = getInwardExitType();
            if (inExit != null)
                stairs.put(getId() + " " + Integer.toString(level+1), inExit);
        }
        makeStairs(area, scaper, stairs);
        area.setBackgroundMusic(getBGM(level));
        return area;
    }

    public String getBGM(int level) {
        return defaultBGM;
    }

    public ULandscaper getLandscaperForLevel(int level) {
        ULandscaper scaper = getLandscapers()[random.i(getLandscapers().length)];
        return scaper;
    }

    public void makeStairs(UArea area, ULandscaper scaper, HashMap<String,String> links) {
        for (String label : links.keySet()) {
            String exitType = links.get(label);
            scaper.placeStairs(area, exitType, label);
        }
    }

    public String describeLabel(String label, String labelname, int labeldata) {
        return getName() + " " + Integer.toString(labeldata*25) + "ft";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ULandscaper[] getLandscapers() {
        return landscapers;
    }

    public void setLandscapers(ULandscaper[] landscapers) {
        this.landscapers = landscapers;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public int getXsize() {
        return xsize;
    }

    public void setXsize(int xsize) {
        this.xsize = xsize;
    }

    public int getYsize() {
        return ysize;
    }

    public void setYsize(int ysize) {
        this.ysize = ysize;
    }

    public int getMaxlevel() {
        return maxlevel;
    }

    public void setMaxlevel(int maxlevel) {
        this.maxlevel = maxlevel;
    }

    public String getInwardExitType() {
        return inwardExitType;
    }

    public void setInwardExitType(String inwardExitType) {
        this.inwardExitType = inwardExitType;
    }

    public String getOutwardExitType() {
        return outwardExitType;
    }

    public void setOutwardExitType(String outwardExitType) {
        this.outwardExitType = outwardExitType;
    }

    public ArrayList<Link> getLinks() {
        return links;
    }

    public void setLinks(ArrayList<Link> links) {
        this.links = links;
    }

    public void setDefaultBGM(String b) { defaultBGM = b; }
    public String getDefaultBGM() { return defaultBGM; }

}
