package ure.terrain;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ure.sys.Injector;
import ure.sys.ResourceManager;
import ure.sys.UCommander;

import javax.inject.Inject;

/**
 * Load and manage all the terrain types and dole them out as needed.
 *
 * Generally, your game will make one of these and use it for all terrain.  It loads terrain definitions
 * from terrain.json in your resources folder by default.  This json can specify any public property of
 * your terrain subclasses, including anything you add to a TerrainDeco.
 */

public class UTerrainCzar {

    private  HashMap<String,UTerrain> terrainsByName;

    @Inject
    ObjectMapper objectMapper;
    @Inject
    UCommander commander;
    @Inject
    ResourceManager resourceManager;

    private Log log = LogFactory.getLog(UTerrainCzar.class);

    public UTerrainCzar(String jsonfilename) {
        this();
        loadTerrains();
    }

    public UTerrainCzar() {
        Injector.getAppComponent().inject(this);
    }

    /**
     * Load terrain definitions from a json file and create sample instances.
     *
     * This is done on creation, but you can rerun it to load a new terrain set.
     *
     */
    public void loadTerrains() {
        terrainsByName = new HashMap<>();
        List<String> files = resourceManager.getResourceFiles("/terrain");
        for (String resourceName : files) {
            if (resourceName.endsWith(".json")) {
                log.debug("loading " + resourceName);
                try {
                    InputStream inputStream = resourceManager.getResourceAsStream("/terrain/" + resourceName);
                    UTerrain[] terrainObjs = objectMapper.readValue(inputStream, UTerrain[].class);
                    for (UTerrain terrain : terrainObjs) {
                        terrain.initializeAsTemplate();
                        terrainsByName.put(terrain.getName(), terrain);
                    }
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        }
    }

    /**
     * Get a new instance of a named terrain type.
     *
     * @param name
     * @return
     */
    public UTerrain getTerrainByName(String name) {
        UTerrain template = terrainsByName.get(name);
        UTerrain clone = template.makeClone();
        clone.initializeAsCloneFrom(template);
        clone.setID(commander.generateNewID(clone));
        return clone;
    }

    public ArrayList<UTerrain> getAllTerrainTemplates() {
        ArrayList<UTerrain> terrains = new ArrayList<>();
        for (String key : terrainsByName.keySet())
            terrains.add(terrainsByName.get(key));
        return terrains;
    }

    public Set<String> getAllTerrains() {
        return terrainsByName.keySet();
    }
}
