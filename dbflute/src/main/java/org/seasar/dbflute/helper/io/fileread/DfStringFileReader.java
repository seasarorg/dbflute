package org.seasar.dbflute.helper.io.fileread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author jflute
 * @since 0.5.4 (2007/07/18)
 */
public class DfStringFileReader {

    // TODO: @jflute - staticじゃないように修正すること
    
    public static String readString(String path, String encoding) {
        final File file = new File(path);
        final StringBuilder sb = new StringBuilder();
        if (file.exists()) {
            java.io.FileInputStream fis = null;
            java.io.InputStreamReader ir = null;
            java.io.BufferedReader br = null;
            try {
                fis = new java.io.FileInputStream(file);
                ir = new java.io.InputStreamReader(fis, encoding);
                br = new java.io.BufferedReader(ir);

                int count = -1;
                while (true) {
                    ++count;

                    final String lineString = br.readLine();
                    if (lineString == null) {
                        break;
                    }
                    sb.append(lineString + System.getProperty("line.separator"));
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return sb.toString();
    }
}