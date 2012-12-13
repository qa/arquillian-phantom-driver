package org.jboss.selenium.phantomjs.resolver;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.os.CommandLine;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.service.DriverService;

public class ResolvingPhantomJSDriverService extends DriverService {

    public static final String PHANTOMJS_EXECUTABLE_PATH_PROPERTY = PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY;
    public static final String PHANTOMJS_DEFAULT_EXECUTABLE = "phantomjs";

    private static final Logger LOG = Logger.getLogger(ResolvingPhantomJSDriverService.class.getName());

    private ResolvingPhantomJSDriverService(File executable, int port, ImmutableList<String> args, ImmutableMap<String, String> environment) throws IOException {
        super(executable, port, args, environment);
    }

    public static DriverService createDefaultService(Capabilities capabilities) throws IOException {
        PhantomJSBinary binary;
        String phantomjs = capabilities == null ? null : (String) capabilities.getCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY);
        if (phantomjs == null) {
            if (CommandLine.find(PHANTOMJS_DEFAULT_EXECUTABLE) != null) {
                return PhantomJSDriverService.createDefaultService(capabilities);
            } else {
                File temp = File.createTempFile("drone-phantomjs-", "");
                temp.deleteOnExit();
                LOG.log(Level.WARNING, "{0} capability isn''t set, so resolving phantomjs binary as {1}", new String[] {PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, temp.getAbsolutePath()});
                binary = new PhantomJSBinaryResolver().resolve(temp);
            }
        } else {
            binary = new PhantomJSBinaryResolver().resolve(phantomjs);
        }
        DesiredCapabilities newCapabilities = new DesiredCapabilities(capabilities);
        newCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, binary.getLocation().getAbsolutePath());
        return PhantomJSDriverService.createDefaultService(newCapabilities);
    }

    public static DriverService createDefaultService() throws IOException {
        return createDefaultService(null);
    }

}
