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
    //                                                                             IndexOf
    //                                                                             =======
    public static IndexOfInfo indexOfFirstFront(final String str, final String... delimiters) {
        int minIndex = -1;
        String targetDelimiter = null;
        for (String delimiter : delimiters) {
            final int index = str.indexOf(delimiter);
            if (index < 0) {
                continue;
            }
            if (minIndex < 0 || minIndex > index) {
                minIndex = index;
                targetDelimiter = delimiter;
            }
        }
        final IndexOfInfo info = new IndexOfInfo();
        info.setBaseString(str);
        info.setIndex(minIndex);
        info.setDelimiter(targetDelimiter);
        return info;
    }

    public static IndexOfInfo indexOfFirstRear(final String str, final String... delimiters) {
        int maxIndex = -1;
        String targetDelimiter = null;
        for (String delimiter : delimiters) {
            final int index = str.indexOf(delimiter);
            if (index < 0) {
                continue;
            }
            if (maxIndex < 0 || maxIndex < index) {
                maxIndex = index;
                targetDelimiter = delimiter;
            }
        }
        final IndexOfInfo info = new IndexOfInfo();
        info.setBaseString(str);
        info.setIndex(maxIndex);
        info.setDelimiter(targetDelimiter);
        return info;
    }

    public static IndexOfInfo indexOfLastFront(final String str, final String... delimiters) {
        int minIndex = -1;
        String targetDelimiter = null;
        for (String delimiter : delimiters) {
            final int index = str.lastIndexOf(delimiter);
            if (index < 0) {
                continue;
            }
            if (minIndex < 0 || minIndex > index) {
                minIndex = index;
                targetDelimiter = delimiter;
            }
        }
        final IndexOfInfo info = new IndexOfInfo();
        info.setBaseString(str);
        info.setIndex(minIndex);
        info.setDelimiter(targetDelimiter);
        return info;
    }

    public static IndexOfInfo indexOfLastRear(final String str, final String... delimiters) {
        int maxIndex = -1;
        String targetDelimiter = null;
        for (String delimiter : delimiters) {
            final int index = str.lastIndexOf(delimiter);
            if (index < 0) {
                continue;
            }
            if (maxIndex < 0 || maxIndex < index) {
                maxIndex = index;
                targetDelimiter = delimiter;
            }
        }
        final IndexOfInfo info = new IndexOfInfo();
        info.setBaseString(str);
        info.setIndex(maxIndex);
        info.setDelimiter(targetDelimiter);
        return info;
    }

    public static class IndexOfInfo {
        protected String _baseString;
        protected int _index;
        protected String _delimiter;

        public String getBaseString() {
            return _baseString;
        }

        public void setBaseString(String baseStr) {
            this._baseString = baseStr;
        }

        public int getIndex() {
            return _index;
        }

        public void setIndex(int index) {
            this._index = index;
        }

        public String getDelimiter() {
            return _delimiter;
        }

        public void setDelimiter(String delimiter) {
            this._delimiter = delimiter;
        }
    }

    // ===================================================================================
    //                                                                           SubString
    //                                                                           =========
    /**
     * Extract front sub-string from first index of delimiter.
     * <pre>
     * substringFirstFront("foo.bar.baz", ".")
     * returns "foo"
     * </pre>
     * @param str The target string. (NotNull)
     * @param delimiters The array of delimiters. (NotNull) 
     * @return The part of string. (NotNull: if delimiter not found, returns argument-plain string)
     */
    public static final String substringFirstFront(final String str, final String... delimiters) {
        assertStringNotNull(str);
        final IndexOfInfo info = indexOfFirstFront(str, delimiters);
        final int firstIndex = info.getIndex();
        if (firstIndex < 0) {
            return str;
        }
        return str.substring(0, firstIndex);
    }

    /**
     * Extract rear sub-string from first index of delimiter.
     * <pre>
     * substringFirstRear("foo.bar.baz", ".")
     * returns "bar.baz"
     * </pre>
     * @param str The target string. (NotNull)
     * @param delimiters The array of delimiters. (NotNull) 
     * @return The part of string. (NotNull: if delimiter not found, returns argument-plain string)
     */
    public static final String substringFirstRear(String str, String... delimiters) {
        assertStringNotNull(str);
        final IndexOfInfo info = indexOfFirstRear(str, delimiters);
        final int firstIndex = info.getIndex();
        if (firstIndex < 0) {
            return str;
        }
        return str.substring(firstIndex + info.getDelimiter().length());
    }

    /**
     * Extract front sub-string from last index of delimiter.
     * <pre>
     * substringLastFront("foo.bar.baz", ".")
     * returns "foo.bar"
     * </pre>
     * @param str The target string. (NotNull)
     * @param delimiters The array of delimiters. (NotNull) 
     * @return The part of string. (NotNull: if delimiter not found, returns argument-plain string)
     */
    public static final String substringLastFront(String str, String... delimiters) {
        assertStringNotNull(str);
        final IndexOfInfo info = indexOfLastFront(str, delimiters);
        final int lastIndex = info.getIndex();
        if (lastIndex < 0) {
            return str;
        }
        return str.substring(0, lastIndex);
    }

    /**
     * Extract rear sub-string from rear index of delimiter.
     * <pre>
     * substringLastRear("foo.bar.baz", ".")
     * returns "baz"
     * </pre>
     * @param str The target string. (NotNull)
     * @param delimiters The array of delimiters. (NotNull) 
     * @return The part of string. (NotNull: if delimiter not found, returns argument-plain string)
     */
    public static final String substringLastRear(String str, String... delimiters) {
        assertStringNotNull(str);
        final IndexOfInfo info = indexOfLastRear(str, delimiters);
        final int lastIndex = info.getIndex();
        if (lastIndex < 0) {
            return str;
        }
        return str.substring(lastIndex + info.getDelimiter().length());
    }

    // ===================================================================================
    //                                                                               Split
    //                                                                               =====
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

    public static final String replaceScopeContent(String str, String fromStr, String toStr, String beginMark,
            String endMark) {
        final List<ScopeInfo> scopeList = extractScopeList(str, beginMark, endMark);
        if (scopeList.isEmpty()) {
            return str;
        }
        return scopeList.get(0).replaceContentOnBaseString(fromStr, toStr);
    }

    public static final String replaceScopeInterspace(String str, String fromStr, String toStr, String beginMark,
            String endMark) {
        final List<ScopeInfo> scopeList = extractScopeList(str, beginMark, endMark);
        if (scopeList.isEmpty()) {
            return str;
        }
        return scopeList.get(0).replaceInterspaceOnBaseString(fromStr, toStr);
    }

    // ===================================================================================
    //                                                                            Contains
    //                                                                            ========
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
    //                                                                               Count
    //                                                                               =====
    public static int count(String str, String element) {
        int count = 0;
        while (true) {
            final int index = str.indexOf(element);
            if (index < 0) {
                break;
            }
            str = str.substring(index + element.length());
            ++count;
        }
        return count;
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
    //                                                                  Delimiter Handling
    //                                                                  ==================
    public static final List<DelimiterInfo> extractDelimiterList(final String str, final String delimiter) {
        assertStringNotNull(str);
        assertDelimiterNotNull(delimiter);
        final List<DelimiterInfo> delimiterList = new ArrayList<DelimiterInfo>();
        DelimiterInfo previous = null;
        String rear = str;
        while (true) {
            final int beginIndex = rear.indexOf(delimiter);
            if (beginIndex < 0) {
                break;
            }
            final DelimiterInfo info = new DelimiterInfo();
            info.setBaseString(str);
            info.setDelimiter(delimiter);
            final int absoluteIndex = (previous != null ? previous.getEndIndex() : 0) + beginIndex;
            info.setBeginIndex(absoluteIndex);
            info.setEndIndex(absoluteIndex + delimiter.length());
            if (previous != null) {
                info.setPrevious(previous);
                previous.setNext(info);
            }
            delimiterList.add(info);
            previous = info;
            rear = str.substring(info.getEndIndex());
            continue;
        }
        return delimiterList;
    }

    public static class DelimiterInfo {
        protected String _baseString;
        protected int _beginIndex;
        protected int _endIndex;
        protected String _delimiter;
        protected DelimiterInfo _previous;
        protected DelimiterInfo _next;

        public String substringInterspaceToPrevious() {
            int previousIndex = -1;
            if (_previous != null) {
                previousIndex = _previous.getBeginIndex();
            }
            if (previousIndex >= 0) {
                return _baseString.substring(previousIndex + _previous.getDelimiter().length(), _beginIndex);
            } else {
                return _baseString.substring(0, _beginIndex);
            }
        }

        public String substringInterspaceToNext() {
            int nextIndex = -1;
            if (_next != null) {
                nextIndex = _next.getBeginIndex();
            }
            if (nextIndex >= 0) {
                return _baseString.substring(_endIndex, nextIndex);
            } else {
                return _baseString.substring(_endIndex);
            }
        }

        @Override
        public String toString() {
            return _delimiter + ":(" + _beginIndex + ", " + _endIndex + ")";
        }

        public String getBaseString() {
            return _baseString;
        }

        public void setBaseString(String baseStr) {
            this._baseString = baseStr;
        }

        public int getBeginIndex() {
            return _beginIndex;
        }

        public void setBeginIndex(int beginIndex) {
            this._beginIndex = beginIndex;
        }

        public int getEndIndex() {
            return _endIndex;
        }

        public void setEndIndex(int endIndex) {
            this._endIndex = endIndex;
        }

        public String getDelimiter() {
            return _delimiter;
        }

        public void setDelimiter(String delimiter) {
            this._delimiter = delimiter;
        }

        public DelimiterInfo getPrevious() {
            return _previous;
        }

        public void setPrevious(DelimiterInfo previous) {
            this._previous = previous;
        }

        public DelimiterInfo getNext() {
            return _next;
        }

        public void setNext(DelimiterInfo next) {
            this._next = next;
        }
    }

    // ===================================================================================
    //                                                                      Scope Handling
    //                                                                      ==============
    public static final ScopeInfo extractScopeFirst(final String str, final String beginMark, final String endMark) {
        final List<ScopeInfo> scopeList = doExtractScopeList(str, beginMark, endMark, true);
        if (scopeList.isEmpty()) {
            return null;
        }
        if (scopeList.size() > 1) {
            String msg = "This method should extract only one scope: " + scopeList;
            throw new IllegalStateException(msg);
        }
        return scopeList.get(0);
    }

    public static final List<ScopeInfo> extractScopeList(final String str, final String beginMark, final String endMark) {
        return doExtractScopeList(str, beginMark, endMark, false);
    }

    public static final List<ScopeInfo> doExtractScopeList(final String str, final String beginMark,
            final String endMark, final boolean firstOnly) {
        assertStringNotNull(str);
        assertBeginMarkNotNull(beginMark);
        assertEndMarkNotNull(endMark);
        final List<ScopeInfo> resultList = new ArrayList<ScopeInfo>();
        ScopeInfo previous = null;
        String rear = str;
        while (true) {
            final int beginIndex = rear.indexOf(beginMark);
            if (beginIndex < 0) {
                break;
            }
            rear = rear.substring(beginIndex); // scope begins
            if (rear.length() <= beginMark.length()) {
                break;
            }
            rear = rear.substring(beginMark.length()); // skip begin-mark
            final int endIndex = rear.indexOf(endMark);
            if (endIndex < 0) {
                break;
            }
            final String scope = beginMark + rear.substring(0, endIndex + endMark.length());
            final ScopeInfo info = new ScopeInfo();
            info.setBaseString(str);
            final int absoluteIndex = (previous != null ? previous.getEndIndex() : 0) + beginIndex;
            info.setBeginIndex(absoluteIndex);
            info.setEndIndex(absoluteIndex + scope.length());
            info.setBeginMark(beginMark);
            info.setEndMark(endMark);
            info.setContent(rtrim(ltrim(scope, beginMark), endMark));
            info.setScope(scope);
            if (previous != null) {
                info.setPrevious(previous);
                previous.setNext(info);
            }
            resultList.add(info);
            if (previous == null && firstOnly) {
                break;
            }
            previous = info;
            rear = str.substring(info.getEndIndex());
        }
        return resultList;
    }

    public static class ScopeInfo {
        protected String _baseString;
        protected int _beginIndex;
        protected int _endIndex;
        protected String beginMark;
        protected String endMark;
        protected String _content;
        protected String _scope;
        protected ScopeInfo _previous;
        protected ScopeInfo _next;

        public boolean isBeforeScope(int index) {
            return index < _beginIndex;
        }

        public boolean isInScope(int index) {
            return index >= _beginIndex && index <= _endIndex;
        }

        public String replaceContentOnBaseString(String fromStr, String toStr) {
            final List<ScopeInfo> scopeList = takeScopeList();
            final StringBuilder sb = new StringBuilder();
            for (ScopeInfo scope : scopeList) {
                sb.append(scope.substringInterspaceToPrevious());
                sb.append(scope.getBeginMark());
                sb.append(Srl.replace(scope.getContent(), fromStr, toStr));
                sb.append(scope.getEndMark());
                if (scope.getNext() == null) { // last
                    sb.append(scope.substringInterspaceToNext());
                }
            }
            return sb.toString();
        }

        public String replaceInterspaceOnBaseString(String fromStr, String toStr) {
            final List<ScopeInfo> scopeList = takeScopeList();
            final StringBuilder sb = new StringBuilder();
            for (ScopeInfo scope : scopeList) {
                sb.append(Srl.replace(scope.substringInterspaceToPrevious(), fromStr, toStr));
                sb.append(scope.getScope());
                if (scope.getNext() == null) { // last
                    sb.append(Srl.replace(scope.substringInterspaceToNext(), fromStr, toStr));
                }
            }
            return sb.toString();
        }

        protected List<ScopeInfo> takeScopeList() {
            ScopeInfo scope = this;
            while (true) {
                final ScopeInfo previous = scope.getPrevious();
                if (previous == null) {
                    break;
                }
                scope = previous;
            }
            final List<ScopeInfo> scopeList = new ArrayList<ScopeInfo>();
            scopeList.add(scope);
            while (true) {
                final ScopeInfo next = scope.getNext();
                if (next == null) {
                    break;
                }
                scope = next;
                scopeList.add(next);
            }
            return scopeList;
        }

        public String substringInterspaceToPrevious() {
            int previousEndIndex = -1;
            if (_previous != null) {
                previousEndIndex = _previous.getEndIndex();
            }
            if (previousEndIndex >= 0) {
                return _baseString.substring(previousEndIndex, _beginIndex);
            } else {
                return _baseString.substring(0, _beginIndex);
            }
        }

        public String substringInterspaceToNext() {
            int nextBeginIndex = -1;
            if (_next != null) {
                nextBeginIndex = _next.getBeginIndex();
            }
            if (nextBeginIndex >= 0) {
                return _baseString.substring(_endIndex, nextBeginIndex);
            } else {
                return _baseString.substring(_endIndex);
            }
        }

        public String substringScopeToPrevious() {
            int previousBeginIndex = -1;
            if (_previous != null) {
                previousBeginIndex = _previous.getBeginIndex();
            }
            if (previousBeginIndex >= 0) {
                return _baseString.substring(previousBeginIndex, _endIndex);
            } else {
                return _baseString.substring(0, _endIndex);
            }
        }

        public String substringScopeToNext() {
            int nextEndIndex = -1;
            if (_next != null) {
                nextEndIndex = _next.getEndIndex();
            }
            if (nextEndIndex >= 0) {
                return _baseString.substring(_beginIndex, nextEndIndex);
            } else {
                return _baseString.substring(_beginIndex);
            }
        }

        @Override
        public String toString() {
            return _scope + ":(" + _beginIndex + ", " + _endIndex + ")";
        }

        public String getBaseString() {
            return _baseString;
        }

        public void setBaseString(String baseString) {
            this._baseString = baseString;
        }

        public int getBeginIndex() {
            return _beginIndex;
        }

        public void setBeginIndex(int beginIndex) {
            this._beginIndex = beginIndex;
        }

        public int getEndIndex() {
            return _endIndex;
        }

        public void setEndIndex(int endIndex) {
            this._endIndex = endIndex;
        }

        public String getBeginMark() {
            return beginMark;
        }

        public void setBeginMark(String beginMark) {
            this.beginMark = beginMark;
        }

        public String getEndMark() {
            return endMark;
        }

        public void setEndMark(String endMark) {
            this.endMark = endMark;
        }

        public String getContent() {
            return _content;
        }

        public void setContent(String content) {
            this._content = content;
        }

        public String getScope() {
            return _scope;
        }

        public void setScope(String scope) {
            this._scope = scope;
        }

        public ScopeInfo getPrevious() {
            return _previous;
        }

        public void setPrevious(ScopeInfo previous) {
            this._previous = previous;
        }

        public ScopeInfo getNext() {
            return _next;
        }

        public void setNext(ScopeInfo next) {
            this._next = next;
        }
    }

    public static String removeScope(final String str, final String beginMark, final String endMark) {
        assertStringNotNull(str);
        final StringBuilder sb = new StringBuilder();
        String rear = str;
        while (true) {
            final int beginIndex = rear.indexOf(beginMark);
            if (beginIndex < 0) {
                sb.append(rear);
                break;
            }
            final int endIndex = rear.indexOf(endMark);
            if (endIndex < 0) {
                sb.append(rear);
                break;
            }
            if (beginIndex > endIndex) {
                final int borderIndex = endIndex + endMark.length();
                sb.append(rear.substring(0, borderIndex));
                rear = rear.substring(borderIndex);
                continue;
            }
            sb.append(rear.substring(0, beginIndex));
            rear = rear.substring(endIndex + endMark.length());
        }
        return sb.toString();
    }

    // ===================================================================================
    //                                                                       Line Handling
    //                                                                       =============
    /**
     * Remove empty lines. <br />
     * And CR is removed.
     * @param str The target string. (NotNull)
     * @return The filtered string. (NotNull)
     */
    public static String removeEmptyLine(String str) {
        assertStringNotNull(str);
        final StringBuilder sb = new StringBuilder();
        final List<String> splitList = splitList(str, "\n");
        for (String line : splitList) {
            if (Srl.is_Null_or_TrimmedEmpty(line)) {
                continue; // skip
            }
            line = removeCR(line); // remove CR!
            sb.append(line).append("\n");
        }
        final String filtered = sb.toString();
        return filtered.substring(0, filtered.length() - "\n".length());
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
    /**
     * Remove block comments.
     * @param sql The string of SQL. (NotNull)
     * @return The filtered string. (NotNull)
     */
    public static String removeBlockComment(String sql) {
        assertSqlNotNull(sql);
        return removeScope(sql, "/*", "*/");
    }

    /**
     * Remove line comments. <br />
     * And CR is removed.
     * @param sql The string of SQL. (NotNull)
     * @return The filtered string. (NotNull)
     */
    public static String removeLineComment(String sql) { // with removing CR!
        assertSqlNotNull(sql);
        final StringBuilder sb = new StringBuilder();
        final List<String> splitList = splitList(sql, "\n");
        for (String line : splitList) {
            if (line == null) {
                continue;
            }
            line = removeCR(line); // remove CR!
            if (line.trim().startsWith("--")) {
                continue;
            }
            final List<DelimiterInfo> delimiterList = extractDelimiterList(line, "--");
            int realIndex = -1;
            indexLoop: for (DelimiterInfo delimiter : delimiterList) {
                final List<ScopeInfo> scopeList = extractScopeList(line, "/*", "*/");
                final int delimiterIndex = delimiter.getBeginIndex();
                for (ScopeInfo scope : scopeList) {
                    if (scope.isBeforeScope(delimiterIndex)) {
                        break;
                    }
                    if (scope.isInScope(delimiterIndex)) {
                        continue indexLoop;
                    }
                }
                // found
                realIndex = delimiterIndex;
            }
            if (realIndex >= 0) {
                line = line.substring(0, realIndex);
            }
            sb.append(line).append("\n");
        }
        final String filtered = sb.toString();
        return filtered.substring(0, filtered.length() - "\n".length());
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

    protected static void assertSqlNotNull(String sql) {
        assertObjectNotNull("sql", sql);
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
