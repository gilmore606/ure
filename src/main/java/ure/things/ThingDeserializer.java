package ure.things;

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

public class ThingDeserializer extends JsonDeserializer<ThingI> {

    private Reflections reflections = new Reflections("ure", new SubTypesScanner());
    private Set<Class<? extends ThingI>> thingClasses = reflections.getSubTypesOf(ThingI.class);
    private ObjectMapper objectMapper;

    public ThingDeserializer(ObjectMapper mapper) {
        objectMapper = mapper;
    }

    @Override
    public ThingI deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);
        JsonNode typeNode = node.get("type");
        String type = typeNode != null ? node.get("type").asText() : null;
        Class<? extends ThingI> thingClass = classForType(type);
        return objectMapper.treeToValue(node, thingClass);
    }

    private Class<? extends ThingI> classForType(String type) {
        if (type == null || type.equals("")) {
            return Blank.class;
        }
        try {
            for (Class<? extends ThingI> thingClass : thingClasses) {
                Field typeField = thingClass.getField("TYPE");
                String typeValue = (String) typeField.get(null);
                if (type.equals(typeValue)) {
                    return thingClass;
                }
            }
            throw new RuntimeException("No ThingI class of type '" + type + "' was found in the classpath");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("All sublcasses of ThingI must define a static TYPE field", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
