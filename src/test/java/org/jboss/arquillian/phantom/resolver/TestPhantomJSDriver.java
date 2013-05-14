package org.jboss.arquillian.phantom.resolver;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class TestPhantomJSDriver {

    @Test
    public void testSimple() throws IOException {
        WebDriver driver = new PhantomJSDriver(ResolvingPhantomJSDriverService.createDefaultService(), DesiredCapabilities.phantomjs());
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
        capabilities.setCapability(ResolvingPhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, binary.getAbsoluteFile().getPath());
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
