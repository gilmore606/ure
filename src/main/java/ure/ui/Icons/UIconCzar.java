package ure.ui.Icons;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ure.sys.Injector;
import ure.sys.ResourceManager;
import ure.sys.UCommander;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class UIconCzar {

    @Inject
    ObjectMapper objectMapper;
    @Inject
    UCommander commander;
    @Inject
    ResourceManager resourceManager;

    private HashMap<String,Icon> iconsByName;

    private Log log = LogFactory.getLog(UIconCzar.class);

    public UIconCzar() {
        Injector.getAppComponent().inject(this);
    }

    public void loadIcons() {
        iconsByName = new HashMap<>();
        for (String resource : resourceManager.getResourceFiles("/icons")) {
            if (resource.endsWith(".json")) {
                try {
                    InputStream inputStream = resourceManager.getResourceAsStream("/icons/" + resource);
                    Icon[] iconObjs = objectMapper.readValue(inputStream, Icon[].class);
                    for (Icon icon : iconObjs) {
                        iconsByName.put(icon.getName(), icon);
                        log.debug("loaded " + icon.getName());
                    }
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        }
    }

    /**
     * Pulling out these templates is probably something only GlyphEd should be doing.
     */
    public Icon getTemplateByName(String name) {
        return iconsByName.get(name);
    }

    public Icon getIconByName(String name) {
        Icon template = iconsByName.get(name);
        if (template != null) {
            Icon clone = template.makeClone();
            clone.initialize();
            return clone;
        }
        return null;
    }

    public void replaceTemplate(String name, Icon template) {
        iconsByName.put(name, template);
    }
}
