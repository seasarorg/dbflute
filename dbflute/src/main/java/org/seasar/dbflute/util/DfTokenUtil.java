package org.seasar.dbflute.util;

import java.util.ArrayList;
import java.util.List;

public class DfTokenUtil {

    public static String[] tokenToArgs(String value, String delimiter) {
        List<String> list = tokenToList(value, delimiter);
        return (String[]) list.toArray(new String[list.size()]);
    }

    public static List<String> tokenToList(String value, String delimiter) {
        List<String> list = new ArrayList<String>();
        int i = 0;
        int j = value.indexOf(delimiter);
        for (int h = 0; j >= 0; h++) {
            list.add(value.substring(i, j));
            i = j + 1;
            j = value.indexOf(delimiter, i);
        }
        list.add(value.substring(i));
        return list;
    }
}
