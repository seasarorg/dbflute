package org.dbflute.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author DBFlute(AutoGenerator)
 */
public class SimpleResourceUtil {

    // ===================================================================================
    //                                                                       Resource Path
    //                                                                       =============
    public static String getResourcePath(String path, String extension) {
        if (extension == null) {
            return path;
        }
        extension = "." + extension;
        if (path.endsWith(extension)) {
            return path;
        }
        return path.replace('.', '/') + extension;
    }

    public static String getResourcePath(Class<?> clazz) {
        return clazz.getName().replace('.', '/') + ".class";
    }

    // ===================================================================================
    //                                                                        Resource URL
    //                                                                        ============
    public static URL getResourceUrl(String path) {
        return getResourceUrl(path, null);
    }

    public static URL getResourceUrl(String path, String extension) {
        return getResourceUrl(path, extension, Thread.currentThread().getContextClassLoader());
    }

    public static URL getResourceUrl(String path, String extension, ClassLoader loader) {
        if (path == null || loader == null) {
            return null;
        }
        path = getResourcePath(path, extension);
        return loader.getResource(path);
    }

    // ===================================================================================
    //                                                                     Resource Stream
    //                                                                     ===============
    public static InputStream getResourceStream(String path) {
        return getResourceStream(path, null);
    }

    public static InputStream getResourceStream(String path, String extension) {
        final URL url = getResourceUrl(path, extension);
        return url != null ? openStream(url) : null;
    }

    // ===================================================================================
    //                                                                  Resource Existence
    //                                                                  ==================
    public static boolean isExist(String path) {
        return getResourceUrl(path) != null;
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    protected static InputStream openStream(URL url) {
        try {
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException e) {
            String msg = "Failed to open the stream: url=" + url;
            throw new IllegalStateException(msg, e);
        }
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    /**
     * Assert that the object is not null.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     * @exception IllegalArgumentException
     */
    protected static void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=" + variableName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }
}
