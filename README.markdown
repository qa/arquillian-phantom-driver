# Arquillian Phantom Driver

Arquillian Phantom Driver provides dependency on the [GhostDriver](https://github.com/detro/ghostdriver), dependency on [PhantomJS](http://phantomjs.org/) binary and provides runtime resolution of [the binary artifact](https://github.com/qa/arquillian-phantom-binary) (distributed in Maven repository) to enable true headless unattended testing without need of local PhantomJS installation.

## Usage

Add following snippet to your project's POM:

    <dependencies>
        <dependency>
            <groupId>org.jboss.arquillian.extension</groupId>
            <artifactId>arquillian-phantom-driver</artifactId>
            <version>1.1.0.Beta1</version>
        </dependency>
    </dependencies>

Java code:

    WebDriver driver = new PhantomJSDriver(
            ResolvingPhantomJSDriverService.createDefaultService(), // service resolving phantomjs binary automatically
            DesiredCapabilities.phantomjs());

or you can specify path to the phantomjs binary file. If the binary file doesn't exist or it isn't up to date,
the resolver will copy own file to the given path:

    DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();

    // where the binary binary is expected (will be resolved when file does not exist)
    capabilities.setCapability("phantomjs.binary.path", "./target/phantomjs");

    // enforce resolver to use given phantomjs version
    capabilities.setCapability("phantomjs.binary.version", "1.9.1");

    // if there is phantomjs available on PATH, use that one
    capabilities.setCapability("phantomjs.prefer.resolved", Boolean.FALSE);

    WebDriver driver = new PhantomJSDriver(
            ResolvingPhantomJSDriverService.createDefaultService(),
            capabilities);

## Installation

    mvn clean install
