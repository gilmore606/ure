package ure.areas.gen.shapers;

import com.fasterxml.jackson.core.JsonParser;
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

public class ShaperDeserializer extends JsonDeserializer<Shaper> {

    private ObjectMapper objectMapper;

    private Reflections reflections = new Reflections("ure", new SubTypesScanner());
    private Set<Class<? extends Shaper>> shaperClasses = reflections.getSubTypesOf(Shaper.class);

    public ShaperDeserializer(ObjectMapper mapper) {
        objectMapper = mapper;
    }

    @Override
    public Shaper deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);
        JsonNode typeNode = node.get("type");
        String type = (typeNode != null && !typeNode.isNull()) ? node.get("type").asText() : null;
        Class<? extends Shaper> shaperClass = classForType(type);
        return objectMapper.treeToValue(node, shaperClass);
    }

    private Class<? extends Shaper> classForType(String type) {
        if (type == null || type.equals("")) {
            throw new RuntimeException("shaper JSON must specify a valid type in their type field that matches the TYPE field of a Shaper subclass");
        }
        try {
            for (Class<? extends Shaper> shaperClass : shaperClasses) {
                Field typeField = shaperClass.getField("TYPE");
                String typeValue = (String) typeField.get(null);
                if (type.equals(typeValue)) {
                    return shaperClass;
                }
            }
            throw new RuntimeException("No Shaper class of type '" + type + "' was found in the classpath");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("All subclasses of Shaper must define a static TYPE field", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
