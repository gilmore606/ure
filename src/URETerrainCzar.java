/**
 * Load and manage all the terrain types and dole them out as needed.
 */
import java.io.IOException;
import java.util.HashMap;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

public class URETerrainCzar {
    private  HashMap<Character,URETerrain> terrains;

    public URETerrainCzar() {
        terrains = new HashMap<Character,URETerrain>();
        try {
            byte[] jsonData = Files.readAllBytes(Paths.get("terrain.json"));
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode terrainsNode = rootNode.path("terrains");
            Iterator<JsonNode> elements = terrainsNode.elements();
            while (elements.hasNext()) {
                JsonNode terrainNode = elements.next();
                URETerrain terrain = objectMapper.treeToValue(terrainNode, URETerrain.class);
                terrain.initColors();
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
