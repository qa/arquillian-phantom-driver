package org.jboss.selenium.phantomjs.resolver;

import java.io.File;
import java.io.IOException;

public class PhantomJSBinary {

    private final File location;



    public PhantomJSBinary(String location) throws IOException {
        this(new File(location));
    }
    public PhantomJSBinary(File location) throws IOException {
        this.location = location;
        FileUtils.setExecutable(location);
    }

    public File getLocation() {
        return location;
    }

    public boolean delete() {
        return location.delete();
    }

    public void deleteOnExit() {
        location.deleteOnExit();
    }

}
