package org.jboss.arquillian.phantom.resolver;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils {

    private static final String OS = System.getProperty("os.name").toLowerCase();

    private FileUtils() {
    }

    public static File extract(final ZipFile jar, final String filename, final File destination) throws IOException {
        final ZipEntry entry = jar.getEntry(filename);
        if (entry == null) {
            throw new FileNotFoundException("cannot find file: " + filename + " in archive: " + jar.getName());
        }

        final InputStream zipStream = jar.getInputStream(entry);
        OutputStream fileStream = null;

        try {
            final byte[] buf;
            int i;

            fileStream = new FileOutputStream(destination);
            buf = new byte[1024];
            i = 0;

            while ((i = zipStream.read(buf)) != -1) {
                fileStream.write(buf, 0, i);
            }
        } finally {
            close(zipStream);
            close(fileStream);
        }

        return destination;
    }

    public static void close(final Closeable stream) throws IOException {
        if (stream != null) {
            stream.close();
        }
    }

    public static void setExecutable(File file) throws IOException {
        if (isUnix()) {
            try {
                Runtime.getRuntime().exec("chmod +x " + file.getAbsolutePath()).waitFor();
            } catch (InterruptedException ex) {
                Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 || "mac os x".equals(OS));
    }
}
