package org.seasar.dbflute.properties;

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
import java.util.Properties;

import org.seasar.dbflute.util.DfStringUtil;

/**
 * @author jflute
 */
public final class DfAllClassCopyrightProperties extends DfAbstractHelperProperties {

    protected String _copyright;

    public DfAllClassCopyrightProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                                           Copyright
    //                                                                           =========
    public String getAllClassCopyright() {
        if (_copyright != null) {
            return _copyright;
        }
        String prop = stringProp("torque.allClassCopyright", "");

        final String sourceCodeLn = getBasicProperties().getSourceCodeLineSeparator();
        prop = DfStringUtil.replace(prop, "\r\n", "\n");
        prop = DfStringUtil.replace(prop, "\n", sourceCodeLn);

        _copyright = prop;
        return _copyright;
    }

    public void reflectAllExCopyright(String path) {
        final String copyright = getAllClassCopyright();
        if (copyright == null || copyright.trim().length() == 0) {
            return;
        }
        final File exfile = new File(path);
        final String encoding = getBasicProperties().getTemplateFileEncoding();
        final BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(exfile), encoding));
        } catch (UnsupportedEncodingException e) {
            String msg = "The encoding is unsupported: encoding=" + encoding;
            throw new IllegalStateException(msg, e);
        } catch (FileNotFoundException e) {
            String msg = "The file of extended class was NOT found: exfile=" + exfile;
            throw new IllegalStateException(msg, e);
        }
        final StringBuilder sb = new StringBuilder();
        final String sourceCodeLn = getBasicProperties().getSourceCodeLineSeparator();
        String lineString = null;
        int index = 0;
        try {
            while (true) {
                lineString = bufferedReader.readLine();
                if (lineString == null) {
                    break;
                }
                if (index == 0) { // first line
                    if (!lineString.trim().startsWith("package ")) {
                        return;
                    }
                    sb.append(copyright);
                }
                sb.append(lineString);
                sb.append(sourceCodeLn);
                ++index;
            }
        } catch (IOException e) {
            String msg = "bufferedReader.readLine() threw the exception: current line=" + lineString;
            throw new IllegalStateException(msg, e);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException ignored) {
            }
        }
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(exfile), encoding));
            bufferedWriter.write(sb.toString());
            bufferedWriter.flush();
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
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}