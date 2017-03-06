package org.jboss.arquillian.phantom.resolver;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class PhantomJSBinary {

    private Logger log = Logger.getLogger(PhantomJSBinary.class.getName());
    private final File location;

    public PhantomJSBinary(String location) throws IOException {
        this(new File(location));
    }

    public PhantomJSBinary(File location) throws IOException {
        this.location = location;
        if (!FileUtils.isExecutable(location)) {
            log.info("The file " + location + " will be set as executable.");
            FileUtils.setExecutable(location);
        }
    }

    public File getLocation() {
        return location;
    }

    public boolean delete() {
        return location.delete();
    }

    public PhantomJSBinary deleteOnExit() {
        location.deleteOnExit();
        return this;
    }

}
