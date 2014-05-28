package org.seasar.dbflute.logic.jdbc.metadata.basic;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.unit.core.PlainTestCase;

/**
 * @author jflute
 */
public class DfUniqueKeyExtractorTest extends PlainTestCase {

    public void test_removePkMatchUniqueKey_simple() throws Exception {
        // ## Arrange ##
        DfUniqueKeyExtractor extractor = new DfUniqueKeyExtractor();
        List<String> pkList = newArrayList("FOO");
        Map<String, Map<Integer, String>> uniqueKeyMap = newHashMap();
        {
            Map<Integer, String> elementMap = newLinkedHashMap();
            elementMap.put(1, "1ST");
            elementMap.put(2, "2ND");
            elementMap.put(3, "3RD");
            uniqueKeyMap.put("FIRST", elementMap);
        }
        {
            Map<Integer, String> elementMap = newLinkedHashMap();
            elementMap.put(1, "FOO");
            uniqueKeyMap.put("SECOND", elementMap);
        }

        // ## Act ##
        extractor.removePkMatchUniqueKey("TEST", pkList, uniqueKeyMap);

        // ## Assert ##
        log(uniqueKeyMap);
        assertTrue(uniqueKeyMap.containsKey("FIRST"));
        assertFalse(uniqueKeyMap.containsKey("SECOND"));
    }

    public void test_removePkMatchUniqueKey_compound() throws Exception {
        // ## Arrange ##
        DfUniqueKeyExtractor extractor = new DfUniqueKeyExtractor();
        List<String> pkList = newArrayList("FOO", "BAR");
        Map<String, Map<Integer, String>> uniqueKeyMap = newHashMap();
        {
            Map<Integer, String> elementMap = newLinkedHashMap();
            elementMap.put(1, "1ST");
            elementMap.put(2, "2ND");
            elementMap.put(3, "3RD");
            uniqueKeyMap.put("FIRST", elementMap);
        }
        {
            Map<Integer, String> elementMap = newLinkedHashMap();
            elementMap.put(1, "FOO");
            elementMap.put(2, "BAR");
            uniqueKeyMap.put("SECOND", elementMap);
        }

        // ## Act ##
        extractor.removePkMatchUniqueKey("TEST", pkList, uniqueKeyMap);

        // ## Assert ##
        assertTrue(uniqueKeyMap.containsKey("FIRST"));
        assertFalse(uniqueKeyMap.containsKey("SECOND"));
    }
}
