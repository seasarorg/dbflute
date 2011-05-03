package org.seasar.dbflute.helper.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/05/03 Tuesday)
 */
public class ProcessConsoleReader extends Thread {

    protected final BufferedReader _reader;
    protected final StringBuilder _consoleSb = new StringBuilder();

    public ProcessConsoleReader(InputStream ins, String encoding) {
        encoding = encoding != null ? encoding : "UTF-8";
        try {
            _reader = new BufferedReader(new InputStreamReader(ins, encoding));
        } catch (UnsupportedEncodingException e) {
            String msg = "Failed to create a reader by the encoding: " + encoding;
            throw new IllegalStateException(msg);
        }
    }

    public String read() {
        return _consoleSb.toString();
    }

    @Override
    public void run() {
        final StringBuilder sb = _consoleSb;
        try {
            while (true) {
                final String line = _reader.readLine();
                if (line == null) {
                    break;
                }
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(line);
            }
        } catch (IOException e) {
            String msg = "Failed to read the stream: " + _reader;
            throw new IllegalStateException(msg, e);
        } finally {
            try {
                _reader.close();
            } catch (IOException ignored) {
            }
        }
    }
}
