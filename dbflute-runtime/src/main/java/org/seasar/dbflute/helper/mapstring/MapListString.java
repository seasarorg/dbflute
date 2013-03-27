/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.helper.mapstring;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * The string for map and list.
 * <pre>
 * e.g. map-string
 *   map:{key1=value1,key2=list:{value21,value22,value23},key3=map:{key31=value31}}
 * 
 * e.g. list-string
 *   list:{key1=value1,key2=list:{value21,value22,value23},key3=map:{key31=value31}}
 * </pre>
 * @author jflute
 */
public class MapListString {

    // this code was written when jflute was very young
    // (the code has small modifications only after created)

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** The default mark of map value. */
    public static final String DEFAULT_MAP_MARK = "map:";

    /** The default mark of list value. */
    public static final String DEFAULT_LIST_MARK = "list:";

    /** The default control mark of start-brace. */
    public static final String DEFAULT_START_BRACE = "{";

    /** The default control mark of end-brace. */
    public static final String DEFAULT_END_BRACE = "}";

    /** The default control mark of delimiter. */
    public static final String DEFAULT_DELIMITER = ";";

    /** The default control mark of equal. */
    public static final String DEFAULT_EQUAL = "=";

    /** The escape character for control marks. */
    protected static final String ESCAPE_CHAR = "\\";

    /** The temporary mark of escaped escape character. */
    protected static final String ESCAPED_ESCAPE_MARK = "$$df:escapedEscape$$";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The mark of map. (NotNull: but changeable) */
    protected String _mapMark;

    /** The mark of list. (NotNull: but changeable) */
    protected String _listMark;

    /** The control mark of start brace. (NotNull: but changeable) */
    protected String _startBrace;

    /** The control mark of end brace. (NotNull: but changeable) */
    protected String _endBrace;

    /** The control mark of delimiter. (NotNull: but changeable) */
    protected String _delimiter;

    /** The control mark of equal for map-string. (NotNull: but changeable) */
    protected String _equal;

    /** The escape character for control marks. (NotNull) */
    protected final String _escapeChar;

    /** The string of top (full) string as temporary variable for generation. (NullAllowed: depends on process) */
    protected String _topString;

    /** The string of remainder as temporary variable for generation. (NullAllowed: depends on process) */
    protected String _remainderString;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor setting as default. <br />
     * You can change marks by setters after creation.
     */
    public MapListString() {
        _mapMark = DEFAULT_MAP_MARK;
        _listMark = DEFAULT_LIST_MARK;
        _startBrace = DEFAULT_START_BRACE;
        _endBrace = DEFAULT_END_BRACE;
        _delimiter = DEFAULT_DELIMITER;
        _equal = DEFAULT_EQUAL;
        _escapeChar = ESCAPE_CHAR; // fixed for now
    }

    // ===================================================================================
    //                                                                        Build String
    //                                                                        ============
    /**
     * Build map-string from the map object.
     * @param map The map object that has string keys. (NotNull)
     * @return The string as map expression. (NotNull)
     */
    public String buildMapString(Map<String, ? extends Object> map) {
        final StringBuilder sb = new StringBuilder();
        @SuppressWarnings("unchecked")
        final Map<String, Object> casted = (Map<String, Object>) map;
        doBuildMapString(sb, casted, "", "    ");
        return sb.toString();
    }

