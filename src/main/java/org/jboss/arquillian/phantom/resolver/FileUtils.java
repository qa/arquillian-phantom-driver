package org.jboss.arquillian.phantom.resolver;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import org.openqa.selenium.Platform;

import static org.openqa.selenium.Platform.WINDOWS;

public class FileUtils {

    private static final FileExecutableChecker fileExecutableChecker = new FileExecutableChecker();
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final ImmutableSet<String> ENDINGS = Platform.getCurrent().is(WINDOWS) ?
        ImmutableSet.of("", ".cmd", ".exe", ".com", ".bat") : ImmutableSet.of("");

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
                ProcessBuilder pb = new ProcessBuilder("chmod", "+x", file.getAbsolutePath());
                int exitCode = pb.start().waitFor();
                if (exitCode != 0) {
                    throw new IOException("Unable to set executable flag on " + file.getAbsolutePath()
                                              + ". Exit code was " + exitCode);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 || "mac os x".equals(OS));
    }

    public static boolean isExecutable(File file) throws IllegalArgumentException {
        if (!file.exists()) {
            throw new IllegalArgumentException(String.format("The file %s does not exist", file));
        }

        return fileExecutableChecker.canExecute(file);
    }

    /**
     * Find the executable by scanning the file system and the PATH. In the case of Windows this
     * method allows common executable endings (".com", ".bat" and ".exe") to be omitted.
     *
     * @param command The name of the executable to find
     * @return Whether the command is executable or not.
     */
    public static boolean isExecutable(String command) throws IllegalArgumentException {
        File file = new File(command);
        if (fileExecutableChecker.canExecute(file)) {
            return true;
        }

        if (Platform.getCurrent().is(Platform.WINDOWS)) {
            file = new File(command + ".exe");
            if (fileExecutableChecker.canExecute(file)) {
                return true;
            }
        }

        final ImmutableSet.Builder<String> pathSegmentBuilder = new ImmutableSet.Builder<>();
        addPathFromEnvironment(pathSegmentBuilder);
        if (Platform.getCurrent().is(Platform.MAC)) {
            addMacSpecificPath(pathSegmentBuilder);
        }

        for (String pathSegment : pathSegmentBuilder.build()) {
            for (String ending : ENDINGS) {
                file = new File(pathSegment, command + ending);
                if (fileExecutableChecker.canExecute(file)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void addPathFromEnvironment(final ImmutableSet.Builder<String> pathSegmentBuilder) {
        String pathName = "PATH";
        Map<String, String> env = System.getenv();
        if (!env.containsKey(pathName)) {
            for (String key : env.keySet()) {
                if (pathName.equalsIgnoreCase(key)) {
                    pathName = key;
                    break;
                }
            }
        }
        String path = env.get(pathName);
        if (path != null) {
            pathSegmentBuilder.add(path.split(File.pathSeparator));
        }
    }

    private static void addMacSpecificPath(final ImmutableSet.Builder<String> pathSegmentBuilder) {
        File pathFile = new File("/etc/paths");
        if (pathFile.exists()) {
            try {
                pathSegmentBuilder.addAll(Files.readLines(pathFile, Charsets.UTF_8));
            } catch (IOException e) {
                // Guess we won't include those, then
            }
        }
    }

    /**
     * Checker if a file can be executed. It requires Java 6 to do that. If anything goes wrong, it supposes that a file can be
     * executed.
     */
    private static final class FileExecutableChecker {
        private static final Logger log = Logger.getLogger(FileExecutableChecker.class.getName());

        private final Method isExecutableMethod;

        FileExecutableChecker() {
            Method m = null;
            try {
                m = File.class.getMethod("canExecute");
            } catch (SecurityException e) {
                log.warning(
                    "Unable to verify executable bits for files, will consider them all executable. " + e.getMessage());
            } catch (NoSuchMethodException e) {
                log.warning(
                    "Unable to verify executable bits for files, will consider them all executable. " + e.getMessage());
            }

            isExecutableMethod = m;
        }

        public boolean canExecute(File file) {
            if (isExecutableMethod == null) {
                return true;
            }

            Boolean result = true;
            try {
                result = (Boolean) isExecutableMethod.invoke(file);
            } catch (IllegalArgumentException e) {
                log.warning(
                    "Unable to check if " + file.getAbsolutePath() + " can be executed, will consider it executable."
                        + e.getMessage());
            } catch (IllegalAccessException e) {
                log.warning(
                    "Unable to check if " + file.getAbsolutePath() + " can be executed, will consider it executable."
                        + e.getMessage());
            } catch (InvocationTargetException e) {
                log.warning(
                    "Unable to check if " + file.getAbsolutePath() + " can be executed, will consider it executable."
                        + e.getMessage());
            }

            return result;
        }
    }
}
