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
package org.jboss.arquillian.phantom.resolver;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

public class ShrinkwrapPhantomJSBinaryResolver implements PhantomJSBinaryResolver {

    public static final String PHANTOMJS = "phantomjs" + (PlatformUtils.isWindows() ? ".exe" : "");
    public static final String PHANTOMJS_RESOURCE = (PlatformUtils.isWindows() ? "" : "bin/") + PHANTOMJS;

    protected static final String ARTIFACT_BINARY = "org.jboss.arquillian.extension:arquillian-phantom-binary:jar";
    protected static final String PHANTOMJS_VERSION = "1.9.0";

    @Override
    public PhantomJSBinary resolve(String destination) throws IOException {
        return resolve(new File(destination));
    }

    @Override
    public PhantomJSBinary resolve(File destination) throws IOException {
        File realDestination = destination.isDirectory() ? new File(destination, PHANTOMJS) : destination;
        if (realDestination.exists()) {
            return new PhantomJSBinary(realDestination);
        }
        return resolveFreshExtracted(realDestination, PHANTOMJS_VERSION);
    }

    protected PhantomJSBinary resolveFreshExtracted(File destination, String version) throws IOException {
        if (destination.exists()) {
            destination.delete();
        }
        ZipFile jar = new ZipFile(getJavaArchive(version));
        FileUtils.extract(jar, PHANTOMJS_RESOURCE, destination);
        return new PhantomJSBinary(destination);
    }

    protected File getJavaArchive(String version) {
        return Maven.resolver().resolve(getArtifactCanonicalForm(version)).withoutTransitivity().asSingleFile();
    }

    protected String getArtifactCanonicalForm(String version) {
        switch (PlatformUtils.platform().os()) {
            case WINDOWS:
                return ARTIFACT_BINARY + ":windows:" + version;
            case UNIX:
                if (PlatformUtils.is64()) {
                    return ARTIFACT_BINARY + ":linux-64:" + version;
                } else {
                    return ARTIFACT_BINARY + ":linux-32:" + version;
                }
            case MACOSX:
                return ARTIFACT_BINARY + ":macosx:" + version;
            default:
                throw new IllegalStateException("The current platform is not supported."
                        + "Supported platforms are windows, linux and macosx."
                        + "Your platform has been detected as " + PlatformUtils.platform().os().toString().toLowerCase() + ""
                        + "from the the system property 'os.name' => '" + PlatformUtils.OS + "'.");
        }
    }

}
