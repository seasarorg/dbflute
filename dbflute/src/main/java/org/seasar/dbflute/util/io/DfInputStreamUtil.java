package org.seasar.dbflute.util.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author jflute
 * @since 0.1.0 (2007/09/19 Wednesday)
 */
public class DfInputStreamUtil {

    public static void makeFileAndClose(InputStream in, String outputFilename) {
        final byte[] bytes;
        try {
            bytes = toBytes(in);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                String msg = InputStream.class.getSimpleName() + "#close() threw the IO exception!";
                msg = msg + ": outputFilename=" + outputFilename;
                throw new IllegalStateException(msg, e);
            }
        }

        final File outputFile = new File(outputFilename);
        final FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(outputFile, false);
        } catch (FileNotFoundException e) {
            String msg = "new FileOutputStream(outputFile, false) threw the " + e.getClass().getSimpleName();
            msg = msg + ": outputFilename=" + outputFilename;
            throw new IllegalStateException(msg, e);
        }
        try {
            fileOutputStream.write(bytes);
        } catch (IOException e) {
            String msg = "fileOutputStream.write(toBytes) threw the " + e.getClass().getSimpleName();
            msg = msg + ": outputFilename=" + outputFilename;
            throw new IllegalStateException(msg, e);
        }
    }

    public static byte[] toBytes(InputStream in) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            copy(in, out);
        } catch (IOException e) {
            String msg = DfInputStreamUtil.class.getSimpleName() + "#copy() threw the IO exception!";
            throw new IllegalStateException(msg, e);
        }
        return out.toByteArray();
    }

    protected static void copy(InputStream in, OutputStream out) throws IOException {
        final byte[] buff = new byte[256];
        int len = in.read(buff);
        while (len != -1 && len != 0) {
            out.write(buff, 0, len);
            len = in.read(buff);
        }
    }
}
