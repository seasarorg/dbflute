package org.seasar.dbflute.helper.mapstring;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * MapList-String.
 * 
 * @author jflute
 */
public class DfMapListStringImpl implements DfMapListString {

    /** Line separator. */
    public static final String NEW_LINE = System.getProperty("line.separator");

    /** Map mark. */
    protected String _mapMark;

    /** List mark. */
    protected String _listMark;

    /** Delimiter. */
    protected String _delimiter;

    /** Start brace. */
    protected String _startBrace;

    /** End brace. */
    protected String _endBrace;

    /** Equal. */
    protected String _equal;

    /** Top string. */
    protected String _topString;

    /** Remainder string. */
    protected String _remainderString;

    public DfMapListStringImpl() {
        _mapMark = DEFAULT_MAP_MARK;
        _listMark = DEFAULT_LIST_MARK;
        _delimiter = DEFAULT_DELIMITER;
        _startBrace = DEFAULT_START_BRACE;
        _endBrace = DEFAULT_END_BRACE;
        _equal = DEFAULT_EQUAL;
    }

    public DfMapListStringImpl(String mapMark, String listMark, String delimiter) {
        _mapMark = mapMark;
        _listMark = listMark;
        _delimiter = delimiter;
        _startBrace = DEFAULT_START_BRACE;
        _endBrace = DEFAULT_END_BRACE;
        _equal = DEFAULT_EQUAL;
    }

    public DfMapListStringImpl(String mapMark, String listMark, String delimiter, String startBrace, String endBrace,
            String equal) {
        _mapMark = mapMark;
        _listMark = listMark;
        _delimiter = delimiter;
        _startBrace = startBrace;
        _endBrace = endBrace;
        _equal = equal;
    }

    // ==========================================================================================
    //                                                                                     Setter
    //                                                                                     ======
    /**
     * Set delimiter.
     * 
     * @param delimiter Delimiter.
     */
    public synchronized void setDelimiter(String delimiter) {
        _delimiter = delimiter;
    }

    /**
     * Set start brace.
     * 
     * @param startBrace Start brace.
     */
    public synchronized void setStartBrace(String startBrace) {
        _startBrace = startBrace;
    }

    /**
     * Set end brace.
     * 
     * @param endBrace End brace.
     */
    public synchronized void setEndBrace(String endBrace) {
        _endBrace = endBrace;
    }

    // ****************************************************************************************************
    //                                                                                          Main Method
    //                                                                                          ***********

    // ==========================================================================================
    //                                                                                   Generate
    //                                                                                   ========
    /**
     * Generate map from map-string. {Implement}
     * 
     * @param mapString Map-string (NotNull)
     * @return Generated map. (NotNull)
     */
    public synchronized Map<String, Object> generateMap(String mapString) {
        assertMapString(mapString);

        _topString = mapString;
        _remainderString = mapString;

        removeBothSideSpaceAndTabAndNewLine();
        removePrefixMapMarkAndStartBrace();

        final Map<String, Object> generatedMap = newStringObjectMap();
        parseRemainderMapString(generatedMap);
        if (!"".equals(_remainderString)) {
            String msg = "Final remainderString must be empty string:";
            msg = msg + getNewLineAndIndent() + " # remainderString --> " + _remainderString;
            msg = msg + getNewLineAndIndent() + " # mapString --> " + mapString;
            msg = msg + getNewLineAndIndent() + " # generatedMap --> " + generatedMap;
            throw new IllegalStateException(msg);
        }
        return generatedMap;
    }

