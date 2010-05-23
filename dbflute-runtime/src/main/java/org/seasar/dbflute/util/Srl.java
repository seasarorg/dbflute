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
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * String Utility for Internal Programming of DBFlute.
 * @author jflute
 */
public class Srl {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String[] EMPTY_STRINGS = new String[0];

    // ===================================================================================
    //                                                                        Null & Empty
    //                                                                        ============
    public static final boolean is_Null_or_Empty(final String str) {
        return str == null || str.length() == 0;
    }

    public static final boolean is_Null_or_TrimmedEmpty(final String str) {
        return str == null || str.trim().length() == 0;
    }

    public static final boolean is_NotNull_and_NotEmpty(final String str) {
        return !is_Null_or_Empty(str);
    }

    public static final boolean is_NotNull_and_NotTrimmedEmpty(final String str) {
        return !is_Null_or_TrimmedEmpty(str);
    }

    public static final boolean isEmpty(final String str) {
        return str != null && str.length() == 0;
    }

    public static final boolean isTrimmedEmpty(final String str) {
        return str != null && str.trim().length() == 0;
    }

    // ===================================================================================
    //                                                                             Replace
    //                                                                             =======
    public static final String replace(String str, String fromStr, String toStr) {
        assertStringNotNull(str);
        assertFromStringNotNull(fromStr);
        assertToStringNotNull(toStr);
        final StringBuilder sb = new StringBuilder();
        int pos = 0;
        int pos2 = 0;
        do {
            pos = str.indexOf(fromStr, pos2);
            if (pos == 0) {
                sb.append(toStr);
                pos2 = fromStr.length();
            } else if (pos > 0) {
                sb.append(str.substring(pos2, pos));
                sb.append(toStr);
                pos2 = pos + fromStr.length();
            } else {
                sb.append(str.substring(pos2));
                return sb.toString();
            }
        } while (true);
    }

    public static final String replace(String str, Map<String, String> fromToMap) {
        assertStringNotNull(str);
        assertFromToMapNotNull(fromToMap);
        final Set<Entry<String, String>> entrySet = fromToMap.entrySet();
        for (Entry<String, String> entry : entrySet) {
            str = replace(str, entry.getKey(), entry.getValue());
        }
        return str;
    }

    // ===================================================================================
    //                                                                                Trim
    //                                                                                ====
    public static final String trim(String str) {
        return doTrim(str, null);
    }

    public static final String trim(String str, String trimStr) {
        return doTrim(str, trimStr);
    }

    public static final String ltrim(String str) {
        return doLTrim(str, null);
    }

    public static final String ltrim(String str, String trimStr) {
        return doLTrim(str, trimStr);
    }

    public static final String rtrim(String str) {
        return doRTrim(str, null);
    }

    public static final String rtrim(String str, String trimStr) {
        return doRTrim(str, trimStr);
    }

    protected static final String doTrim(String str, String trimStr) {
        return doRTrim(doLTrim(str, trimStr), trimStr);
    }

    protected static final String doLTrim(String str, String trimStr) {
        assertStringNotNull(str);

        // for trim target same as String.trim()
        if (trimStr == null) {
            final String notTrimmedString = "a";
            final String trimmed = (str + notTrimmedString).trim();
            return trimmed.substring(0, trimmed.length() - notTrimmedString.length());
        }

        // for original trim target
        int pos;
        for (pos = 0; pos < str.length() && trimStr.indexOf(str.charAt(pos)) >= 0; pos++)
            ;
        return str.substring(pos);
    }

    protected static final String doRTrim(String str, String trimStr) {
        assertStringNotNull(str);

        // for trim target same as String.trim()
        if (trimStr == null) {
            final String notTrimmedString = "a";
            return (notTrimmedString + str).trim().substring(notTrimmedString.length());
        }

        // for original trim target
        int pos;
        for (pos = str.length() - 1; pos >= 0 && trimStr.indexOf(str.charAt(pos)) >= 0; pos--)
            ;
        return str.substring(0, pos + 1);
    }

    // ===================================================================================
    //                                                                           SubString
    //                                                                           =========
    public static final String substringFirstFront(String str, String... delimiters) {
        assertStringNotNull(str);
        Integer firstIndex = null;
        for (String delimiter : delimiters) {
            final int currentIndex = str.indexOf(delimiter);
            if (currentIndex < 0) {
                continue;
            }
            if (firstIndex == null) {
                firstIndex = currentIndex;
            } else if (currentIndex >= 0 && firstIndex > currentIndex) {
                firstIndex = currentIndex;
            }
        }
        if (firstIndex == null || firstIndex < 0) {
            return str;
        }
        return str.substring(0, firstIndex);
    }

