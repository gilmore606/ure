package ure;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

/**
 * Load and manage all the terrain types and dole them out as needed.
 */

public class URETerrainCzar {
    private  HashMap<Character,URETerrain> terrains;

    public URETerrainCzar() {
        terrains = new HashMap<>();
        try {
            InputStream inputStream = getClass().getResourceAsStream("/terrain.json");
            ObjectMapper objectMapper = new ObjectMapper();
            URETerrain[] terrainObjs = objectMapper.readValue(inputStream, URETerrain[].class);
            for (URETerrain terrain : terrainObjs) {
                terrain.initialize();
                terrains.put(terrain.filechar, terrain);
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public URETerrain getTerrainForFilechar(char thechar) {
        return terrains.get(thechar);
    }
}
