/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author jflute
 */
public class DfStringUtil {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String[] EMPTY_STRINGS = new String[0];

    // ===================================================================================
    //                                                                        Null & Empty
    //                                                                        ============
    public static final boolean isNullOrEmpty(final String text) {
        return text == null || text.length() == 0;
    }

    public static final boolean isNullOrTrimmedEmpty(final String text) {
        return text == null || text.trim().length() == 0;
    }

    public static final boolean isNotNullAndNotEmpty(final String text) {
        return !isNullOrEmpty(text);
    }

    public static final boolean isNotNullAndNotTrimmedEmpty(final String text) {
        return !isNullOrTrimmedEmpty(text);
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
    //                                                                                Trim
    //                                                                                ====
    public static final String rtrim(String text) {
        return rtrim(text, null);
    }

    public static final String rtrim(String text, String trimText) {
        if (text == null) {
            return null;
        }

        // for trim target same as String.trim()
        if (trimText == null) {
            final String notTrimmedString = "a";
            return (notTrimmedString + text).trim().substring(notTrimmedString.length());
        }

        // for original trim target
        int pos;
        for (pos = text.length() - 1; pos >= 0 && trimText.indexOf(text.charAt(pos)) >= 0; pos--)
            ;
        return text.substring(0, pos + 1);
    }

    // ===================================================================================
    //                                                                                Fill
    //                                                                                ====
    public static String rfill(String str, int size) {
        return doFill(str, size, false);
    }

    public static String lfill(String str, int size) {
        return doFill(str, size, true);
    }

    private static String doFill(String str, int size, boolean left) {
        if (str == null) {
            return null;
        }
        if (str.length() >= size) {
            return str;
        }
        final int addSize = size - str.length();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < addSize; i++) {
            sb.append(" ");
        }
        if (left) {
            return sb + str;
        } else {
            return str + sb;
        }
    }

    // ===================================================================================
    //                                                                       List Handling
    //                                                                       =============
    /**
     * @param str The split target string. (NotNull)
     * @param delimiter The delimiter for split. (NotNull)
     * @return The split list. (NotNull)
     */
    public static List<String> splitList(final String str, final String delimiter) {
        return doSplitList(str, delimiter, false);
    }

    /**
     * @param str The split target string. (NotNull)
     * @param delimiter The delimiter for split. (NotNull)
     * @return The split list that their elements is trimmed. (NotNull)
     */
    public static List<String> splitListTrimmed(final String str, final String delimiter) {
        return doSplitList(str, delimiter, true);
    }

    protected static List<String> doSplitList(final String str, final String delimiter, boolean trim) {
        final List<String> list = new ArrayList<String>();
        int i = 0;
        int j = str.indexOf(delimiter);
        for (int h = 0; j >= 0; h++) {
            final String element = str.substring(i, j);
            list.add(trim ? element.trim() : element);
            i = j + delimiter.length();
            j = str.indexOf(delimiter, i);
        }
        final String element = str.substring(i);
        list.add(trim ? element.trim() : element);
        return list;
    }

    public static boolean containsIgnoreCase(String target, List<String> strList) {
        if (target == null || strList == null) {
            return false;
        }
        for (String str : strList) {
            if (target.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                                    Initial Handling
    //                                                                    ================
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

    public static String initCapTrimmed(String str) {
        if (str == null) {
            return null;
        }
        str = str.trim();
        return initCap(str);
    }

    public static String initUncap(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return str;
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static String initUncapTrimmed(String str) {
        if (str == null) {
            return null;
        }
        str = str.trim();
        return initUncap(str);

    }

    // ===================================================================================
    //                                                                      Scope Handling
    //                                                                      ==============
    public static String extractFirstScope(String targetStr, String beginMark, String endMark) {
        if (targetStr == null || beginMark == null || endMark == null) {
            return null;
        }
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

    public static List<String> extractAllScope(String targetStr, String beginMark, String endMark) {
        if (targetStr == null || beginMark == null || endMark == null) {
            return new ArrayList<String>();
        }
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
    //                                                                       Name Handling
    //                                                                       =============
    public static String camelize(String decamelName) {
        final StringBuilder sb = new StringBuilder();
        final StringTokenizer tok = new StringTokenizer(decamelName, "_");
        while (tok.hasMoreTokens()) {
            String part = ((String) tok.nextElement());
            boolean allUpperCase = true;
            for (int i = 1; i < part.length(); ++i) {
                if (isLowerCase(part.charAt(i))) {
                    allUpperCase = false;
                }
            }
            if (allUpperCase) {
                part = part.toLowerCase();
            }
            sb.append(initCap(part));
        }
        return sb.toString();
    }

    public static String decamelize(String camelName) {
        if (camelName == null) {
            return null;
        }
        if (camelName.length() == 1) {
            return camelName.toUpperCase();
        }
        StringBuilder sb = new StringBuilder(40);
        int pos = 0;
        for (int i = 1; i < camelName.length(); ++i) {
            if (isUpperCase(camelName.charAt(i))) {
                if (sb.length() != 0) {
                    sb.append('_');
                }
                sb.append(camelName.substring(pos, i).toUpperCase());
                pos = i;
            }
        }
        if (sb.length() != 0) {
            sb.append('_');
        }
        sb.append(camelName.substring(pos, camelName.length()).toUpperCase());
        return sb.toString();
    }

    public static String toBeansPropertyName(String name) { // according to Java Beans rule
        if (name == null || name.length() == 0) {
            return name;
        }
        name = camelize(name);
        if (name.length() > 1 && isUpperCase(name.charAt(0), name.charAt(1))) {
            return name;
        }
        final char chars[] = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    private static boolean isUpperCase(char c) {
        return Character.isUpperCase(c);
    }

    private static boolean isUpperCase(char c1, char c2) {
        return isUpperCase(c1) && isUpperCase(c2);
    }

    private static boolean isLowerCase(char c) {
        return Character.isLowerCase(c);
    }

    // ===================================================================================
    //                                                                        SQL Handling
    //                                                                        ============
    public static String removeBlockComment(final String sql) {
        if (sql == null) {
            return null;
        }
        final String beginMark = "/*";
        final String endMark = "*/";
        final StringBuilder sb = new StringBuilder();
        String tmp = sql;
        while (true) {
            if (tmp.indexOf(beginMark) < 0) {
                sb.append(tmp);
                break;
            }
            if (tmp.indexOf(endMark) < 0) {
                sb.append(tmp);
                break;
            }
            if (tmp.indexOf(beginMark) > tmp.indexOf(endMark)) {
                final int borderIndex = tmp.indexOf(endMark) + endMark.length();
                sb.append(tmp.substring(0, borderIndex));
                tmp = tmp.substring(borderIndex);
                continue;
            }
            sb.append(tmp.substring(0, tmp.indexOf(beginMark)));
            tmp = tmp.substring(tmp.indexOf(endMark) + endMark.length());
        }
        return sb.toString();
    }

    public static String removeLineComment(final String sql) { // with removing CR!
        if (sql == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        final String[] lines = sql.split("\n");
        for (String line : lines) {
            if (line == null) {
                continue;
            }
            line = removeCR(line); // remove CR!
            if (line.startsWith("--")) {
                continue;
            }
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    private static String removeCR(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("\r", "");
    }
}
