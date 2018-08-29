package ure.areas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class UVaultSet {

    public String filename;
    public UVault[] vaults;
    public String[] tags;
    @JsonIgnore
    ObjectMapper objectMapper;

    public UVaultSet() {
        vaults = null;
    }

    public void setVaults(UVault[] _vaults) {
        vaults = _vaults;
    }
    public UVault[] getVaults() { return vaults; }
    public void setTags(String[] _tags) { tags = _tags; }
    public String[] getTags() { return tags; }
    public void setFilename(String _filename) { filename = _filename; }
    public String getFilename() { return filename; }

    public int size() { return vaults.length; }
    public UVault vaultAt(int i) {
        return vaults[i];
    }
    public void putVault(int i, UVault vault) {
        if (vaults == null) {
            vaults = new UVault[1];
            vaults[0] = vault;
        } else if (i >= vaults.length) {
            UVault[] newVaults = new UVault[vaults.length + 1];
            for (int n=0;n<vaults.length;n++) {
                newVaults[n] = vaults[n];
            }
            newVaults[newVaults.length - 1] = vault;
            vaults = newVaults;
        } else {
            vaults[i] = vault;
        }
    }
    public void setObjectMapper(ObjectMapper om) { objectMapper = om; }

    public void initialize() {
        vaults = null;
        addVault();
    }

    public void addVault() {
        UVault vault = new UVault();
        vault.initialize();
        if (vaults == null)
            putVault(0,vault);
        else
            putVault(vaults.length, vault);
    }

    public void persist(String absoluteFilepath) {
        File file = new File(absoluteFilepath);
        try (
                FileOutputStream stream = new FileOutputStream(file);
                //GZIPOutputStream gzip = new GZIPOutputStream(stream)
        ) {
            JsonFactory jfactory = new JsonFactory();
            JsonGenerator jGenerator = jfactory
                    .createGenerator(stream, JsonEncoding.UTF8);
            jGenerator.setCodec(new ObjectMapper());
            jGenerator.writeObject(this);
            jGenerator.close();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't persist object " + toString(), e);
        }
    }

    public String[] vaultNames() {
        String[] names = new String[vaults.length];
        for (int i=0;i<vaults.length;i++)
            names[i] = vaults[i].name;
        return names;
    }
}
