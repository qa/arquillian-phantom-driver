package org.jboss.arquillian.phantom.resolver;

import java.io.File;
import java.io.IOException;

public class PhantomJSBinary {

    private final File location;
    private final File checksum;

    public PhantomJSBinary(String location, String checksum) throws IOException {
        this(new File(location), new File(checksum));
    }
    public PhantomJSBinary(File location, File checksum) throws IOException {
        this.location = location;
        this.checksum = checksum;
        FileUtils.setExecutable(location);
    }

    public File getLocation() {
        return location;
    }

    public boolean delete() {
        return location.delete() && checksum.delete();
    }

    public PhantomJSBinary deleteOnExit() {
        location.deleteOnExit();
        checksum.deleteOnExit();
        return this;
    }

}
