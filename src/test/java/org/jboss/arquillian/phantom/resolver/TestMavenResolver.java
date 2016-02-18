package org.jboss.arquillian.phantom.resolver;

import java.io.File;
import java.io.IOException;

import org.jboss.arquillian.phantom.resolver.maven.MavenPhantomJSBinaryResolver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.os.CommandLine;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TestMavenResolver {

    private PhantomJSBinaryResolver resolver = new MavenPhantomJSBinaryResolver();
    @Before
    public void setUp() {
        System.setProperty(ResolverConfiguration.PHANTOMJS_BINARY_VERSION, ResolverConfiguration.DEFAULT_PHANTOMJS_BINARY_VERSION);
    }

    @After
    public void cleanUp() {
        System.setProperty(ResolverConfiguration.PHANTOMJS_BINARY_VERSION, ResolverConfiguration.DEFAULT_PHANTOMJS_BINARY_VERSION);
    }

    @Test
    public void testResolving() throws IOException {
        PhantomJSBinary binary = resolver.resolve(new File("target/testResolving-phantomjs"));
        File location = binary.getLocation();

        assertTrue(location.exists());
        location.delete();
    }

    @Test
    public void testIsExecutable() throws IOException {
        PhantomJSBinary binary = resolver.resolve(new File("target/folder with spaces/testExecutable-phantomjs"));
        File location = binary.getLocation();
        assertTrue(location.canExecute());
        location.delete();
    }

    @Test
    public void testDelete() throws IOException {
        PhantomJSBinary binary = resolver.resolve(new File("target/testDelete-phantomjs"));
        File location = binary.getLocation();
        assertTrue(location.exists());
        binary.delete();

        resolver.resolve(new File("target/testDelete-phantomjs"));
        assertTrue(location.exists());
        location.delete();
    }

    @Test
    public void testDefaultVersion() throws IOException {
        // when
        File location = resolver.resolve(new File("target/testDefaultVersion-phantomjs")).deleteOnExit().getLocation();
        CommandLine cmd = new CommandLine(location.getAbsolutePath(), new String[] { "--version" });
        cmd.execute();

        // then
        assertThat(cmd.getStdOut(), containsString(ResolverConfiguration.DEFAULT_PHANTOMJS_BINARY_VERSION));
        location.delete();
    }

    @Test
    public void testChangingVersion() throws IOException {
        // given
        System.setProperty(ResolverConfiguration.PHANTOMJS_BINARY_VERSION,
                           "1.9.8");

        // when
        File location = resolver.resolve(new File("target/testVersion-phantomjs")).deleteOnExit().getLocation();
        CommandLine cmd = new CommandLine(location.getAbsolutePath(), new String[] { "--version" });
        cmd.execute();

        // then
        assertThat(cmd.getStdOut(), containsString("1.9.8"));
        location.delete();
    }

    @Test
    public void testChangingVersionViaParameter() throws IOException {
        // when
        File location =
            resolver.resolve(new File("target/testVersion-phantomjs"), "1.9.7").deleteOnExit().getLocation();
        CommandLine cmd = new CommandLine(location.getAbsolutePath(), new String[] { "--version" });
        cmd.execute();

        // then
        assertThat(cmd.getStdOut(), containsString("1.9.7"));
        location.delete();
    }

    @Test
    public void testReformatCLIArgumentsInCapToArray() throws IOException {
        // given
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, "--version");
        ResolvingPhantomJSDriverService.reformatCLIArgumentsInCapToArray(capabilities);

        // when
        File location = resolver.resolve(new File("target/testVersion-phantomjs")).deleteOnExit().getLocation();
        CommandLine cmd = new CommandLine(location.getAbsolutePath(), (String[]) capabilities.getCapability(
            PhantomJSDriverService.PHANTOMJS_CLI_ARGS));
        cmd.execute();

        // then
        assertThat(cmd.getStdOut(), containsString(ResolverConfiguration.DEFAULT_PHANTOMJS_BINARY_VERSION));
        location.delete();
    }
}
