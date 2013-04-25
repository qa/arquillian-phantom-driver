package org.jboss.arquillian.phantom.resolver;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class TestShrinkwrapPhantomJSBinaryResolver {

    private PhantomJSBinaryResolver resolver = new ShrinkwrapPhantomJSBinaryResolver();

    @Test
    public void testResolving() throws IOException {
        PhantomJSBinary binary = resolver.resolve("target/testResolving-phantomjs");
        File location = binary.getLocation();

        assertTrue(location.exists());
    }

    @Test
    public void testDelete() throws IOException {
        PhantomJSBinary binary = resolver.resolve("target/testDelete-phantomjs");
        File location = binary.getLocation();
        assertTrue(location.exists());
        binary.delete();

        resolver.resolve("target/testDelete-phantomjs");
        assertTrue(location.exists());
    }
}
