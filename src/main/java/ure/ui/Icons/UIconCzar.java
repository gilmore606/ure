package ure.ui.Icons;

import com.fasterxml.jackson.databind.ObjectMapper;
import ure.sys.Injector;
import ure.sys.UCommander;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class UIconCzar {

    @Inject
    ObjectMapper objectMapper;
    @Inject
    UCommander commander;

    private HashMap<String,Icon> iconsByName;

    public UIconCzar() {
        Injector.getAppComponent().inject(this);
    }

    public void loadIcons() {
        iconsByName = new HashMap<>();
        File jsonDir = new File(commander.config.getResourcePath() + "icons/");
        ArrayList<File> files = new ArrayList<File>(Arrays.asList(jsonDir.listFiles()));
        for (File resourceFile : files) {
            String resourceName = resourceFile.getName();
            if (resourceName.endsWith(".json")) {
                try {
                    InputStream inputStream = getClass().getResourceAsStream("/icons/" + resourceName);
                    Icon[] iconObjs = objectMapper.readValue(inputStream, Icon[].class);
                    for (Icon icon : iconObjs) {
                        iconsByName.put(icon.getName(), icon);
                        System.out.println("ICONCZAR ICON: loaded " + icon.getName());
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
