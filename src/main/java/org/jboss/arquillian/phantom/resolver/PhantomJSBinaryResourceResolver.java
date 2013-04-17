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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.zip.ZipFile;

/**
 * This class can resolve phantomjs binary needed to start server for PhantomJS
 * driver.
 */
public class PhantomJSBinaryResourceResolver implements PhantomJSBinaryResolver {

    public static final String CHECKSUM_EXTENSION = "sha1";
    public static final String PHANTOMJS = "phantomjs" + (isWindows() ? ".exe" : "");
    public static final String PHANTOMJS_RESOURCE = (isWindows() ? "" : "bin/") + PHANTOMJS;
    public static final String PHANTOMJS_CHECKSUM = PHANTOMJS_RESOURCE + "." + CHECKSUM_EXTENSION;

    @Override
    public PhantomJSBinary resolve(String destination) throws IOException {
        return resolve(new File(destination));
    }

    @Override
    public PhantomJSBinary resolve(File destination) throws IOException {
        File realDestination = destination.isDirectory() ? new File(destination, PHANTOMJS) : destination;
        if (alreadyExists(realDestination)) {
            return new PhantomJSBinary(realDestination, new File(realDestination.getPath() + "." + CHECKSUM_EXTENSION));
        }
        return resolveFreshExtracted(realDestination);
    }

    protected boolean alreadyExists(File destination) throws IOException {
        File extractedChecksumFile = new File(destination.getAbsolutePath() + "." + CHECKSUM_EXTENSION);
        if (!extractedChecksumFile.exists()) {
            return false;
        }
        URL inArchiveChecksumURL = PhantomJSBinaryResourceResolver.class.getClassLoader().getResource(PHANTOMJS_CHECKSUM);
        BufferedReader extractedReader = null;
        BufferedReader inArchiveReader = null;
        try {
            extractedReader = new BufferedReader(new InputStreamReader(new FileInputStream(extractedChecksumFile)));
            inArchiveReader = new BufferedReader(new InputStreamReader(inArchiveChecksumURL.openStream()));
            String extracted = extractedReader.readLine();
            String inArchive = inArchiveReader.readLine();
            return extracted != null && extracted.equals(inArchive);
        } finally {
            FileUtils.close(extractedReader);
            FileUtils.close(inArchiveReader);
        }
    }

    protected File getJavaArchive(URL resource) {
        if (resource == null) {
            throw new IllegalArgumentException("The given resource is null.");
        }
        try {
            String path = URLDecoder.decode(resource.getPath(), "UTF-8");
            return new File(path.split("!")[0].replace("file:", ""));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unable to decode a file path '" + resource.getPath() + "'.", e);
        }
    }

    protected PhantomJSBinary resolveFromClassPath() throws IOException {
        return new PhantomJSBinary(
                getClass().getClassLoader().getResource(PHANTOMJS).getFile(),
                getClass().getClassLoader().getResource(PHANTOMJS).getFile() + "." + CHECKSUM_EXTENSION);
    }

    protected PhantomJSBinary resolveFreshExtracted(File destination) throws IOException {
        File checksum = new File(destination.getAbsolutePath() + "." + CHECKSUM_EXTENSION);
        if (!destination.exists()) {
            destination.delete();
        }
        if (checksum.exists()) {
            checksum.delete();
        }
        ZipFile jar = new ZipFile(getJavaArchive(getClass().getClassLoader().getResource(PHANTOMJS_RESOURCE)));
        FileUtils.extract(jar, PHANTOMJS_RESOURCE, destination);
        FileUtils.extract(jar, PHANTOMJS_CHECKSUM, checksum);
        return new PhantomJSBinary(destination, checksum);
    }

    protected static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
    }

}
