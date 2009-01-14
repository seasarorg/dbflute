package org.seasar.dbflute.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * {Refers to S2Container's utility and Extends it}
 * @author jflute
 */
public class DfStringUtil {

    protected static final String[] EMPTY_STRINGS = new String[0];
    
    // ===================================================================================
    //                                                                               Empty
    //                                                                               =====
    public static final boolean isEmpty(final String text) {
        return text == null || text.length() == 0;
    }

    public static final boolean isNotEmpty(final String text) {
        return !isEmpty(text);
    }
    
    // ===================================================================================
    //                                                                             Replace
    //                                                                             =======
    public static String replace(String text, String fromText, String toText) {
        if (text == null || fromText == null || toText == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        int pos2 = 0;
        do {
            pos = text.indexOf(fromText, pos2);
            if (pos == 0) {
                sb.append(toText);
                pos2 = fromText.length();
            } else if (pos > 0) {
                sb.append(text.substring(pos2, pos));
                sb.append(toText);
                pos2 = pos + fromText.length();
            } else {
                sb.append(text.substring(pos2));
                return sb.toString();
            }
        } while (true);
    }

    // ===================================================================================
    //                                                                               Split
    //                                                                               =====
    public static String[] split(final String str, final String delimiter) {
        if (str == null || str.trim().length() == 0) {
            return EMPTY_STRINGS;
        }
        final List<String> list = new ArrayList<String>();
        final StringTokenizer st = new StringTokenizer(str, delimiter);
        while (st.hasMoreElements()) {
            list.add(st.nextToken());
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    // ===================================================================================
    //                                                                     Initial Convert
    //                                                                     ===============
    public static String initCap(String str) {
        assertObjectNotNull("str", str);
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String initUncap(String str) {
        assertObjectNotNull("str", str);
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    // ===================================================================================
    //                                                                      Naming Convert
    //                                                                      ==============
    public static String fromPropertyNameToColumnName(String propertyName) {
        return decamelize(propertyName);
    }

    public static String fromEntityNameToTableName(String entityName) {
        return decamelize(entityName);
    }

    protected static String decamelize(String s) {
        if (s == null) {
            return null;
        }
        if (s.length() == 1) {
            return s.toUpperCase();
        }
        StringBuffer buf = new StringBuffer(40);
        int pos = 0;
        for (int i = 1; i < s.length(); ++i) {
            if (Character.isUpperCase(s.charAt(i))) {
                if (buf.length() != 0) {
                    buf.append('_');
                }
                buf.append(s.substring(pos, i).toUpperCase());
                pos = i;
            }
        }
        if (buf.length() != 0) {
            buf.append('_');
        }
        buf.append(s.substring(pos, s.length()).toUpperCase());
        return buf.toString();
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    /**
     * Assert that the object is not null.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     * @exception IllegalArgumentException
     */
    protected static void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=" + variableName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }
}
