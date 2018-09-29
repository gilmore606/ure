package ure.sys;

import javax.inject.Inject;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceManager {

    @Inject
    UConfig config;

    public ResourceManager() {
        Injector.getAppComponent().inject(this);
    }

    public List<String> getResourceFiles( String path ) {
        List<String> filenames = new ArrayList<>();

        try (
                InputStream in = getResourceAsStream( path );
                BufferedReader br = new BufferedReader( new InputStreamReader( in ) )
            )
        {
            String resource;

            while( (resource = br.readLine()) != null ) {
                filenames.add( resource );
            }
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        return filenames;
    }

    public InputStream getResourceAsStream( String resource ) {
        // Whatever is on the filesystem will always be the most current.  Use that if it exists.
        try {
            File file = new File(config.getResourcePath() + resource);
            return new FileInputStream(file);
        } catch (IOException e) {
            // No big deal, it's probably in the jar
        }
        final InputStream in = getContextClassLoader().getResourceAsStream( resource );
        return in == null ? getClass().getResourceAsStream( resource ) : in;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

}
