package org.seasar.dbflute.util.basic;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.seasar.dbflute.util.crypto.DfBase64Util;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public abstract class DfStringUtil {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String[] EMPTY_STRINGS = new String[0];

    public static final int WAVE_DASH = 0x301c;

    public static final int FULLWIDTH_TILDE = 0xff5e;

    // ===================================================================================
    //                                                                               Basic
    //                                                                               =====
    public static String replace(String text, String fromText, String toText) {
        if (text == null || fromText == null || toText == null) {
            return null;
        }
        StringBuffer buf = new StringBuffer(100);
        int pos = 0;
        int pos2 = 0;
        while (true) {
            pos = text.indexOf(fromText, pos2);
            if (pos == 0) {
                buf.append(toText);
                pos2 = fromText.length();
            } else if (pos > 0) {
                buf.append(text.substring(pos2, pos));
                buf.append(toText);
                pos2 = pos + fromText.length();
            } else {
                buf.append(text.substring(pos2));
                break;
            }
        }
        return buf.toString();
    }

    public static final boolean isEmpty(String text) {
        return text == null || text.length() == 0;
    }

    public static String[] split(final String str, final String delim) {
        if (isEmpty(str)) {
            return EMPTY_STRINGS;
        }
        List<String> list = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(str, delim);
        while (st.hasMoreElements()) {
            list.add((String) st.nextElement());
        }
        return list.toArray(new String[list.size()]);
    }

    public static final String rtrim(String text) {
        return rtrim(text, null);
    }

    public static final String rtrim(String text, String trimText) {
        if (text == null)
            return null;
        if (trimText == null)
            trimText = " ";
        int pos;
        for (pos = text.length() - 1; pos >= 0 && trimText.indexOf(text.charAt(pos)) >= 0; pos--)
            ;
        return text.substring(0, pos + 1);
    }

    // ===================================================================================
    //                                                                      Begin End Mark
    //                                                                      ==============
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

    // ===================================================================================
    //                                                                             Initial
    //                                                                             =======
    public static String initCap(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return str;
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
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
    
    // ===================================================================================
    //                                                                         List String
    //                                                                         ===========
    public static boolean containsIgnoreCase(String target, List<String> strList) {
        for (String str : strList) {
            if (target.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                                           To String
    //                                                                           =========
    public static String toString(Object value) {
        return toString(value, null);
    }

    public static String toString(Object value, String pattern) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return (String) value;
        } else if (value instanceof java.util.Date) {
            return toString((java.util.Date) value, pattern);
        } else if (value instanceof Number) {
            return toString((Number) value, pattern);
        } else if (value instanceof byte[]) {
            return DfBase64Util.encode((byte[]) value);
        } else {
            return value.toString();
        }
    }

    public static String toString(Number value, String pattern) {
        if (value != null) {
            if (pattern != null) {
                return new DecimalFormat(pattern).format(value);
            }
            return value.toString();
        }
        return null;
    }

    public static String toString(java.util.Date value, String pattern) {
        if (value != null) {
            if (pattern != null) {
                return new SimpleDateFormat(pattern).format(value);
            }
            return value.toString();
        }
        return null;
    }

    // ===================================================================================
    //                                                                    Pinpoint Replace
    //                                                                    ================
    public static String fromWaveDashToFullwidthTilde(String source) {
        if (source == null) {
            return null;
        }
        StringBuffer result = new StringBuffer(source.length());
        char ch;
        for (int i = 0; i < source.length(); i++) {
            ch = source.charAt(i);
            switch (ch) {
            case WAVE_DASH:
                ch = FULLWIDTH_TILDE;
                break;
            default:
                break;
            }
            result.append(ch);
        }
        return result.toString();
    }
}