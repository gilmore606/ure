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
    public ArrayList<UVault> vaults;
    public String[] tags;
    @JsonIgnore
    ObjectMapper objectMapper;

    public UVaultSet() {
        vaults = null;
    }

    public void setVaults(ArrayList<UVault> _vaults) {
        vaults = _vaults;
    }
    public ArrayList<UVault> getVaults() { return vaults; }
    public void setTags(String[] _tags) { tags = _tags; }
    public String[] getTags() { return tags; }
    public void setFilename(String _filename) { filename = _filename; }
    public String getFilename() { return filename; }

    public int size() { return vaults.size(); }
    public UVault vaultAt(int i) {
        return vaults.get(i);
    }
    public void putVault(int i, UVault vault) {
        vaults.set(i, vault);
    }
    public void setObjectMapper(ObjectMapper om) { objectMapper = om; }

    public void initialize() {
        vaults = new ArrayList<>();
        addVault();
    }

    public void addVault() {
        UVault vault = new UVault();
        vault.initialize();
        vaults.add(vault);
    }

    public void removeVault(UVault vault) {
        if (vaults.size() <= 1)
            return;
        vaults.remove(vault);
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
        String[] names = new String[vaults.size()];
        for (int i=0;i<vaults.size();i++)
            names[i] = vaults.get(i).name;
        return names;
    }
}
