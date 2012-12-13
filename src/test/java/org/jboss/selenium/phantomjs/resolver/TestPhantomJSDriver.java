package org.jboss.selenium.phantomjs.resolver;

import java.io.IOException;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class TestPhantomJSDriver {

    @Test
    public void testSimple() throws IOException {
        WebDriver driver = new PhantomJSDriver(ResolvingPhantomJSDriverService.createDefaultService(), DesiredCapabilities.phantomjs());
        driver.get("http://google.com");
        System.out.println(driver.getTitle());
        driver.quit();
    }

}
