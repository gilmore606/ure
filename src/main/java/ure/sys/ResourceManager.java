package ure.sys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ResourceManager {

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

    private InputStream getResourceAsStream( String resource ) {
        final InputStream in = getContextClassLoader().getResourceAsStream( resource );
        return in == null ? getClass().getResourceAsStream( resource ) : in;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

}