    protected void doBuildMapString(StringBuilder sb, Map<String, Object> map, String preIndent, String curIndent) {
        sb.append(_mapMark).append(_startBrace);
        final Set<Entry<String, Object>> entrySet = map.entrySet();
        for (Entry<String, ? extends Object> entry : entrySet) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            sb.append(ln()).append(curIndent).append(_delimiter);
            sb.append(" ").append(escapeControlMark(key)).append(" ").append(_equal).append(" ");
            if (value instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                final Map<String, Object> valueMap = (Map<String, Object>) value;
                doBuildMapString(sb, valueMap, curIndent, calculateNextIndent(preIndent, curIndent));
            } else if (value instanceof List<?>) {
                @SuppressWarnings("unchecked")
                final List<Object> valueList = (List<Object>) value;
                doBuildListString(sb, valueList, curIndent, calculateNextIndent(preIndent, curIndent));
            } else {
                sb.append(escapeControlMark(value));
            }
        }
        sb.append(ln()).append(preIndent).append(_endBrace);
    }

    /**
     * Build list-string from the list object.
     * @param list The list object that has object elements. (NotNull)
     * @return The string as list expression. (NotNull)
     */
    public String buildListString(List<? extends Object> list) {
        final StringBuilder sb = new StringBuilder();
        @SuppressWarnings("unchecked")
        final List<Object> casted = (List<Object>) list;
        doBuildListString(sb, casted, "", "    ");
        return sb.toString();
    }

    protected void doBuildListString(StringBuilder sb, List<? extends Object> list, String preIndent, String curIndent) {
        sb.append(_listMark).append(_startBrace);
        for (Object value : list) {
            sb.append(ln()).append(curIndent).append(_delimiter);
            sb.append(" ");
            if (value instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                final Map<String, Object> valueMap = (Map<String, Object>) value;
                doBuildMapString(sb, valueMap, curIndent, calculateNextIndent(preIndent, curIndent));
            } else if (value instanceof List<?>) {
                @SuppressWarnings("unchecked")
                final List<Object> valueList = (List<Object>) value;
                doBuildListString(sb, valueList, curIndent, calculateNextIndent(preIndent, curIndent));
            } else {
                sb.append(escapeControlMark(value));
            }
        }
        sb.append(ln()).append(preIndent).append(_endBrace);
    }

    protected String calculateNextIndent(String preIndent, String curIndent) {
        final StringBuilder sb = new StringBuilder();
        final int indentLength = curIndent.length() - preIndent.length();
        for (int i = 0; i < indentLength; i++) {
            sb.append(" ");
        }
        return curIndent + sb.toString();
    }

    // ===================================================================================
    //                                                                     Generate Object
    //                                                                     ===============
    /**
     * Generate map object from the map-string.
     * @param mapString The string as map expression. (NotNull)
     * @return The generated map. (NotNull)
     */
    public Map<String, Object> generateMap(String mapString) {
        assertMapString(mapString);

        _topString = mapString;
        _remainderString = mapString;

        removeBothSideSpaceAndTabAndNewLine();
        removePrefixMapMarkAndStartBrace();

        final Map<String, Object> generatedMap = newStringObjectMap();
        parseRemainderMapString(generatedMap);
        if (!"".equals(_remainderString)) {
            String msg = "Final remainderString must be empty string:";
            msg = msg + lnd() + " # remainderString --> " + _remainderString;
            msg = msg + lnd() + " # mapString --> " + mapString;
            msg = msg + lnd() + " # generatedMap --> " + generatedMap;
            throw new IllegalStateException(msg);
        }
        return generatedMap;
    }

    /**
     * Generate map object from list-string.
     * @param listString The string as list expression. (NotNull)
     * @return The generated list. (NotNull)
     */
    public List<Object> generateList(String listString) {
        assertListString(listString);

        _topString = listString;
        _remainderString = listString;

        removeBothSideSpaceAndTabAndNewLine();
        removePrefixListMarkAndStartBrace();

        final List<Object> generatedList = newObjectList();
        parseRemainderListString(generatedList);
        if (!"".equals(_remainderString)) {
            String msg = "Final remainderString must be empty string:";
            msg = msg + lnd() + " # remainderString --> " + _remainderString;
            msg = msg + lnd() + " # listString --> " + listString;
            msg = msg + lnd() + " # generatedList --> " + generatedList;
            throw new IllegalStateException(msg);
        }
        return generatedList;
    }

    // ===================================================================================
    //                                                                               Parse
    //                                                                               =====
    /**
     * Parse the current remainder string as map.
     * @param currentMap The current map made by parse process. (NotNull)
     */
    protected void parseRemainderMapString(final Map<String, Object> currentMap) {
        while (true) {
            if (initializeAtLoopBeginning()) {
                return;
            }

            // *** now, _remainderString should starts with the key of the map ***

            final int equalIndex = indexOfEqual();
            assertEqualIndex(_remainderString, equalIndex, _topString, currentMap);
            final String mapKey = _remainderString.substring(0, equalIndex).trim();
            removePrefixTargetIndexPlus(equalIndex, _equal.length());
            removeBothSideSpaceAndTabAndNewLine();

            // *** now, _remainderString should starts with the value of the map ***

            if (isStartsWithMapPrefix(_remainderString)) {
                removePrefixMapMarkAndStartBrace();
                parseRemainderMapString(setupNestMap(currentMap, mapKey));
                if (closeAfterParseNestMapList()) {
                    return;
                }
                continue;
            }

            if (isStartsWithListPrefix(_remainderString)) {
                removePrefixListMarkAndStartBrace();
                parseRemainderListString(setupNestList(currentMap, mapKey));
                if (closeAfterParseNestMapList()) {
                    return;
                }
                continue;
            }

            final int delimiterIndex = indexOfDelimiter();
            final int endBraceIndex = indexOfEndBrace();
            assertEndBracekIndex(_remainderString, endBraceIndex, _topString, currentMap);

            if (delimiterIndex >= 0 && delimiterIndex < endBraceIndex) { // delimiter exists
                // e.g. value1 ; key2=value2}
                final String mapValue = _remainderString.substring(0, delimiterIndex);
                currentMap.put(filterMapListKey(mapKey), filterMapListValue(mapValue));

                // because the map element continues since the delimiter,
                // skip the delimiter and continue the loop
                removePrefixTargetIndexPlus(delimiterIndex, _delimiter.length());
                continue;
            }

            // e.g. value1} ; key2=value2}
            final String mapValue = _remainderString.substring(0, endBraceIndex);
            currentMap.put(filterMapListKey(mapKey), filterMapListValue(mapValue));

            // analyzing map is over, so close and return.
            closeByEndBraceIndex(endBraceIndex);
            return;
        }
    }

    /**
     * Parse remainder list string.
     * @param currentList current list.
     */
    protected void parseRemainderListString(final List<Object> currentList) {
        while (true) {
            if (initializeAtLoopBeginning()) {
                return;
            }

            // *** now, _remainderString should starts with the value of the list ***

            if (isStartsWithMapPrefix(_remainderString)) {
                removePrefixMapMarkAndStartBrace();
                parseRemainderMapString(setupNestMap(currentList));
                if (closeAfterParseNestMapList()) {
                    return;
                }
                continue;
            }

            if (isStartsWithListPrefix(_remainderString)) {
                removePrefixListMarkAndStartBrace();
                parseRemainderListString(setupNestList(currentList));
                if (closeAfterParseNestMapList()) {
                    return;
                }
                continue;
            }

            final int delimiterIndex = indexOfDelimiter();
            final int endBraceIndex = indexOfEndBrace();
            assertEndBraceIndex(_remainderString, endBraceIndex, _topString, currentList);

            if (delimiterIndex >= 0 && delimiterIndex < endBraceIndex) { // delimiter exists
                // e.g. value1 ; value2 ; value3}
                final String listValue = _remainderString.substring(0, delimiterIndex);
                currentList.add(filterMapListValue(listValue));

                // because the list element continues since the delimiter,
                // skip the delimiter and continue the loop.
                removePrefixTargetIndexPlus(delimiterIndex, _delimiter.length());
                continue;
            }

            // e.g. value1}, value2, }
            final String listValue = _remainderString.substring(0, endBraceIndex);
            currentList.add(filterMapListValue(listValue));

            // analyzing list is over, so close and return
            closeByEndBraceIndex(endBraceIndex);
            return;
        }
    }

    /**
     * Initialize at loop beginning.
     * @return Is is end?
     */
    protected boolean initializeAtLoopBeginning() {
        // remove prefix delimiter (result string is always trimmed)
        removePrefixAllDelimiter();

        if (_remainderString.equals("")) { // analyzing is over
            return true;
        }
        if (isStartsWithEndBrace(_remainderString)) { // analyzing current map is over
            removePrefixEndBrace();
            return true;
        }
        return false;
    }

    /**
     * Close after parse nest map list.
     * @return Is is closed?
     */
    protected boolean closeAfterParseNestMapList() {
        if (isStartsWithEndBrace(_remainderString)) {
            removePrefixEndBrace();
            return true;
        }
        return false;
    }

    /**
     * Close by end-brace index.
     * @param endBraceIndex The index of end-brace. (NotMinus)
     */
    protected void closeByEndBraceIndex(int endBraceIndex) {
        _remainderString = _remainderString.substring(endBraceIndex);
        removePrefixEndBrace();
    }

    protected int indexOfStartBrace() {
        return findIndexOfControlMark(_remainderString, _startBrace);
    }

    protected int indexOfEndBrace() {
        return findIndexOfControlMark(_remainderString, _endBrace);
    }

    protected int indexOfDelimiter() {
        return findIndexOfControlMark(_remainderString, _delimiter);
    }

    protected int indexOfEqual() {
        return findIndexOfControlMark(_remainderString, _equal);
    }

    protected int findIndexOfControlMark(String remainderString, String controlMark) {
        String current = remainderString;
        if (isEscapeCharEscape()) {
            final String escapedEscapeChar = toEscapedMark(_escapeChar);
            current = replace(current, escapedEscapeChar, buildLengthSpace(escapedEscapeChar));
        }
        int baseIndex = 0;
        while (true) {
            final int index = current.indexOf(controlMark);
            if (index < 0) { // not found
                return index;
            }
            if (index > 0) {
                final String lastChar = current.substring(index - 1, index);
                if (_escapeChar.equals(lastChar)) { // escaped
                    final int nextIndex = index + _escapeChar.length();
                    baseIndex = baseIndex + nextIndex;
                    current = current.substring(nextIndex);
                    continue;
                }
            }
            return baseIndex + index; // found
        }
    }

    protected String buildLengthSpace(String value) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    // ===================================================================================
    //                                                                              Remove
    //                                                                              ======
    /**
     * Remove prefix map-mark and start-brace.
     */
    protected void removePrefixMapMarkAndStartBrace() {
        removePrefix(_mapMark + _startBrace);
    }

    /**
     * Remove prefix list-mark and start-brace.
     */
    protected void removePrefixListMarkAndStartBrace() {
        removePrefix(_listMark + _startBrace);
    }

    /**
     * Remove prefix delimiter.
     */
    protected void removePrefixDelimiter() {
        removePrefix(_delimiter);
    }

    /**
     * Remove prefix end-brace.
     */
    protected void removePrefixEndBrace() {
        removePrefix(_endBrace);
    }

    /**
     * Remove prefix as mark.
     * @param prefixString The string for prefix. (NotNull)
     */
    protected void removePrefix(String prefixString) {
        if (_remainderString == null) {
            String msg = "The remainderString must not be null: " + _remainderString;
            throw new IllegalArgumentException(msg);
        }
        if (prefixString == null) {
            String msg = "The argument 'prefixString' must not be null!";
            throw new IllegalArgumentException(msg);
        }

        removeBothSideSpaceAndTabAndNewLine();

        if (_remainderString.length() < prefixString.length()) {
            String msg = "The remainderString length must be larger than the argument 'prefixString' length:";
            msg = msg + lnd() + " # remainderString --> " + _remainderString;
            msg = msg + lnd() + " # prefixString=" + prefixString;
            throw new IllegalArgumentException(msg);
        }
        if (!_remainderString.startsWith(prefixString)) {
            String msg = "The remainderString must start with The argument 'prefixString':";
            msg = msg + lnd() + " # remainderString --> " + _remainderString;
            msg = msg + lnd() + " # prefixString --> " + prefixString;
            throw new IllegalArgumentException(msg);
        }

        _remainderString = _remainderString.substring(prefixString.length());
        removeBothSideSpaceAndTabAndNewLine();
    }

    /**
     * Remove prefix and all delimiters.
     */
    protected void removePrefixAllDelimiter() {
        removeBothSideSpaceAndTabAndNewLine();

        while (true) {
            if (!isStartsWithDelimiter(_remainderString)) {
                break;
            }

            if (isStartsWithDelimiter(_remainderString)) {
                removePrefixDelimiter();
                removeBothSideSpaceAndTabAndNewLine();
            }
        }
    }

    /**
     * Remove both side space and tab and new-line.
     */
    protected void removeBothSideSpaceAndTabAndNewLine() {
        _remainderString = _remainderString.trim();
    }

    /**
     * Remove prefix by the index and plus count.
     * @param index The base index. (NotMinus)
     * @param plusCount The plus count for index. (NotMinus)
     */
    protected void removePrefixTargetIndexPlus(int index, int plusCount) {
        _remainderString = _remainderString.substring(index + plusCount);
    }

    // ===================================================================================
    //                                                                              Filter
    //                                                                              ======
    protected String filterMapListKey(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        value = unescapeControlMark(value);
        return (("".equals(value) || "null".equals(value)) ? null : value);
    }

    protected String filterMapListValue(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        value = unescapeControlMark(value);
        return (("".equals(value) || "null".equals(value)) ? null : value);
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    /**
     * Does it start with the map-prefix?
     * @param targetString The target string to determine. (NotNull)
     * @return The determination, true or false.
     */
    protected boolean isStartsWithMapPrefix(String targetString) {
        if (targetString == null) {
            String msg = "The argument 'targetString' must not be null.";
            throw new IllegalArgumentException(msg);
        }
        targetString = targetString.trim();
        return targetString.startsWith(_mapMark + _startBrace);
    }

    /**
     * Does it start with the list-prefix?
     * @param targetString The target-string to determine. (NotNull)
     * @return The determination, true or false.
     */
    protected boolean isStartsWithListPrefix(String targetString) {
        if (targetString == null) {
            String msg = "The argument 'targetString' must not be null.";
            throw new IllegalArgumentException(msg);
        }
        targetString = targetString.trim();
        return targetString.startsWith(_listMark + _startBrace);
    }

    /**
     * Does it start with the delimiter?
     * @param targetString The target string to determine. (NotNull)
     * @return The determination, true or false.
     */
    protected boolean isStartsWithDelimiter(String targetString) {
        if (targetString == null) {
            String msg = "The argument 'targetString' must not be null.";
            throw new IllegalArgumentException(msg);
        }
        targetString = targetString.trim();
        return targetString.startsWith(_delimiter);
    }

    /**
     * Does it start with end-brace?
     * @param targetString The target string to determine. (NotNull)
     * @return The determination, true or false.
     */
    protected boolean isStartsWithEndBrace(String targetString) {
        if (targetString == null) {
            String msg = "The argument 'targetString' must not be null.";
            throw new IllegalArgumentException(msg);
        }
        targetString = targetString.trim();
        return targetString.startsWith(_endBrace);
    }

    /**
     * Does it end with end-brace?
     * @param targetString The target string to determine. (NotNull)
     * @return The determination, true or false.
     */
    protected boolean isEndsWithEndBrace(String targetString) {
        if (targetString == null) {
            String msg = "The argument 'targetString' must not be null.";
            throw new IllegalArgumentException(msg);
        }
        targetString = targetString.trim();
        return targetString.endsWith(_endBrace);
    }

    // ===================================================================================
    //                                                                       Setup MapList
    //                                                                       =============
    /**
     * Set up new-created nest map as element of the current map.
     * @param currentMap the current map to set up. (NotNull)
     * @param mapKey The key of nest map. (NotNull)
     * @return The new-created nest map. (NotNull)
     */
    protected Map<String, Object> setupNestMap(Map<String, Object> currentMap, String mapKey) {
        final Map<String, Object> nestMap = newStringObjectMap();
        currentMap.put(filterMapListKey(mapKey), nestMap);
        return nestMap;
    }

    /**
     * Set up new-created nest map as element of the current list.
     * @param currentList the current list to set up. (NotNull)
     * @return The new-created nest map. (NotNull)
     */
    protected Map<String, Object> setupNestMap(List<Object> currentList) {
        final Map<String, Object> nestMap = newStringObjectMap();
        currentList.add(nestMap);
        return nestMap;
    }

    /**
     * Set up new-created nest list as element of the current map.
     * @param currentMap the current map to set up. (NotNull)
     * @param mapKey The key of nest map. (NotNull)
     * @return The new-created nest list. (NotNull)
     */
    protected List<Object> setupNestList(Map<String, Object> currentMap, String mapKey) {
        final List<Object> nestList = newObjectList();
        currentMap.put(filterMapListKey(mapKey), nestList);
        return nestList;
    }

    /**
     * Set up new-created nest list as element of the current list.
     * @param currentList the current map to set up. (NotNull)
     * @return The new-created nest list. (NotNull)
     */
    protected List<Object> setupNestList(List<Object> currentList) {
        final List<Object> nestList = newObjectList();
        currentList.add(nestList);
        return nestList;
    }

    /**
     * New string-object map.
     * @return The new-created map. (NotNull)
     */
    protected Map<String, Object> newStringObjectMap() {
        return new LinkedHashMap<String, Object>();
    }

    /**
     * New object-type list.
     * @return The new-created list. (NotNull)
     */
    protected List<Object> newObjectList() {
        return new ArrayList<Object>();
    }

    // ===================================================================================
    //                                                                              Escape
    //                                                                              ======
    protected String escapeControlMark(Object value) {
        if (value == null) {
            return null;
        }
        String filtered = value.toString();
        if (isEscapeCharEscape()) {
            filtered = replace(filtered, _escapeChar, toEscapedMark(_escapeChar));
        }
        filtered = replace(filtered, _startBrace, toEscapedMark(_startBrace));
        filtered = replace(filtered, _endBrace, toEscapedMark(_endBrace));
        filtered = replace(filtered, _delimiter, toEscapedMark(_delimiter));
        filtered = replace(filtered, _equal, toEscapedMark(_equal));
        return filtered;
    }

    protected String unescapeControlMark(String value) {
        if (value == null) {
            return null;
        }
        String filtered = value;
        final String escapedEscapeMark = ESCAPED_ESCAPE_MARK;
        if (isEscapeCharEscape()) {
            filtered = replace(filtered, toEscapedMark(_escapeChar), escapedEscapeMark);
        }
        filtered = replace(filtered, toEscapedMark(_startBrace), _startBrace);
        filtered = replace(filtered, toEscapedMark(_endBrace), _endBrace);
        filtered = replace(filtered, toEscapedMark(_delimiter), _delimiter);
        filtered = replace(filtered, toEscapedMark(_equal), _equal);
        if (isEscapeCharEscape()) {
            filtered = replace(filtered, escapedEscapeMark, _escapeChar);
        }
        return filtered;
    }

    protected String toEscapedMark(String mark) {
        return _escapeChar + mark;
    }

    protected boolean isEscapeCharEscape() {
        // escape for escape char is unsupported (unneeded)
        // so fixedly returns false
        //
        // compatibility is treated as important here
        //  o "\\n = \n" in convertValueMap.dfprop can directly work
        //  o plain "\" is can directly work
        //
        // [specification]
        // escape char without control mark is treated as plain value
        //  e.g. "a\b\c"
        // 
        // previous escape char of control mark is always treated as escape char
        //  e.g. "\;"
        //
        // if any spaces between the escape char and the control mark exist,
        //  the escape char is plain value, e.g. "\ ;"
        //
        return false;

    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    // *these codes, written by younger jflute, should be improved but it's very hard...
    protected void assertMapString(String mapString) {
        if (mapString == null) {
            String msg = "Argument[mapString] must not be null: ";
            throw new IllegalArgumentException(msg + "mapString=null");
        }
        mapString = mapString.trim();
        if (!isStartsWithMapPrefix(mapString)) {
            String msg = "Argument[mapString] must start with '" + _mapMark + _startBrace + "': ";
            throw new IllegalArgumentException(msg + "mapString=" + mapString);
        }
        if (!isEndsWithEndBrace(mapString)) {
            String msg = "Argument[mapString] must end with '" + _endBrace + "': ";
            throw new IllegalArgumentException(msg + "mapString=" + mapString);
        }

        final int startBraceCount = getControlMarkCount(mapString, _startBrace);
        final int endBraceCount = getControlMarkCount(mapString, _endBrace);
        if (startBraceCount != endBraceCount) {
            String msg = "The count of start braces should be the same as the one of end braces:";
            msg = msg + lnd() + " # mapString --> " + mapString;
            msg = msg + lnd() + " # startBraceCount --> " + startBraceCount;
            msg = msg + lnd() + " # endBraceCount --> " + endBraceCount;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertListString(String listString) {
        if (listString == null) {
            String msg = "Argument[listString] must not be null: ";
            throw new IllegalArgumentException(msg + "listString=null");
        }
        listString = listString.trim();
        if (!isStartsWithListPrefix(listString)) {
            String msg = "Argument[listString] must start with '" + _mapMark + "': ";
            throw new IllegalArgumentException(msg + "listString=" + listString);
        }
        if (!isEndsWithEndBrace(listString)) {
            String msg = "Argument[listString] must end with '" + _endBrace + "': ";
            throw new IllegalArgumentException(msg + "listString=" + listString);
        }

        final int startBraceCount = getControlMarkCount(listString, _startBrace);
        final int endBraceCount = getControlMarkCount(listString, _endBrace);
        if (startBraceCount != endBraceCount) {
            String msg = "The count of start braces should be the same as the one of end braces:";
            msg = msg + lnd() + " # listString --> " + listString;
            msg = msg + lnd() + " # startBraceCount --> " + startBraceCount;
            msg = msg + lnd() + " # endBraceCount --> " + endBraceCount;
            throw new IllegalArgumentException(msg);
        }
    }

    protected int getControlMarkCount(String targetString, String controlMark) {
        int result = 0;
        String current = targetString;
        while (true) {
            final int index = findIndexOfControlMark(current, controlMark);
            if (index < 0) {
                break;
            }
            result++;
            current = current.substring(index + controlMark.length());
        }
        if (result == 0) {
            result = -1;
        }
        return result;
    }

    protected void assertEqualIndex(String remainderMapString, int equalIndex, String mapString4Log,
            Map<String, Object> currentMap4Log) {
        if (remainderMapString == null) {
            String msg = "Argument[remainderMapString] must not be null:";
            msg = msg + lnd() + " # remainderMapString --> null";
            msg = msg + lnd() + " # equalIndex --> " + equalIndex;
            msg = msg + lnd() + " # mapString4Log --> " + mapString4Log;
            msg = msg + lnd() + " # currentMap4Log --> " + currentMap4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        if (equalIndex < 0) {
            String msg = "Argument[equalIndex] must be plus or zero:";
            msg = msg + lnd() + " # remainderMapString --> " + remainderMapString;
            msg = msg + lnd() + " # equalIndex --> " + equalIndex;
            msg = msg + lnd() + " # mapString4Log --> " + mapString4Log;
            msg = msg + lnd() + " # currentMap4Log --> " + currentMap4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        if (remainderMapString.length() < equalIndex) {
            String msg = "Argument[remainderMapString] length must be larger than equalIndex value:";
            msg = msg + lnd() + " # remainderMapString --> " + remainderMapString;
            msg = msg + lnd() + " # equalIndex --> " + equalIndex;
            msg = msg + lnd() + " # mapString4Log --> " + mapString4Log;
            msg = msg + lnd() + " # currentMap4Log --> " + currentMap4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        final String expectedAsEndMark = remainderMapString.substring(equalIndex, equalIndex + _equal.length());
        if (!expectedAsEndMark.equals(_equal)) {
            String msg = "Argument[remainderMapString] must have '" + _equal + "' at Argument[equalIndex]:";
            msg = msg + lnd() + " # remainderMapString --> " + remainderMapString;
            msg = msg + lnd() + " # equalIndex --> " + equalIndex;
            msg = msg + lnd() + " # expectedAsEndMark --> " + expectedAsEndMark;
            msg = msg + lnd() + " # mapString --> " + mapString4Log;
            msg = msg + lnd() + " # currentMap --> " + currentMap4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertEndBracekIndex(String remainderMapString, int endBraceIndex, String mapString4Log,
            Map<String, Object> currentMap4Log) {
        if (remainderMapString == null) {
            String msg = "Argument[remainderMapString] must not be null:";
            msg = msg + lnd() + " # remainderMapString --> null";
            msg = msg + lnd() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + lnd() + " # mapString --> " + mapString4Log;
            msg = msg + lnd() + " # currentMap --> " + currentMap4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        if (endBraceIndex < 0) {
            String msg = "Argument[endMarkIndex] must be plus or zero:";
            msg = msg + lnd() + " # remainderMapString --> " + remainderMapString;
            msg = msg + lnd() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + lnd() + " # mapString --> =" + mapString4Log;
            msg = msg + lnd() + " # currentMap --> " + currentMap4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        if (remainderMapString.length() < endBraceIndex) {
            String msg = "Argument[remainderMapString] length must be larger than endMarkIndex value:";
            msg = msg + lnd() + " # remainderMapString --> " + remainderMapString;
            msg = msg + lnd() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + lnd() + " # mapString --> " + mapString4Log;
            msg = msg + lnd() + " # currentMap --> " + currentMap4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        final String expectedAsEndMark = remainderMapString
                .substring(endBraceIndex, endBraceIndex + _endBrace.length());
        if (!expectedAsEndMark.equals(_endBrace)) {
            String msg = "Argument[remainderMapString] must have '" + _endBrace + "' at Argument[endBraceIndex]:";
            msg = msg + lnd() + " # remainderMapString --> " + remainderMapString;
            msg = msg + lnd() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + lnd() + " # expectedAsEndMark --> " + expectedAsEndMark;
            msg = msg + lnd() + " # mapString --> " + mapString4Log;
            msg = msg + lnd() + " # currentMap --> " + currentMap4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertEndBraceIndex(String remainderListString, int endBraceIndex, String listString4Log,
            List<?> currentList4Log) {
        if (remainderListString == null) {
            String msg = "Argument[remainderListString] must not be null:";
            msg = msg + lnd() + " # remainderListString --> null";
            msg = msg + lnd() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + lnd() + " # listString --> " + listString4Log;
            msg = msg + lnd() + " # currentList --> " + currentList4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        if (endBraceIndex < 0) {
            String msg = "Argument[endMarkIndex] must be plus or zero:";
            msg = msg + lnd() + " # remainderListString --> " + remainderListString;
            msg = msg + lnd() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + lnd() + " # listString --> " + listString4Log;
            msg = msg + lnd() + " # currentList --> " + currentList4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        if (remainderListString.length() < endBraceIndex) {
            String msg = "Argument[remainderListString] length must be larger than endMarkIndex value:";
            msg = msg + lnd() + " # remainderListString --> " + remainderListString;
            msg = msg + lnd() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + lnd() + " # listString --> " + listString4Log;
            msg = msg + lnd() + " # currentList --> " + currentList4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }

        final String expectedAsEndBrace = remainderListString.substring(endBraceIndex,
                endBraceIndex + _endBrace.length());
        if (!expectedAsEndBrace.equals(_endBrace)) {
            String msg = "Argument[remainderListString] must have '" + _endBrace + "' at Argument[endBraceIndex]:";
            msg = msg + lnd() + " # remainderListString --> " + remainderListString;
            msg = msg + lnd() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + lnd() + " # expectedAsEndBrace --> " + expectedAsEndBrace;
            msg = msg + lnd() + " # listString --> " + listString4Log;
            msg = msg + lnd() + " # currentList --> " + currentList4Log;
            msg = msg + lnd() + " # _startBrace --> " + _startBrace;
            msg = msg + lnd() + " # _endBrace --> " + _endBrace;
            msg = msg + lnd() + " # _delimiter --> " + _delimiter;
            msg = msg + lnd() + " # _equal --> " + _equal;
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String replace(String str, String fromStr, String toStr) {
        StringBuilder sb = null; // lazy load
        int pos = 0;
        int pos2 = 0;
        do {
            pos = str.indexOf(fromStr, pos2);
            if (pos2 == 0 && pos < 0) { // first loop and not found
                return str; // without creating StringBuilder 
            }
            if (sb == null) {
                sb = new StringBuilder();
            }
            if (pos == 0) {
                sb.append(toStr);
                pos2 = fromStr.length();
            } else if (pos > 0) {
                sb.append(str.substring(pos2, pos));
                sb.append(toStr);
                pos2 = pos + fromStr.length();
            } else { // (pos < 0) second or after loop only
                sb.append(str.substring(pos2));
                return sb.toString();
            }
        } while (true);
    }

    protected String lnd() {
        return ln() + "    ";
    }

    protected final String ln() {
        return "\n";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setMapMark(String mapMark) {
        _mapMark = mapMark;
    }

    public void setListMark(String listMark) {
        _listMark = listMark;
    }

    public void setStartBrace(String startBrace) {
        _startBrace = startBrace;
    }

    public void setEndBrace(String endBrace) {
        _endBrace = endBrace;
    }

    public void setDelimiter(String delimiter) {
        _delimiter = delimiter;
    }

    public void setEqual(String equal) {
        _equal = equal;
    }
}