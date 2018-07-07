package ure.terrain;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

/**
 * Load and manage all the terrain types and dole them out as needed.
 */

public class URETerrainCzar {

    private  HashMap<Character,TerrainI> terrains;
    private  HashMap<String,TerrainI> terrainsByName;

    private Set<Class<? extends TerrainI>> terrainClasses;
    private ObjectMapper objectMapper = new ObjectMapper();
    private Reflections reflections = new Reflections("ure", new SubTypesScanner());

    public URETerrainCzar() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(TerrainI.class, new TerrainDeserializer());
        objectMapper.registerModule(module);
    }

    public void loadTerrains(String resourceName) {
        terrainClasses = reflections.getSubTypesOf(TerrainI.class);
        System.out.println("+++++++++ " + terrainClasses.size() + " TerrainI subclasses");
        for (Class<? extends TerrainI> terrainClass : terrainClasses) {
            System.out.println("**** " + terrainClass.toString());
        }
        System.out.println("+++++++++");
        terrains = new HashMap<>();
        terrainsByName = new HashMap<>();
        try {
            InputStream inputStream = getClass().getResourceAsStream("/terrain.json");
            TerrainI[] terrainObjs = objectMapper.readValue(inputStream, TerrainI[].class);
            for (TerrainI terrain : terrainObjs) {
                terrain.initialize();
                terrains.put(terrain.filechar, terrain);
                terrainsByName.put(terrain.name, terrain);
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public URETerrain getTerrainForFilechar(char thechar) {
        return (URETerrain)(terrains.get(thechar).getClone());
    }

    public URETerrain getTerrainByName(String name) {
        return (URETerrain)(getTerrainForFilechar(terrainsByName.get(name).filechar));
    }

    public class TerrainDeserializer extends JsonDeserializer<TerrainI> {

        @Override
        public TerrainI deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
            ObjectCodec codec = parser.getCodec();
            JsonNode node = codec.readTree(parser);
            JsonNode typeNode = node.get("type");
            String type = typeNode != null ? node.get("type").asText() : null;
            Class<? extends TerrainI> terrainClass = classForType(type);
            return (objectMapper.treeToValue(node, terrainClass));
        }

        private Class<? extends TerrainI> classForType(String type) {
            if (type == null || type.equals("")) {
                return Blank.class;
            }
            try {
                for (Class<? extends TerrainI> terrainClass : terrainClasses) {
                    Field typeField = terrainClass.getField("TYPE");
                    String typeValue = (String) typeField.get(null);
                    if (type.equals(typeValue)) {
                        return terrainClass;
                    }
                }
                throw new RuntimeException("No TerrainI class of type '" + type + "' was found in the classpath");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("All subclasses of TerrainI must define a static TYPE field", e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
