package org.seasar.dbflute.util.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public abstract class DfStreamUtil {

    public static FileInputStream createFileInputStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static FileOutputStream createFileOutputStream(File file) {
        try {
            return new FileOutputStream(file);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static final byte[] getBytes(InputStream ins) {
        byte[] bytes = null;
        byte[] buf = new byte[8192];
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int n = 0;
            while ((n = ins.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, n);
            }
            bytes = baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (ins != null) {
                close(ins);
            }
        }
        return bytes;
    }

    public static void close(InputStream ins) {
        if (ins == null) {
            return;
        }
        try {
            ins.close();
        } catch (IOException ignored) {
        }
    }

    public static void close(OutputStream ins) {
        if (ins == null) {
            return;
        }
        try {
            ins.close();
        } catch (IOException ignored) {
        }
    }
}