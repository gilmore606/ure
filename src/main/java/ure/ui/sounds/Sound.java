package ure.ui.sounds;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ure.math.URandom;
import ure.sys.Injector;

import javax.inject.Inject;

public class Sound {

    @Inject
    @JsonIgnore
    public USpeaker speaker;
    @Inject
    @JsonIgnore
    public URandom random;

    String file;
    String[] files;
    float gain;

    public Sound() {
        Injector.getAppComponent().inject(this);
    }
    public Sound(String file) {
        this();
        this.file = file;
        this.gain = 1f;
    }
    public Sound(String[] files) {
        this();
        this.files = files;
        this.gain = 1f;
    }
    public Sound(String file, float gain) {
        this();
        this.file = file;
        this.gain = gain;
    }
    public Sound(String[] files, float gain) {
        this();
        this.files = files;
        this.gain = gain;
    }


    public String file() {
        if (file != null)
            return file;
        if (files != null)
            return random.member(files);
        return null;
    }

    public float gain() {
        return gain;
    }

    public String getFile() { return file; }
    public void setFile(String s) { file = s; }
    public String[] getFiles() { return files; }
    public void setFiles(String[] s) { files = s; }
    public float getGain() { return gain; }
    public void setGain(float f) { gain = f; }

}
