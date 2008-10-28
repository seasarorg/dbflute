package org.seasar.dbflute.util.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public abstract class DfFileUtil {

    public static String getCanonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static URL toURL(final File file) {
        try {
            return file.toURL();
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static byte[] getBytes(File file) {
        return DfStreamUtil.getBytes(DfStreamUtil.createFileInputStream(file));
    }

    public static void copy(File src, File dest) {
        if (dest.canWrite() == false || (dest.exists() && dest.canWrite() == false)) {
            return;
        }
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(DfStreamUtil.createFileInputStream(src));
            out = new BufferedOutputStream(DfStreamUtil.createFileOutputStream(dest));
            byte[] buf = new byte[1024];
            int length;
            while (-1 < (length = in.read(buf))) {
                out.write(buf, 0, length);
                out.flush();
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            DfStreamUtil.close(in);
            DfStreamUtil.close(out);
        }
    }

    public static void write(String path, byte[] data) {
        if (path == null) {
            throw new NullPointerException("path");
        }
        if (data == null) {
            throw new NullPointerException("data");
        }
        write(path, data, 0, data.length);
    }

    public static void write(String path, byte[] data, int offset, int length) {
        if (path == null) {
            throw new NullPointerException("path");
        }
        if (data == null) {
            throw new NullPointerException("data");
        }
        try {
            OutputStream out = new BufferedOutputStream(new FileOutputStream(path));
            try {
                out.write(data, offset, length);
            } finally {
                out.close();
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}