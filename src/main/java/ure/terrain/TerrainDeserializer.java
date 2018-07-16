package ure.terrain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Set;

public class TerrainDeserializer extends JsonDeserializer<UTerrain> {

    ObjectMapper objectMapper;

    private Reflections reflections = new Reflections("ure", new SubTypesScanner());
    private Set<Class<? extends TerrainI>> terrainClasses = reflections.getSubTypesOf(TerrainI.class);

    public TerrainDeserializer(ObjectMapper mapper) {
        objectMapper = mapper;
    }

    @Override
    public TerrainI deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);
        JsonNode typeNode = node.get("type");
        String type = (typeNode != null && !typeNode.isNull()) ? node.get("type").asText() : null;
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