    /**
     * Generate map from list-string. {Implement}
     * 
     * @param listString List-string (NotNull)
     * @return Generated list. (NotNull)
     */
    public synchronized List<Object> generateList(String listString) {
        assertListString(listString);

        _topString = listString;
        _remainderString = listString;

        removeBothSideSpaceAndTabAndNewLine();
        removePrefixListMarkAndStartBrace();

        final List<Object> generatedList = newObjectList();
        parseRemainderListString(generatedList);
        if (!"".equals(_remainderString)) {
            String msg = "Final remainderString must be empty string:";
            msg = msg + getNewLineAndIndent() + " # remainderString --> " + _remainderString;
            msg = msg + getNewLineAndIndent() + " # listString --> " + listString;
            msg = msg + getNewLineAndIndent() + " # generatedList --> " + generatedList;
            throw new IllegalStateException(msg);
        }
        return generatedList;
    }

    // ==========================================================================================
    //                                                                                      Parse
    //                                                                                      =====
    protected void parseRemainderMapString(final Map<String, Object> currentMap) {
        while (true) {
            if (initializeAtLoopBeginning()) {
                return;
            }

            final int equalIndex = _remainderString.indexOf(_equal);
            assertEqualIndex(_remainderString, equalIndex, _topString, currentMap);
            final String mapKey = _remainderString.substring(0, equalIndex).trim();
            removePrefixTargetIndexPlusOne(equalIndex);
            removeBothSideSpaceAndTabAndNewLine();

            if (isStartsWithMapPrefix(_remainderString)) {
                removePrefixMapMarkAndStartBrace();
                parseRemainderMapString(setupNestMap(currentMap, mapKey));
                if (closingAfterParseNestMapList()) {
                    return;
                }
                continue;
            }

            if (isStartsWithListPrefix(_remainderString)) {
                removePrefixListMarkAndStartBrace();
                parseRemainderListString(setupNestList(currentMap, mapKey));
                if (closingAfterParseNestMapList()) {
                    return;
                }
                continue;
            }

            final int delimiterIndex = _remainderString.indexOf(_delimiter);
            final int endBraceIndex = _remainderString.indexOf(_endBrace);
            assertEndBracekIndex(_remainderString, endBraceIndex, _topString, currentMap);

            if (delimiterIndex >= 0 && delimiterIndex < endBraceIndex) {
                final String mapValue = _remainderString.substring(0, delimiterIndex);
                currentMap.put(mapKey, filterMapListValue(mapValue));

                removePrefixTargetIndexPlusOne(delimiterIndex);
                continue;
            }

            final String mapValue = _remainderString.substring(0, endBraceIndex);
            currentMap.put(mapKey, filterMapListValue(mapValue));

            closingByEndBraceIndex(endBraceIndex);
            return;
        }
    }

    protected void parseRemainderListString(final List<Object> currentList) {
        while (true) {
            if (initializeAtLoopBeginning()) {
                return;
            }

            if (isStartsWithMapPrefix(_remainderString)) {
                removePrefixMapMarkAndStartBrace();
                parseRemainderMapString(setupNestMap(currentList));
                if (closingAfterParseNestMapList()) {
                    return;
                }
                continue;
            }

            if (isStartsWithListPrefix(_remainderString)) {
                removePrefixListMarkAndStartBrace();
                parseRemainderListString(setupNestList(currentList));
                if (closingAfterParseNestMapList()) {
                    return;
                }
                continue;
            }

            final int delimiterIndex = _remainderString.indexOf(_delimiter);
            final int endBraceIndex = _remainderString.indexOf(_endBrace);
            assertEndBraceIndex(_remainderString, endBraceIndex, _topString, currentList);

            if (delimiterIndex >= 0 && delimiterIndex < endBraceIndex) {
                final String listValue = _remainderString.substring(0, delimiterIndex);
                currentList.add(filterMapListValue(listValue));

                removePrefixTargetIndexPlusOne(delimiterIndex);
                continue;
            }

            final String listValue = _remainderString.substring(0, endBraceIndex);
            currentList.add(filterMapListValue(listValue));

            closingByEndBraceIndex(endBraceIndex);
            return;
        }
    }