    public static final String substringFirstRear(String str, String... delimiters) {
        assertStringNotNull(str);
        Integer firstIndex = null;
        Integer delimiterLength = null;
        for (String delimiter : delimiters) {
            final int currentIndex = str.indexOf(delimiter);
            if (currentIndex < 0) {
                continue;
            }
            if (firstIndex == null) {
                firstIndex = currentIndex;
                delimiterLength = delimiter.length();
            } else if (currentIndex >= 0 && firstIndex > currentIndex) {
                firstIndex = currentIndex;
                delimiterLength = delimiter.length();
            }
        }
        if (firstIndex == null || firstIndex < 0) {
            return str;
        }
        return str.substring(firstIndex + delimiterLength);
    }

    public static final String substringLastFront(String str, String... delimiters) {
        assertStringNotNull(str);
        Integer lastIndex = null;
        for (String delimiter : delimiters) {
            final int currentIndex = str.lastIndexOf(delimiter);
            if (currentIndex < 0) {
                continue;
            }
            if (lastIndex == null) {
                lastIndex = currentIndex;
            } else if (currentIndex >= 0 && lastIndex < currentIndex) {
                lastIndex = currentIndex;
            }
        }
        if (lastIndex == null || lastIndex < 0) {
            return str;
        }
        return str.substring(0, lastIndex);
    }

    public static final String substringLastRear(String str, String... delimiters) {
        assertStringNotNull(str);
        Integer lastIndex = null;
        Integer delimiterLength = null;
        for (String delimiter : delimiters) {
            final int currentIndex = str.lastIndexOf(delimiter);
            if (currentIndex < 0) {
                continue;
            }
            if (lastIndex == null) {
                lastIndex = currentIndex;
                delimiterLength = delimiter.length();
            } else if (currentIndex >= 0 && lastIndex < currentIndex) {
                lastIndex = currentIndex;
                delimiterLength = delimiter.length();
            }
        }
        if (lastIndex == null || lastIndex < 0) {
            return str;
        }
        return str.substring(lastIndex + delimiterLength);
    }

    // ===================================================================================
    //                                                                             Connect
    //                                                                             =======
    public static final String connectPrefix(String str, String prefix, String delimiter) {
        assertStringNotNull(str);
        if (is_NotNull_and_NotTrimmedEmpty(prefix)) {
            str = prefix + delimiter + str;
        }
        return str;
    }

    public static final String connectSuffix(String str, String suffix, String delimiter) {
        assertStringNotNull(str);
        if (is_NotNull_and_NotTrimmedEmpty(suffix)) {
            str = str + delimiter + suffix;
        }
        return str;
    }

    // ===================================================================================
    //                                                                                Fill
    //                                                                                ====
    public static final String rfill(String str, int size) {
        return doFill(str, size, false);
    }

    public static final String lfill(String str, int size) {
        return doFill(str, size, true);
    }

