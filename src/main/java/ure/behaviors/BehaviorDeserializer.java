package ure.behaviors;

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

public class BehaviorDeserializer extends JsonDeserializer<UBehavior> {

    private ObjectMapper objectMapper;

    private Reflections reflections = new Reflections("ure", new SubTypesScanner());
    private Set<Class<? extends UBehavior>> behaviorClasses = reflections.getSubTypesOf(UBehavior.class);

    public BehaviorDeserializer(ObjectMapper mapper) { objectMapper = mapper; }

    @Override
    public UBehavior deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);
        JsonNode typeNode = node.get("type");
        String type = (typeNode != null && !typeNode.isNull()) ? node.get("type").asText() : null;
        Class<? extends UBehavior> behaviorClass = classForType(type);
        return objectMapper.treeToValue(node, behaviorClass);
    }

    public Class<? extends UBehavior> classForType(String type) {
        if (type == null || type.equals("")) {
            throw new RuntimeException("UBehavior JSON must specify a valid type field that matches a UBehavior subclass");
        }
        try {
            for (Class<? extends UBehavior> behaviorClass : behaviorClasses) {
                Field typeField = behaviorClass.getField("TYPE");
                String typeValue = (String) typeField.get(null);
                if (type.equals(typeValue)) {
                    return behaviorClass;
                }
            }
            throw new RuntimeException("No UBehavior class of type '" + type + " was found in classpath");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("All subclasses of UBehavior must define a static TYPE field", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
