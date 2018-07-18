package ure.areas;

import java.util.ArrayList;

public class UVaultSet {

    public String filename;
    public ArrayList<UVault> vaults;
    public String[] tags;

    public UVaultSet() {

    }

    public UVaultSet(String _filename) {
        filename = _filename;
    }

    public void setVaults(ArrayList<UVault> _vaults) {
        vaults = _vaults;
    }
    public ArrayList<UVault> getVaults() { return vaults; }
    public void setTags(String[] _tags) { tags = _tags; }
    public String[] getTags() { return tags; }
    public void setFilename(String _filename) { filename = _filename; }
    public String getFilename() { return filename; }

}
