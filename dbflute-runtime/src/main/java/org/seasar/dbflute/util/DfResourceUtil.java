package org.seasar.dbflute.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

import org.seasar.framework.util.JarFileUtil;

/**
 * {Refers to S2Container's utility and Extends it}
 * @author DBFlute(AutoGenerator)
 */
public class DfResourceUtil {

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

    private static String getFileName(URL url) {
        String s = url.getFile();
        return decode(s, "UTF8");
    }

    private static String decode(String s, String enc) {
        try {
            return URLDecoder.decode(s, enc);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
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
    //                                                                     Reader Handling
    //                                                                     ===============
    public static String readText(Reader reader) {
        BufferedReader in = new BufferedReader(reader);
        StringBuilder out = new StringBuilder(100);
        try {
            try {
                char[] buf = new char[8192];
                int n;
                while ((n = in.read(buf)) >= 0) {
                    out.append(buf, 0, n);
                }
            } finally {
                in.close();
            }
        } catch (IOException e) {
            String msg = "The IOException occurred: reader=" + reader;
            throw new IllegalStateException(msg, e);
        }
        return out.toString();
    }

    // ===================================================================================
    //                                                            Build Directory Handling
    //                                                            ========================
    public static File getBuildDir(Class<?> clazz) {
        return getBuildDir(getResourcePath(clazz));
    }

    public static File getBuildDir(String path) {
        File dir = null;
        URL url = getResourceUrl(path);
        if ("file".equals(url.getProtocol())) {
            int num = path.split("/").length;
            dir = new File(getFileName(url));
            for (int i = 0; i < num;) {
                i++;
                dir = dir.getParentFile();
            }
        } else {
            dir = new File(JarFileUtil.toJarFilePath(url));
        }
        return dir;
    }

    // ===================================================================================
    //                                                                InputStream Handling
    //                                                                ====================
    public static void close(InputStream is) {
        if (is == null) {
            return;
        }
        try {
            is.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static final byte[] getBytes(InputStream is) {
        byte[] bytes = null;
        byte[] buf = new byte[8192];
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int n = 0;
            while ((n = is.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, n);
            }
            bytes = baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (is != null) {
                close(is);
            }
        }
        return bytes;
    }

    public static final void copy(InputStream is, OutputStream os) {
        byte[] buf = new byte[8192];
        try {
            int n = 0;
            while ((n = is.read(buf, 0, buf.length)) != -1) {
                os.write(buf, 0, n);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static int available(InputStream is) {
        try {
            return is.available();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
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
