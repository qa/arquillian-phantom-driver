# PhantomJS Driver

Project improving [Ghost Driver](https://github.com/detro/ghostdriver) binding for Java.
You can use Ghost Driver binding for Java without installation of [PhantomJS](http://phantomjs.org/).

## Usage

Add following snippet to your project's POM:

    <dependencies>
        <dependency>
            <groupId>org.jboss.arquillian.selenium</groupId>
            <artifactId>selenium-phantomjs-driver</artifactId>
            <version>1.0.1-SNAPSHOT</version>
        </dependency>
    </dependencies>

Java code:

    WebDriver driver = new PhantomJSDriver(
            ResolvingPhantomJSDriverService.createDefaultService(), // service resolving phantomjs binary automatically
            DesiredCapabilities.phantomjs());

or you can specify path to the phantomjs binary file. If the binary file doesn't exist or it isn't up to date,
the resolver will copy own file to the given path:

    DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
    capabilities.setCapability("phantomjs.binary.path", "path/to/phantomjs/binary");

    WebDriver driver = new PhantomJSDriver(
            ResolvingPhantomJSDriverService.createDefaultService(),
            capabilities);

## Installation

    mvn clean install