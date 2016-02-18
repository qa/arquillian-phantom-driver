package org.jboss.arquillian.phantom.resolver;

import java.io.File;
import java.io.IOException;

import org.jboss.arquillian.phantom.resolver.maven.MavenPhantomJSBinaryResolver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.os.CommandLine;

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
    }

    @Test
    public void testIsExecutable() throws IOException {
        PhantomJSBinary binary = resolver.resolve(new File("target/folder with spaces/testExecutable-phantomjs"));
        File location = binary.getLocation();
        assertTrue(location.canExecute());
    }

    @Test
    public void testDelete() throws IOException {
        PhantomJSBinary binary = resolver.resolve(new File("target/testDelete-phantomjs"));
        File location = binary.getLocation();
        assertTrue(location.exists());
        binary.delete();

        resolver.resolve(new File("target/testDelete-phantomjs"));
        assertTrue(location.exists());
    }

    @Test
    public void testDefaultVersion() throws IOException {
        // when
        File location = resolver.resolve(new File("target/testDefaultVersion-phantomjs")).deleteOnExit().getLocation();
        CommandLine cmd = new CommandLine(location.getAbsolutePath(), new String[] { "--version" });
        cmd.execute();

        // then
        assertThat(cmd.getStdOut(), containsString(ResolverConfiguration.DEFAULT_PHANTOMJS_BINARY_VERSION));
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
    }
}