    /**
     * @return Is return?
     */
    protected boolean initializeAtLoopBeginning() {
        removePrefixAllDelimiter();

        if (_remainderString.equals("")) {
            return true;
        }

        if (isStartsWithEndBrace(_remainderString)) {
            removePrefixEndBrace();
            return true;
        }
        return false;
    }

    /**
     * @return Is return?
     */
    protected boolean closingAfterParseNestMapList() {
        if (isStartsWithEndBrace(_remainderString)) {
            removePrefixEndBrace();
            return true;
        }
        return false;
    }

    protected void closingByEndBraceIndex(int endBraceIndex) {
        _remainderString = _remainderString.substring(endBraceIndex);
        removePrefixEndBrace();
    }

    // ****************************************************************************************************
    //                                                                                      StateFul Method
    //                                                                                      ***************

    // ==========================================================================================
    //                                                                                     Remove
    //                                                                                     ======
    protected void removePrefixMapMarkAndStartBrace() {
        removePrefix(_mapMark + _startBrace);
    }

    protected void removePrefixListMarkAndStartBrace() {
        removePrefix(_listMark + _startBrace);
    }

    protected void removePrefixDelimiter() {
        removePrefix(_delimiter);
    }

    protected void removePrefixEndBrace() {
        removePrefix(_endBrace);
    }

    protected void removePrefix(String prefixString) {
        if (_remainderString == null) {
            String msg = "Argument[remainderString] must not be null: " + _remainderString;
            throw new IllegalArgumentException(msg);
        }
        if (prefixString == null) {
            String msg = "Argument[prefixString] must not be null: " + prefixString;
            throw new IllegalArgumentException(msg);
        }

        removeBothSideSpaceAndTabAndNewLine();

        if (_remainderString.length() < prefixString.length()) {
            String msg = "Argument[remainderString] length must be larger than Argument[prefixString] length:";
            msg = msg + getNewLineAndIndent() + " # remainderString --> " + _remainderString;
            msg = msg + getNewLineAndIndent() + " # prefixString=" + prefixString;
            throw new IllegalArgumentException(msg);
        }
        if (!_remainderString.startsWith(prefixString)) {
            String msg = "Argument[remainderString] must start with Argument[prefixString:]";
            msg = msg + getNewLineAndIndent() + " # remainderString --> " + _remainderString;
            msg = msg + getNewLineAndIndent() + " # prefixString --> " + prefixString;
            throw new IllegalArgumentException(msg);
        }

        _remainderString = _remainderString.substring(prefixString.length());
        removeBothSideSpaceAndTabAndNewLine();
    }

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

    protected void removeBothSideSpaceAndTabAndNewLine() {
        _remainderString = _remainderString.trim();
    }

    protected void removePrefixTargetIndexPlusOne(int index) {
        _remainderString = _remainderString.substring(index + 1);
    }

    // ****************************************************************************************************
    //                                                                                     StateLess Method
    //                                                                                     ****************

