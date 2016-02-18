package org.jboss.arquillian.phantom.resolver;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.os.CommandLine;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.service.DriverService;

public class ResolvingPhantomJSDriverService extends DriverService {

    private static final String PHANTOMJS_DEFAULT_EXECUTABLE = "phantomjs";

    private static final Logger LOG = Logger.getLogger(ResolvingPhantomJSDriverService.class.getName());

    private ResolvingPhantomJSDriverService(File executable, int port, ImmutableList<String> args,
            ImmutableMap<String, String> environment) throws IOException {
        super(executable, port, args, environment);
    }

    /**
     * Creates a new service with phantomjs binary from PATH; if there is no phantomjs on PATH, binary will be resolved to
     * temporary location
     */
    public static PhantomJSDriverService createDefaultService() throws IOException {
        return createDefaultService(null);
    }

    /**
     * If {@link ResolverConfiguration#PREFER_RESOLVED} capability is set to true, the phantomjs binary will be always resolved
     * automatically to temporary location.
     *
     * In opposite case, service will first check whether there is executable phantomjs binary on PATH and fallbacks to its
     * automatic resolution.
     *
     * See {@link ResolverConfiguration} for list of capabilities which can be set in order to change behavior of resolver.
     */
    public static PhantomJSDriverService createDefaultService(Capabilities capabilities) throws IOException {

        final ResolverConfiguration configuration = ResolverConfiguration.get(capabilities);

        if (!configuration.preferResolved() && isDefaultExecutablePresent()) {
            return PhantomJSDriverService.createDefaultService(capabilities);
        }

        PhantomJSBinary binary = resolveBinary(configuration);

        DesiredCapabilities newCapabilities = new DesiredCapabilities(capabilities);
        newCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, binary.getLocation()
                .getAbsolutePath());
        reformatCLIArgumentsInCapToArray(newCapabilities);

        return PhantomJSDriverService.createDefaultService(newCapabilities);
    }

    /**
     * Reformats {@link PhantomJSDriverService.PHANTOMJS_CLI_ARGS} and
     * {@link PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS} from String to String[]
     *
     * @param capabilities Capabilities
     */
    protected static void reformatCLIArgumentsInCapToArray(DesiredCapabilities capabilities){
        reformatCapabilityToArray(capabilities, PhantomJSDriverService.PHANTOMJS_CLI_ARGS);
        reformatCapabilityToArray(capabilities, PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS);
    }

    private static void reformatCapabilityToArray(DesiredCapabilities capabilities, String capabilityName){
        Object capability = capabilities.getCapability(capabilityName);
        if (capability != null) {
            if (capability instanceof String){
                String[] splitArgs = ((String) capability).split(" ");
                capabilities.setCapability(capabilityName, splitArgs);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private static boolean isDefaultExecutablePresent() {
        return CommandLine.find(PHANTOMJS_DEFAULT_EXECUTABLE) != null;
    }

    /**
     * Resolves phantomjs binary from configured resolver with configured version (fallbacks to defaults).
     *
     * The binary is resolved to path given by #PHA
     */
    private static PhantomJSBinary resolveBinary(ResolverConfiguration configuration) throws IOException {

        final PhantomJSBinaryResolver binaryResolver = configuration.resolver();

        File executablePath = configuration.executablePath();
        String version = configuration.version();

        PhantomJSBinary binary = binaryResolver.resolve(executablePath, version);

        return binary;
    }
}
