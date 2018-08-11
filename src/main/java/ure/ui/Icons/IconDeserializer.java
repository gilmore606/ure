package ure.ui.Icons;


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

public class IconDeserializer extends JsonDeserializer<Icon> {

    private Reflections reflections = new Reflections("ure", new SubTypesScanner());
    private Set<Class<? extends Icon>> iconClasses = reflections.getSubTypesOf(Icon.class);
    private ObjectMapper objectMapper;

    public IconDeserializer(ObjectMapper mapper) {
        objectMapper = mapper;
    }

    @Override
    public Icon deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);
        JsonNode typeNode = node.get("type");
        String type = (typeNode != null && !typeNode.isNull()) ? node.get("type").asText() : null;
        Class<? extends Icon> iconClass = classForType(type);
        Icon i = objectMapper.treeToValue(node, iconClass);
        return i;
    }

    public Class<? extends Icon> classForType(String type) {
        if (type == null || type.equals("")) {
            return Blank.class;
        }
        try {
            for (Class<? extends Icon> iconClass : iconClasses) {
                Field typeField = iconClass.getField("TYPE");
                String typeValue = (String) typeField.get(null);
                if (type.equals(typeValue)) {
                    return iconClass;
                }
            }
            throw new RuntimeException("No Icon class of type '" + type + "' was found in the classpath");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("All sublcasses of Icon must define a static TYPE field", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
