package org.seasar.dbflute.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.seasar.dbflute.helper.mapstring.DfMapListStringImpl;

/**
 * @author mkubo
 */
public class DfMapStringFileUtil {

    public static Map<String, String> getSimpleMap(String path, String encoding) {
        final List<String> defaultSysdateList = new ArrayList<String>();
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

                    String lineString = br.readLine();
                    if (lineString == null || lineString.trim().length() == 0) {
                        break;
                    }
                    sb.append(lineString);
                    defaultSysdateList.add(lineString);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        final DfMapListStringImpl mapListString = new DfMapListStringImpl();
        final Map<String, String> resultMap = new LinkedHashMap<String, String>();
        if (sb.toString().trim().length() != 0) {
            final Map<String, Object> map = mapListString.generateMap(sb.toString());
            final Set<String> keySet = map.keySet();
            for (String key : keySet) {
                resultMap.put(key, (String) map.get(key));
            }
        }
        return resultMap;
    }
}