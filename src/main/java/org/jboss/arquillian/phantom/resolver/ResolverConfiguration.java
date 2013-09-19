package org.jboss.arquillian.phantom.resolver;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.arquillian.phantom.resolver.maven.MavenPhantomJSBinaryResolver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;

public final class ResolverConfiguration {

    /**
     * Set capability to true when binary should be resolved from a binary resolver; set to false when a binary from PATH should
     * be prefered
     */
    public final static String PREFER_RESOLVED = "phantomjs.prefer.resolved";
    private final static boolean DEFAULT_PREFER_RESOLVED = true;

    /**
     * A capability name which denotes which Binary resolver will be used to obtain a {@link PhantomJSBinary}
     */
    public final static String PHANTOMJS_BINARY_RESOLVER = "phantomjs.binary.resolver";
    private final static DefaultValue<PhantomJSBinaryResolver> DEFAULT_PHANTOMJS_BINARY_RESOLVER = new DefaultValue<PhantomJSBinaryResolver>() {
        public PhantomJSBinaryResolver getDefault() {
            return new MavenPhantomJSBinaryResolver();
        }
    };

    /**
     * Defines the location of the PhantomJS executable where should be binary found or where should be binary automatically
     * resolved
     */
    public static final String PHANTOMJS_EXECUTABLE_PATH = PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY;
    private final static DefaultValue<String> DEFAULT_PHANTOMJS_EXECUTABLE_PATH = new DefaultValue<String>() {
        public String getDefault() {
            try {
                LOG.log(Level.WARNING, "{0} capability isn't set, so resolving phantomjs binary as temporary file.",
                        new String[] { ResolverConfiguration.PHANTOMJS_EXECUTABLE_PATH });

                File tempFile = File.createTempFile("phantomjs-binary-", "");
                tempFile.deleteOnExit();
                return tempFile.getAbsolutePath();
            } catch (IOException e) {
                throw new IllegalStateException("Unable to create temporary file for phantomjs binary", e);
            }
        }
    };

    /**
     * Defines the version of PhantomJS executable binary to resolve
     */
    public static final String PHANTOMJS_BINARY_VERSION = "phantomjs.binary.version";
    static final String DEFAULT_PHANTOMJS_BINARY_VERSION = "1.9.2";

    private static final Logger LOG = Logger.getLogger(ResolvingPhantomJSDriverService.class.getName());

    private Capabilities capabilities;

    private ResolverConfiguration(Capabilities capabilities) {
        this.capabilities = capabilities;
    }

    /**
     * Default configuration which resolves just from System properties or default value
     */
    public static ResolverConfiguration get() {
        return new ResolverConfiguration(null);
    }

    /**
     * Resolves from capabilities or system properties or default value
     */
    public static ResolverConfiguration get(Capabilities capabilities) {
        return new ResolverConfiguration(capabilities);
    }

    public boolean preferResolved() {
        return capabilityOrDefault(PREFER_RESOLVED, DEFAULT_PREFER_RESOLVED);
    }

    public PhantomJSBinaryResolver resolver() {
        return capabilityOrPropertyOrDefault(PHANTOMJS_BINARY_RESOLVER, DEFAULT_PHANTOMJS_BINARY_RESOLVER);
    }

    public File executablePath() {
        return new File(capabilityOrPropertyOrDefault(PHANTOMJS_EXECUTABLE_PATH, DEFAULT_PHANTOMJS_EXECUTABLE_PATH));
    }

    public String version() {
        return capabilityOrDefault(PHANTOMJS_BINARY_VERSION, DEFAULT_PHANTOMJS_BINARY_VERSION);
    }

    private <T> T capabilityOrDefault(String capabilityName, final T defaultValue) {
        return capabilityOrPropertyOrDefault(capabilityName, new DefaultValue<T>() {
            @Override
            public T getDefault() {
                return defaultValue;
            }
        });
    }

    private <T> T capabilityOrPropertyOrDefault(String capabilityOrPropertyName, DefaultValue<T> defaultValue) {

        // obtain system property
        Object object = System.getProperty(capabilityOrPropertyName);

        // try obtain from capabilities
        if (capabilities != null) {
            object = capabilities.getCapability(capabilityOrPropertyName);
        }

        if (object != null) {
            return (T) object;

        }

        return defaultValue.getDefault();
    }

    private static interface DefaultValue<T> {
        T getDefault();
    }
}
