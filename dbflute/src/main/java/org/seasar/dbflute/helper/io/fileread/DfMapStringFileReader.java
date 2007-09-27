package org.seasar.dbflute.helper.io.fileread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.seasar.dbflute.helper.mapstring.DfMapListStringImpl;

/**
 * @author jflute
 */
public class DfMapStringFileReader {

    // TODO: @jflute - staticじゃないように修正すること

    public static Map<String, Object> readMap(String path, String encoding) {
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
                    if (lineString.trim().length() == 0) {
                        continue;
                    }
                    if (lineString.trim().startsWith("#")) {// If the line is comment...
                        continue;
                    }
                    sb.append(lineString);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (sb.toString().trim().length() == 0) {
            return new LinkedHashMap<String, Object>();
        }
        final DfMapListStringImpl mapListString = new DfMapListStringImpl();
        return mapListString.generateMap(sb.toString());
    }

    public static Map<String, String> readMapAsStringValue(String path, String encoding) {
        final Map<String, String> resultMap = new LinkedHashMap<String, String>();
        final Map<String, Object> map = readMap(path, encoding);
        final Set<String> keySet = map.keySet();
        for (String key : keySet) {
            resultMap.put(key, (String) map.get(key));
        }
        return resultMap;
    }

    public static Map<String, java.util.List<String>> readMapAsListStringValue(String path, String encoding) {
        final Map<String, java.util.List<String>> resultMap = new LinkedHashMap<String, java.util.List<String>>();
        final Map<String, Object> map = readMap(path, encoding);
        final Set<String> keySet = map.keySet();
        for (String key : keySet) {
            resultMap.put(key, (java.util.List<String>) map.get(key));
        }
        return resultMap;
    }
}