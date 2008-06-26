package org.seasar.dbflute.util;

import java.util.List;

public abstract class DfCompareUtil {

    public static boolean containsIgnoreCase(String target, List<String> strList) {
        for (String str : strList) {
            if (target.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }
}