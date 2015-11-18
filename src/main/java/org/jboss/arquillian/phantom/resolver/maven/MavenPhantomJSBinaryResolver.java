/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.phantom.resolver.maven;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import org.jboss.arquillian.phantom.resolver.FileUtils;
import org.jboss.arquillian.phantom.resolver.PhantomJSBinary;
import org.jboss.arquillian.phantom.resolver.PhantomJSBinaryResolver;
import org.jboss.arquillian.phantom.resolver.ResolverConfiguration;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

public class MavenPhantomJSBinaryResolver implements PhantomJSBinaryResolver {

    public static final String PHANTOMJS = "phantomjs" + (PlatformUtils.isWindows() ? ".exe" : "");
    public static final String PHANTOMJS_RESOURCE = (PlatformUtils.isWindows() ? "" : "bin/") + PHANTOMJS;

    protected static final String ARTIFACT_BINARY = "org.jboss.arquillian.extension:arquillian-phantom-binary:jar";

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.arquillian.phantom.resolver.PhantomJSBinaryResolver#resolve(java.io.File)
     */
    @Override
    public PhantomJSBinary resolve(File destination) throws IOException {
        File realDestination = destination.isDirectory() ? new File(destination, PHANTOMJS) : destination;
        if (realDestination.exists() && realDestination.length() > 0 && realDestination.canExecute()) {
            return new PhantomJSBinary(realDestination);
        }
        return resolveFreshExtracted(realDestination);
    }

    /**
     * Resolves fresh phantomjs binary from Maven
     */
    protected PhantomJSBinary resolveFreshExtracted(File destination) throws IOException {
        if (destination.exists()) {
            destination.delete();
        }
        if (!destination.getParentFile().exists()) {
            destination.getParentFile().mkdirs();
        }
        ZipFile jar = new ZipFile(getJavaArchive());
        FileUtils.extract(jar, PHANTOMJS_RESOURCE, destination);
        return new PhantomJSBinary(destination);
    }

    /**
     * Obtains JavaArchive with phantomjs binary
     */
    protected File getJavaArchive() {
        final String version = ResolverConfiguration.get().version();
        final String gav = getArtifactCanonicalForm(version);

        return Maven.resolver().resolve(gav).withoutTransitivity().asSingleFile();
    }

    /**
     * Returns GAV for given artifact version
     */
    protected String getArtifactCanonicalForm(String version) {
        switch (PlatformUtils.platform().os()) {
            case WINDOWS:
                return ARTIFACT_BINARY + ":windows:" + version;

            case UNIX:
                StringBuffer messageLinux = new StringBuffer("This version does not support Linux - ");
                messageLinux.append("use an older one please. ");
                messageLinux.append(
                    "The reason is that there is no binary packages of PhantomJS 2.0.0 available for Linux. ");
                messageLinux.append("This version supports only windows and macosx. ");
                messageLinux.append("For more information see http://phantomjs.org/download.html ");
                messageLinux.append("or https://github.com/ariya/phantomjs/issues/12948");
                throw new IllegalStateException(messageLinux.toString());

            case MACOSX:
                return ARTIFACT_BINARY + ":macosx:" + version;

            default:
                StringBuffer messageOthers = new StringBuffer("The current platform is not supported. ");
                messageOthers.append("Platforms supported by this version are only windows and macosx. ");
                messageOthers.append("Your platform has been detected as ");
                messageOthers.append(PlatformUtils.platform().os().toString().toLowerCase() + " ");
                messageOthers.append("from the the system property 'os.name' => '" + PlatformUtils.OS + "'.");
                throw new IllegalStateException(messageOthers.toString());
        }
    }

}
