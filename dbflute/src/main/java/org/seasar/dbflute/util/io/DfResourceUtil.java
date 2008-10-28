package org.seasar.dbflute.util.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.seasar.dbflute.util.net.DfURLUtil;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public abstract class DfResourceUtil {

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

    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static URL getResource(String path) {
        return getResource(path, null);
    }

    public static URL getResource(String path, String extension) {
        URL url = getResourceNoException(path, extension);
        if (url != null) {
            return url;
        }
        throw new IllegalStateException(getResourcePath(path, extension));
    }

    public static URL getResourceNoException(String path) {
        return getResourceNoException(path, null);
    }

    public static URL getResourceNoException(String path, String extension) {
        return getResourceNoException(path, extension, Thread.currentThread().getContextClassLoader());
    }

    public static URL getResourceNoException(String path, String extension, ClassLoader loader) {
        if (path == null || loader == null) {
            return null;
        }
        path = getResourcePath(path, extension);
        return loader.getResource(path);
    }

    public static InputStream getResourceAsStream(String path) {
        return getResourceAsStream(path, null);
    }

    public static InputStream getResourceAsStream(String path, String extension) {
        URL url = getResource(path, extension);
        return DfURLUtil.openStream(url);
    }

    public static InputStream getResourceAsStreamNoException(String path) {
        return getResourceAsStreamNoException(path, null);
    }

    public static InputStream getResourceAsStreamNoException(String path, String extension) {
        URL url = getResourceNoException(path, extension);
        if (url == null) {
            return null;
        }
        try {
            return url.openStream();
        } catch (final IOException e) {
            return null;
        }
    }

    public static boolean isExist(String path) {
        return getResourceNoException(path) != null;
    }

    public static Properties getProperties(String path) {
        Properties props = new Properties();
        InputStream is = getResourceAsStream(path);
        try {
            props.load(is);
            return props;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String getExtension(String path) {
        int extPos = path.lastIndexOf(".");
        if (extPos >= 0) {
            return path.substring(extPos + 1);
        }
        return null;
    }

    public static String removeExtension(String path) {
        int extPos = path.lastIndexOf(".");
        if (extPos >= 0) {
            return path.substring(0, extPos);
        }
        return path;
    }

    public static File getBuildDir(Class<?> clazz) {
        return getBuildDir(getResourcePath(clazz));
    }

    public static File getBuildDir(String path) {
        File dir = null;
        URL url = getResource(path);
        if ("file".equals(url.getProtocol())) {
            int num = path.split("/").length;
            dir = new File(getFileName(url));
            for (int i = 0; i < num; ++i, dir = dir.getParentFile()) {
            }
        } else {
            dir = new File(DfJarFileUtil.toJarFilePath(url));
        }
        return dir;
    }

    public static String toExternalForm(URL url) {
        String s = url.toExternalForm();
        return DfURLUtil.decode(s, "UTF8");
    }

    public static String getFileName(URL url) {
        String s = url.getFile();
        return DfURLUtil.decode(s, "UTF8");
    }

    public static File getFile(URL url) {
        File file = new File(getFileName(url));
        if (file != null && file.exists()) {
            return file;
        }
        return null;
    }

    public static File getResourceAsFile(String path) {
        return getResourceAsFile(path, null);
    }

    public static File getResourceAsFile(String path, String extension) {
        return getFile(getResource(path, extension));
    }

    public static File getResourceAsFileNoException(Class<?> clazz) {
        return getResourceAsFileNoException(getResourcePath(clazz));
    }

    public static File getResourceAsFileNoException(String path) {
        URL url = getResourceNoException(path);
        if (url == null) {
            return null;
        }
        return getFile(url);
    }

    public static String convertPath(String path, Class<?> clazz) {
        if (isExist(path)) {
            return path;
        }
        String prefix = clazz.getName().replace('.', '/').replaceFirst("/[^/]+$", "");
        String extendedPath = prefix + "/" + path;
        if (getResourceNoException(extendedPath) != null) {
            return extendedPath;
        }
        return path;
    }

}