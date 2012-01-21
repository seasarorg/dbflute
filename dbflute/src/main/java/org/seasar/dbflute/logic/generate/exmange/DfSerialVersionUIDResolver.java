package org.seasar.dbflute.logic.generate.exmange;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class DfSerialVersionUIDResolver {

    protected String _sourceEncoding;
    protected String _sourceLn;

    public DfSerialVersionUIDResolver(String sourceEncoding, String sourceLn) {
        _sourceEncoding = sourceEncoding;
        _sourceLn = sourceLn;
    }

    public void reflectAllExSerialUID(String path) {
        final String serialComment = "/** Serial version UID. (Default) */";
        final String serialDefinition = "private static final long serialVersionUID = 1L;";
        final File exfile = new File(path);
        final String encoding = _sourceEncoding;
        final BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(exfile), encoding));
        } catch (UnsupportedEncodingException e) {
            String msg = "The encoding is unsupported: encoding=" + encoding;
            throw new IllegalStateException(msg, e);
        } catch (FileNotFoundException e) {
            String msg = "The file of extended class was NOT found: exfile=" + exfile;
            throw new IllegalStateException(msg, e);
        }
        final StringBuilder sb = new StringBuilder();
        final String sourceCodeLn = _sourceLn;
        String line = null;
        try {
            while (true) {
                line = br.readLine();
                if (line == null) {
                    break;
                }
                if (line.contains("serialVersionUID")) {
                    return;
                }
                sb.append(line).append(sourceCodeLn);
                if (line.startsWith("public class") && line.contains(" extends ") && line.endsWith("{")) {
                    sb.append(sourceCodeLn); // for empty line
                    sb.append("    " + serialComment).append(sourceCodeLn);
                    sb.append("    " + serialDefinition).append(sourceCodeLn);
                }
            }
        } catch (IOException e) {
            String msg = "bufferedReader.readLine() threw the exception: current line=" + line;
            throw new IllegalStateException(msg, e);
        } finally {
            try {
                br.close();
            } catch (IOException ignored) {
            }
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(exfile), encoding));
            bw.write(sb.toString());
            bw.flush();
        } catch (UnsupportedEncodingException e) {
            String msg = "The encoding is unsupported: encoding=" + encoding;
            throw new IllegalStateException(msg, e);
        } catch (FileNotFoundException e) {
            String msg = "The file of base behavior was not found: bsbhvFile=" + exfile;
            throw new IllegalStateException(msg, e);
        } catch (IOException e) {
            String msg = "bufferedWriter.write() threw the exception: bsbhvFile=" + exfile;
            throw new IllegalStateException(msg, e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