    // ==========================================================================================
    //                                                                                     Assert
    //                                                                                     ======
    protected void assertMapString(String mapString) {
        if (mapString == null) {
            String msg = "Argument[mapString] must not be null: ";
            throw new IllegalArgumentException(msg + "mapString=" + mapString);
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

        final int startBraceCount = getDelimiterCount(mapString, _startBrace);
        final int endBraceCount = getDelimiterCount(mapString, _endBrace);
        if (startBraceCount != endBraceCount) {
            String msg = "It is necessary to have braces of the same number on start and end:";
            msg = msg + getNewLineAndIndent() + " # mapString --> " + mapString;
            msg = msg + getNewLineAndIndent() + " # startBraceCount --> " + startBraceCount;
            msg = msg + getNewLineAndIndent() + " # endBraceCount --> " + endBraceCount;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertListString(String listString) {
        if (listString == null) {
            String msg = "Argument[listString] must not be null: ";
            throw new IllegalArgumentException(msg + "listString=" + listString);
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

        final int startBraceCount = getDelimiterCount(listString, _startBrace);
        final int endBraceCount = getDelimiterCount(listString, _endBrace);
        if (startBraceCount != endBraceCount) {
            String msg = "It is necessary to have braces of the same number on start and end:";
            msg = msg + getNewLineAndIndent() + " # listString --> " + listString;
            msg = msg + getNewLineAndIndent() + " # startBraceCount --> " + startBraceCount;
            msg = msg + getNewLineAndIndent() + " # endBraceCount --> " + endBraceCount;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertEqualIndex(String remainderMapString, int equalIndex, String mapString4Log, Map currentMap4Log) {
        if (remainderMapString == null) {
            String msg = "Argument[remainderMapString] must not be null:";
            msg = msg + getNewLineAndIndent() + " # remainderMapString --> " + remainderMapString;
            msg = msg + getNewLineAndIndent() + " # equalIndex --> " + equalIndex;
            msg = msg + getNewLineAndIndent() + " # mapString4Log --> " + mapString4Log;
            msg = msg + getNewLineAndIndent() + " # currentMap4Log --> " + currentMap4Log;
            throw new IllegalArgumentException(msg);
        }

        if (equalIndex < 0) {
            String msg = "Argument[equalIndex] must be plus or zero:";
            msg = msg + getNewLineAndIndent() + " # remainderMapString --> " + remainderMapString;
            msg = msg + getNewLineAndIndent() + " # equalIndex --> " + equalIndex;
            msg = msg + getNewLineAndIndent() + " # mapString4Log --> " + mapString4Log;
            msg = msg + getNewLineAndIndent() + " # currentMap4Log --> " + currentMap4Log;
            throw new IllegalArgumentException(msg);
        }

        if (remainderMapString.length() < equalIndex) {
            String msg = "Argument[remainderMapString] length must be larger than equalIndex value:";
            msg = msg + getNewLineAndIndent() + " # remainderMapString --> " + remainderMapString;
            msg = msg + getNewLineAndIndent() + " # equalIndex --> " + equalIndex;
            msg = msg + getNewLineAndIndent() + " # mapString4Log --> " + mapString4Log;
            msg = msg + getNewLineAndIndent() + " # currentMap4Log --> " + currentMap4Log;
            throw new IllegalArgumentException(msg);
        }

        final String expectedAsEndMark = remainderMapString.substring(equalIndex, equalIndex + 1);
        if (!expectedAsEndMark.equals(_equal)) {
            String msg = "Argument[remainderMapString] must have '" + _equal + "' at Argument[equalIndex]:";
            msg = msg + getNewLineAndIndent() + " # remainderMapString --> " + remainderMapString;
            msg = msg + getNewLineAndIndent() + " # equalIndex --> " + equalIndex;
            msg = msg + getNewLineAndIndent() + " # expectedAsEndMark --> " + expectedAsEndMark;
            msg = msg + getNewLineAndIndent() + " # mapString --> " + mapString4Log;
            msg = msg + getNewLineAndIndent() + " # currentMap --> " + currentMap4Log;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertEndBracekIndex(String remainderMapString, int endBraceIndex, String mapString4Log,
            Map currentMap4Log) {
        if (remainderMapString == null) {
            String msg = "Argument[remainderMapString] must not be null:";
            msg = msg + getNewLineAndIndent() + " # remainderMapString --> " + remainderMapString;
            msg = msg + getNewLineAndIndent() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + getNewLineAndIndent() + " # mapString --> " + mapString4Log;
            msg = msg + getNewLineAndIndent() + " # currentMap --> " + currentMap4Log;
            throw new IllegalArgumentException(msg);
        }

        if (endBraceIndex < 0) {
            String msg = "Argument[endMarkIndex] must be plus or zero:";
            msg = msg + getNewLineAndIndent() + " # remainderMapString --> " + remainderMapString;
            msg = msg + getNewLineAndIndent() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + getNewLineAndIndent() + " # mapString --> =" + mapString4Log;
            msg = msg + getNewLineAndIndent() + " # currentMap --> " + currentMap4Log;
            throw new IllegalArgumentException(msg);
        }

        if (remainderMapString.length() < endBraceIndex) {
            String msg = "Argument[remainderMapString] length must be larger than endMarkIndex value:";
            msg = msg + getNewLineAndIndent() + " # remainderMapString --> " + remainderMapString;
            msg = msg + getNewLineAndIndent() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + getNewLineAndIndent() + " # mapString --> " + mapString4Log;
            msg = msg + getNewLineAndIndent() + " # currentMap --> " + currentMap4Log;
            throw new IllegalArgumentException(msg);
        }

        final String expectedAsEndMark = remainderMapString.substring(endBraceIndex, endBraceIndex + 1);
        if (!expectedAsEndMark.equals(_endBrace)) {
            String msg = "Argument[remainderMapString] must have '" + _endBrace + "' at Argument[endBraceIndex]:";
            msg = msg + getNewLineAndIndent() + " # remainderMapString --> " + remainderMapString;
            msg = msg + getNewLineAndIndent() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + getNewLineAndIndent() + " # expectedAsEndMark --> " + expectedAsEndMark;
            msg = msg + getNewLineAndIndent() + " # mapString --> " + mapString4Log;
            msg = msg + getNewLineAndIndent() + " # currentMap --> " + currentMap4Log;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertEndBraceIndex(String remainderListString, int endBraceIndex, String listString4Log,
            List currentList4Log) {
        if (remainderListString == null) {
            String msg = "Argument[remainderListString] must not be null:";
            msg = msg + getNewLineAndIndent() + " # remainderListString --> " + remainderListString;
            msg = msg + getNewLineAndIndent() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + getNewLineAndIndent() + " # listString --> " + listString4Log;
            msg = msg + getNewLineAndIndent() + " # currentList --> " + currentList4Log;
            throw new IllegalArgumentException(msg);
        }

        if (endBraceIndex < 0) {
            String msg = "Argument[endMarkIndex] must be plus or zero:";
            msg = msg + getNewLineAndIndent() + " # remainderListString --> " + remainderListString;
            msg = msg + getNewLineAndIndent() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + getNewLineAndIndent() + " # listString --> " + listString4Log;
            msg = msg + getNewLineAndIndent() + " # currentList --> " + currentList4Log;
            throw new IllegalArgumentException(msg);
        }

        if (remainderListString.length() < endBraceIndex) {
            String msg = "Argument[remainderListString] length must be larger than endMarkIndex value:";
            msg = msg + getNewLineAndIndent() + " # remainderListString --> " + remainderListString;
            msg = msg + getNewLineAndIndent() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + getNewLineAndIndent() + " # listString --> " + listString4Log;
            msg = msg + getNewLineAndIndent() + " # currentList --> " + currentList4Log;
            throw new IllegalArgumentException(msg);
        }

        final String expectedAsEndBrace = remainderListString.substring(endBraceIndex, endBraceIndex + 1);
        if (!expectedAsEndBrace.equals(_endBrace)) {
            String msg = "Argument[remainderListString] must have '" + _endBrace + "' at Argument[endBraceIndex]:";
            msg = msg + getNewLineAndIndent() + " # remainderListString --> " + remainderListString;
            msg = msg + getNewLineAndIndent() + " # endBraceIndex --> " + endBraceIndex;
            msg = msg + getNewLineAndIndent() + " # expectedAsEndBrace --> " + expectedAsEndBrace;
            msg = msg + getNewLineAndIndent() + " # listString --> " + listString4Log;
            msg = msg + getNewLineAndIndent() + " # currentList --> " + currentList4Log;
            throw new IllegalArgumentException(msg);
        }
    }

    // ==========================================================================================
    //                                                                                     Filter
    //                                                                                     ======
    /**
     * Filter map or list value.
     * <p>
     * <pre>
     * # The value is trimmed.
     * # If the value is null, this returns null.
     * # If the value is 'null', this returns null.
     * # If the trimmed value is empty string, this returns null.
     * </pre>
     * @param value value. (Nullable)
     * @return Filtered value. (Nullable)
     */
    protected String filterMapListValue(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        return (("".equals(value) || "null".equals(value)) ? null : value);
    }

    // ==========================================================================================
    //                                                                                  Judgement
    //                                                                                  =========
    protected boolean isStartsWithMapPrefix(String targetString) {
        if (targetString == null) {
            String msg = "Argument[targetString] must not be null: " + targetString;
            throw new IllegalArgumentException(msg);
        }
        targetString = targetString.trim();
        if (targetString.startsWith(_mapMark + _startBrace)) {
            return true;
        } else {
            return false;
        }
    }

    protected boolean isStartsWithListPrefix(String targetString) {
        if (targetString == null) {
            String msg = "Argument[targetString] must not be null: " + targetString;
            throw new IllegalArgumentException(msg);
        }
        targetString = targetString.trim();
        if (targetString.startsWith(_listMark + _startBrace)) {
            return true;
        } else {
            return false;
        }
    }

    protected boolean isStartsWithDelimiter(String targetString) {
        if (targetString == null) {
            String msg = "Argument[targetString] must not be null: " + targetString;
            throw new IllegalArgumentException(msg);
        }
        targetString = targetString.trim();
        if (targetString.startsWith(_delimiter)) {
            return true;
        } else {
            return false;
        }
    }

    protected boolean isStartsWithEndBrace(String targetString) {
        if (targetString == null) {
            String msg = "Argument[targetString] must not be null: " + targetString;
            throw new IllegalArgumentException(msg);
        }
        targetString = targetString.trim();
        if (targetString.startsWith(_endBrace)) {
            return true;
        } else {
            return false;
        }
    }

    protected boolean isEndsWithEndBrace(String targetString) {
        if (targetString == null) {
            String msg = "Argument[targetString] must not be null: " + targetString;
            throw new IllegalArgumentException(msg);
        }
        targetString = targetString.trim();
        if (targetString.endsWith(_endBrace)) {
            return true;
        } else {
            return false;
        }
    }

    // ==========================================================================================
    //                                                                                      Other
    //                                                                                      =====
    protected Map<String, Object> setupNestMap(Map<String, Object> currentMap, String mapKey) {
        final Map<String, Object> nestMap = newStringObjectMap();
        currentMap.put(mapKey, nestMap);
        return nestMap;
    }

    protected Map<String, Object> setupNestMap(List<Object> currentList) {
        final Map<String, Object> nestMap = newStringObjectMap();
        currentList.add(nestMap);
        return nestMap;
    }

    protected List<Object> setupNestList(Map<String, Object> currentMap, String mapKey) {
        final List<Object> nestList = newObjectList();
        currentMap.put(mapKey, nestList);
        return nestList;
    }

    protected List<Object> setupNestList(List<Object> currentList) {
        final List<Object> nestList = newObjectList();
        currentList.add(nestList);
        return nestList;
    }

    protected Map<String, Object> newStringObjectMap() {
        return new LinkedHashMap<String, Object>();
    }

    protected List<Object> newObjectList() {
        return new ArrayList<Object>();
    }

    protected String getNewLineAndIndent() {
        return NEW_LINE + "    ";
    }

    protected int getDelimiterCount(String targetString, String delimiter) {
        int result = 0;
        for (int i = 0;;) {
            if (targetString.indexOf(delimiter, i) != -1) {
                result++;
                i = targetString.indexOf(delimiter, i) + 1;
            } else {
                break;
            }
        }
        if (result == 0) {
            result = -1;
        }
        return result;
    }
}