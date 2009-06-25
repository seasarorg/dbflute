package org.seasar.dbflute.helper;

import java.util.LinkedHashMap;

import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.5.1 (2009/06/20 Saturday)
 */
public class StringKeyMapTest extends PlainTestCase {

    public void test_put_null() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> mapAsFlexible = StringKeyMap.createAsFlexible();
        StringKeyMap<Object> mapAsFlexibleConcurrent = StringKeyMap.createAsFlexibleConcurrent();

        // ## Act ##
        mapAsFlexible.put("aaa", null);
        try {
            mapAsFlexibleConcurrent.put("aaa", null);
            fail();
        } catch (NullPointerException e) {
            // OK
        }

        // ## Assert ##
        assertEquals(null, mapAsFlexibleConcurrent.get("aaa"));
    }

    public void test_putAll() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsCaseInsensitive();
        LinkedHashMap<String, Integer> resourceMap = new LinkedHashMap<String, Integer>();
        resourceMap.put("aaa", 1);
        resourceMap.put("bbb", 2);
        resourceMap.put("ccc", 3);

        // ## Act ##
        map.putAll(resourceMap);

        // ## Assert ##
        assertEquals(1, map.get("aaa"));
        assertEquals(2, map.get("bbb"));
        assertEquals(3, map.get("ccc"));
        assertEquals(3, map.size());
    }
}
