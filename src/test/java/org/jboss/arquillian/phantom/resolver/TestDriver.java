package org.jboss.arquillian.phantom.resolver;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.jboss.arquillian.phantom.resolver.maven.PlatformUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

public class TestDriver {

    @Before
    public void setUp(){
        Assume.assumeFalse(PlatformUtils.platform().os() == PlatformUtils.OperatingSystem.UNIX);
    }

    @Test
    public void testSimple() throws IOException {
        PhantomJSDriverService service = ResolvingPhantomJSDriverService.createDefaultService();

        WebDriver driver = new PhantomJSDriver(service, DesiredCapabilities.phantomjs());
        loadPage(driver);
        Assert.assertEquals("The page title doesn't match.", "Simple Page", driver.getTitle());
        driver.quit();
    }

    @Test
    public void testSimpleWithPath() throws IOException {
        File binary = new File("target/phantomjs/phantomjs");
        if (binary.exists()) {
            binary.delete();
        }

        DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
        capabilities.setCapability(ResolverConfiguration.PHANTOMJS_EXECUTABLE_PATH, binary.getAbsoluteFile().getPath());
        WebDriver driver = new PhantomJSDriver(ResolvingPhantomJSDriverService.createDefaultService(capabilities), DesiredCapabilities.phantomjs());
        loadPage(driver);
        Assert.assertEquals("The page title doesn't match.", "Simple Page", driver.getTitle());
        Assert.assertTrue("The binary has to be craeted on the specified path.", binary.exists());
        driver.quit();
    }

    protected void loadPage(WebDriver browser) {
        URL page = this.getClass().getClassLoader().getResource("org/jboss/arquillian/selenium/phantomjs/resolver/simple.html");
        browser.get(page.toString());
    }

}