    protected static final String doFill(String str, int size, boolean left) {
        assertStringNotNull(str);
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
    //                                                                  Quotation Handling
    //                                                                  ==================
    public static boolean isSingleQuoted(String str) {
        assertStringNotNull(str);
        return str.length() > 1 && str.startsWith("'") && str.endsWith("'");

    }

    public static boolean isDoubleQuoted(String str) {
        assertStringNotNull(str);
        return str.length() > 1 && str.startsWith("\"") && str.endsWith("\"");
    }

    public static String unquoteSingle(String str) {
        assertStringNotNull(str);
        if (!isSingleQuoted(str)) {
            return str;
        }
        return trim(str, "'");
    }

    public static String unquoteDouble(String str) {
        assertStringNotNull(str);
        if (!isDoubleQuoted(str)) {
            return str;
        }
        return trim(str, "\"");
    }

    // ===================================================================================
    //                                                                       List Handling
    //                                                                       =============
    /**
     * @param str The split target string. (NotNull)
     * @param delimiter The delimiter for split. (NotNull)
     * @return The split list. (NotNull)
     */
    public static final List<String> splitList(final String str, final String delimiter) {
        return doSplitList(str, delimiter, false);
    }

    /**
     * @param str The split target string. (NotNull)
     * @param delimiter The delimiter for split. (NotNull)
     * @return The split list that their elements is trimmed. (NotNull)
     */
    public static final List<String> splitListTrimmed(final String str, final String delimiter) {
        return doSplitList(str, delimiter, true);
    }

    protected static List<String> doSplitList(final String str, final String delimiter, boolean trim) {
        assertStringNotNull(str);
        assertDelimiterNotNull(delimiter);
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

    public static boolean containsIgnoreCase(List<String> strList, String str) {
        assertListStringNotNull(strList);
        assertStringNotNull(str);
        for (String element : strList) {
            if (str.equalsIgnoreCase(element)) {
                return true;
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                                      Scope Handling
    //                                                                      ==============
    public static final String extractFirstScope(String str, String beginMark, String endMark) {
        assertStringNotNull(str);
        assertBeginMarkNotNull(beginMark);
        assertEndMarkNotNull(endMark);
        final String ret;
        {
            String tmp = str;
            final int beginIndex = tmp.indexOf(beginMark);
            if (beginIndex < 0) {
                return null;
            }
            tmp = tmp.substring(beginIndex + beginMark.length());
            if (tmp.indexOf(endMark) < 0) {
                return null;
            }
            ret = tmp.substring(0, tmp.indexOf(endMark));
        }
        return ret;
    }

    public static final List<String> extractAllScope(String str, String beginMark, String endMark) {
        assertStringNotNull(str);
        assertBeginMarkNotNull(beginMark);
        assertEndMarkNotNull(endMark);
        final List<String> resultList = new ArrayList<String>();
        String tmp = str;
        while (true) {
            final int beginIndex = tmp.indexOf(beginMark);
            if (beginIndex < 0) {
                break;
            }
            tmp = tmp.substring(beginIndex + beginMark.length());
            if (tmp.indexOf(endMark) < 0) {
                break;
            }
            resultList.add(tmp.substring(0, tmp.indexOf(endMark)));
            tmp = tmp.substring(tmp.indexOf(endMark) + endMark.length());
        }
        return resultList;
    }

    // ===================================================================================
    //                                                                    Initial Handling
    //                                                                    ================
    public static String initCap(String str) {
        assertStringNotNull(str);
        if (is_Null_or_Empty(str)) {
            return str;
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        final char chars[] = str.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static String initCapTrimmed(String str) {
        assertStringNotNull(str);
        str = str.trim();
        return initCap(str);
    }

    public static String initUncap(String str) {
        assertStringNotNull(str);
        if (is_Null_or_Empty(str)) {
            return str;
        }
        if (str.length() == 1) {
            return str.toLowerCase();
        }
        final char chars[] = str.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    public static String initUncapTrimmed(String str) {
        assertStringNotNull(str);
        str = str.trim();
        return initUncap(str);
    }

    /**
     * Adjust initial character(s) as beans property. <br />
     * Basically same as initUncap() method except only when
     * it starts with two upper case character, for example 'EMecha'
     * @param resourceName The resource name for beans property that has'nt been adjusted yet. (NotNull)
     * @return The name as beans property that initial is adjusted. (NotNull)
     */
    public static String initBeansProp(String resourceName) { // according to Java Beans rule
        assertObjectNotNull("resourceName", resourceName);
        if (is_Null_or_TrimmedEmpty(resourceName)) {
            return resourceName;
        }
        if (isInitTwoUpperCase(resourceName)) { // for example 'EMecha'
            return resourceName;
        }
        return initUncap(resourceName);
    }

    public static boolean isInitUpperCase(String str) {
        assertStringNotNull(str);
        if (is_Null_or_Empty(str)) {
            return false;
        }
        return isUpperCase(str.charAt(0));
    }

    public static boolean isInitTwoUpperCase(String str) {
        assertStringNotNull(str);
        if (str.length() < 2) {
            return false;
        }
        return isUpperCase(str.charAt(0), str.charAt(1));
    }

    public static boolean isInitLowerCase(String str) {
        assertStringNotNull(str);
        if (is_Null_or_Empty(str)) {
            return false;
        }
        return isLowerCase(str.charAt(0));
    }

    public static boolean isInitTwoLowerCase(String str) {
        assertStringNotNull(str);
        if (str.length() < 2) {
            return false;
        }
        return isLowerCase(str.charAt(0), str.charAt(1));
    }

    // ===================================================================================
    //                                                                       Name Handling
    //                                                                       =============
    public static String camelize(String decamelName) {
        assertDecamelNameNotNull(decamelName);
        return doCamelize(decamelName, "_");
    }

    public static String camelize(String decamelName, String... delimiters) {
        assertDecamelNameNotNull(decamelName);
        String name = decamelName;
        for (String delimiter : delimiters) {
            name = doCamelize(name, delimiter);
        }
        return name;
    }

    protected static String doCamelize(String decamelName, String delimiter) {
        assertDecamelNameNotNull(decamelName);
        assertDelimiterNotNull(delimiter);
        if (is_Null_or_TrimmedEmpty(decamelName)) {
            return decamelName;
        }
        final StringBuilder sb = new StringBuilder();
        final List<String> splitList = splitListTrimmed(decamelName, delimiter);
        for (String part : splitList) {
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
        assertCamelNameNotNull(camelName);
        return doDecamelize(camelName, "_");
    }

    public static String decamelize(String camelName, String delimiter) {
        assertCamelNameNotNull(camelName);
        assertDelimiterNotNull(delimiter);
        return doDecamelize(camelName, delimiter);
    }

    protected static String doDecamelize(String camelName, String delimiter) {
        assertCamelNameNotNull(camelName);
        if (is_Null_or_TrimmedEmpty(camelName)) {
            return camelName;
        }
        if (camelName.length() == 1) {
            return camelName.toUpperCase();
        }
        final StringBuilder sb = new StringBuilder();
        int pos = 0;
        for (int i = 1; i < camelName.length(); ++i) {
            if (isUpperCase(camelName.charAt(i))) {
                if (sb.length() != 0) {
                    sb.append(delimiter);
                }
                sb.append(camelName.substring(pos, i).toUpperCase());
                pos = i;
            }
        }
        if (sb.length() != 0) {
            sb.append(delimiter);
        }
        sb.append(camelName.substring(pos, camelName.length()).toUpperCase());
        return sb.toString();
    }

    // ===================================================================================
    //                                                                        SQL Handling
    //                                                                        ============
    public static String removeBlockComment(final String sql) {
        assertObjectNotNull("sql", sql);
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
        assertObjectNotNull("sql", sql);
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

    protected static String removeCR(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("\r", "");
    }

    // ===================================================================================
    //                                                                  Character Handling
    //                                                                  ==================
    protected static boolean isUpperCase(char c) {
        return Character.isUpperCase(c);
    }

    protected static boolean isUpperCase(char c1, char c2) {
        return isUpperCase(c1) && isUpperCase(c2);
    }

    protected static boolean isLowerCase(char c) {
        return Character.isLowerCase(c);
    }

    protected static boolean isLowerCase(char c1, char c2) {
        return isLowerCase(c1) && isLowerCase(c2);
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected static void assertStringNotNull(String str) {
        assertObjectNotNull("str", str);
    }

    protected static void assertFromToMapNotNull(Map<String, String> fromToMap) {
        assertObjectNotNull("fromToMap", fromToMap);
    }

    protected static void assertDelimiterNotNull(String delimiter) {
        assertObjectNotNull("delimiter", delimiter);
    }

    protected static void assertFromStringNotNull(String fromStr) {
        assertObjectNotNull("fromStr", fromStr);
    }

    protected static void assertToStringNotNull(String toStr) {
        assertObjectNotNull("toStr", toStr);
    }

    protected static void assertBeginMarkNotNull(String beginMark) {
        assertObjectNotNull("beginMark", beginMark);
    }

    protected static void assertEndMarkNotNull(String endMark) {
        assertObjectNotNull("endMark", endMark);
    }

    protected static void assertListStringNotNull(List<String> strList) {
        assertObjectNotNull("strList", strList);
    }

    protected static void assertDecamelNameNotNull(String decamelName) {
        assertObjectNotNull("decamelName", decamelName);
    }

    protected static void assertCamelNameNotNull(String camelName) {
        assertObjectNotNull("camelName", camelName);
    }

    /**
     * Assert that the object is not null.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     * @exception IllegalArgumentException
     */
    protected static void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Assert that the entity is not null and not trimmed empty.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     */
    protected static void assertStringNotNullAndNotTrimmedEmpty(String variableName, String value) {
        assertObjectNotNull("variableName", variableName);
        assertObjectNotNull("value", value);
        if (value.trim().length() == 0) {
            String msg = "The value should not be empty: variableName=" + variableName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
    }
}
