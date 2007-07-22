package org.seasar.dbflute.util;

import java.util.ArrayList;
import java.util.List;

import org.seasar.framework.util.StringUtil;

public abstract class DfStringUtil {

    public static String replace(String text, String fromText, String toText) {
        return StringUtil.replace(text, fromText, toText);
    }

    public static String getStringBetweenBeginEndMark(String targetStr, String beginMark, String endMark) {
        final String ret;
        {
            String tmp = targetStr;
            final int startIndex = tmp.indexOf(beginMark);
            if (startIndex < 0) {
                return null;
            }
            tmp = tmp.substring(startIndex + beginMark.length());
            if (tmp.indexOf(endMark) < 0) {
                return null;
            }
            ret = tmp.substring(0, tmp.indexOf(endMark)).trim();
        }
        return ret;
    }

    public static List<String> getListBetweenBeginEndMark(String targetStr, String beginMark, String endMark) {
        final List<String> resultList = new ArrayList<String>();
        String tmp = targetStr;
        while (true) {
            final int startIndex = tmp.indexOf(beginMark);
            if (startIndex < 0) {
                break;
            }
            tmp = tmp.substring(startIndex + beginMark.length());
            if (tmp.indexOf(endMark) < 0) {
                break;
            }
            resultList.add(tmp.substring(0, tmp.indexOf(endMark)).trim());
            tmp = tmp.substring(tmp.indexOf(endMark) + endMark.length());
        }
        return resultList;
    }

    public static String initCapAfterTrimming(String str) {
        if (str == null) {
            return null;
        }
        str = str.trim();
        if (str.length() == 0) {
            return str;
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}