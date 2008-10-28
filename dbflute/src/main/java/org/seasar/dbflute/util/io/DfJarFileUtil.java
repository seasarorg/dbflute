package org.seasar.dbflute.util.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.seasar.dbflute.util.net.DfURLUtil;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public abstract class DfJarFileUtil {

    public static JarFile create(final String file) {
        try {
            return new JarFile(file);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static JarFile create(final File file) {
        try {
            return new JarFile(file);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static InputStream getInputStream(final JarFile file, final ZipEntry entry) {
        try {
            return file.getInputStream(entry);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static JarFile toJarFile(final URL jarUrl) {
        final URLConnection conn = DfURLUtil.openConnection(jarUrl);
        if (conn instanceof JarURLConnection) {
            try {
                return ((JarURLConnection) conn).getJarFile();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return create(new File(toJarFilePath(jarUrl)));
    }

    public static String toJarFilePath(final URL jarUrl) {
        final URL nestedUrl = DfURLUtil.create(jarUrl.getPath());
        final String nestedUrlPath = nestedUrl.getPath();
        final int pos = nestedUrlPath.lastIndexOf('!');
        final String jarFilePath = nestedUrlPath.substring(0, pos);
        final File jarFile = new File(DfURLUtil.decode(jarFilePath, "UTF8"));
        return DfFileUtil.getCanonicalPath(jarFile);
    }

    public static void close(final JarFile jarFile) {
        try {
            jarFile.close();
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }
}