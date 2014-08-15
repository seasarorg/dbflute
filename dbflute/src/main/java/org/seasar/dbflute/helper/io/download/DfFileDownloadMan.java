package org.seasar.dbflute.helper.io.download;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author jflute
 * @since 1.0.5K (2014/08/15 Friday)
 */
public class DfFileDownloadMan {

    public void download(String urlString, String outputFilename) {
        final URL url = createURL(urlString);
        doDownloadFileAndClose(url, outputFilename);
    }

    protected URL createURL(String urlString) {
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            String msg = "new URL(urlString) threw the IO exception: urlString=" + urlString;
            throw new IllegalStateException(msg, e);
        }
    }

    protected void doDownloadFileAndClose(URL url, String outputFilename) {
        InputStream in;
        try {
            in = url.openStream();
        } catch (IOException e) {
            String msg = "URL#openStream() threw the IO exception: url=" + url;
            throw new IllegalStateException(msg, e);
        }
        doDownloadFileAndClose(in, outputFilename);
    }

    protected void doDownloadFileAndClose(InputStream in, String outputFilename) {
        final byte[] bytes;
        try {
            bytes = toBytes(in);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                String msg = "InputStream#close() threw the IO exception: in=" + in;
                throw new IllegalStateException(msg, e);
            }
        }
        final File outputFile = new File(outputFilename);
        final FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(outputFile, false);
        } catch (FileNotFoundException e) {
            String msg = "Not found the file: outputFilename=" + outputFilename;
            throw new IllegalStateException(msg, e);
        }
        try {
            fileOutputStream.write(bytes);
        } catch (IOException e) {
            String msg = "FileOutputStream#write() threw the IO exception: outputFilename=" + outputFilename;
            throw new IllegalStateException(msg, e);
        }
    }

    protected byte[] toBytes(InputStream in) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            copy(in, out);
        } catch (IOException e) {
            String msg = "this#copy() threw the IO exception: in=" + in;
            throw new IllegalStateException(msg, e);
        }
        return out.toByteArray();
    }

    protected void copy(InputStream in, OutputStream out) throws IOException {
        final byte[] bytes = new byte[256];
        int len = in.read(bytes);
        while (len != -1 && len != 0) {
            out.write(bytes, 0, len);
            len = in.read(bytes);
        }
    }
}
