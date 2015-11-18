# Arquillian Phantom Driver

Arquillian Phantom Driver provides dependency on the [GhostDriver](https://github.com/detro/ghostdriver), dependency on [PhantomJS](http://phantomjs.org/) binary and provides runtime resolution of [the binary artifact](https://github.com/qa/arquillian-phantom-binary) (distributed in Maven repository) to enable true headless unattended testing without need of local PhantomJS installation.

##### NOTE: Please keep in mind that there is no binary packages of PhantomJS 2.0.0 available for Linux, so the version 1.2.0 of Arquillian Phantom Driver doesn't support the Linux platform! For more information see: http://phantomjs.org/download.html and https://github.com/ariya/phantomjs/issues/12948

## Usage

Add following snippet to your project's POM:

    <dependencies>
        <dependency>
            <groupId>org.jboss.arquillian.extension</groupId>
            <artifactId>arquillian-phantom-driver</artifactId>
            <version>${artifact.version}</version>
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
    capabilities.setCapability("phantomjs.binary.version", "2.0.0");

    // if there is phantomjs available on PATH, use that one
    capabilities.setCapability("phantomjs.prefer.resolved", Boolean.FALSE);

    // NOTE: capabilities must be passed into the ResolvingPhantomJSDriverService if you plan on passing custom
    // command-line arguments such as disabling SSL Certificate checking or other features.  

    WebDriver driver = new PhantomJSDriver(
            ResolvingPhantomJSDriverService.createDefaultService(capabilities),
            capabilities);

## Installation

    mvn clean install
