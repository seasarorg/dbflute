package org.seasar.dbflute.helper.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;
import org.seasar.dbflute.unit.DfDBFluteTestCase;

/**
 * 
 * @author jflute
 * @since 0.8.3 (2008/11/08 Saturday)
 */
public class DfStringKeyMapTest extends DfDBFluteTestCase {

    @Test
    public void test_flexibleKey_Tx() throws Exception {
        // ## Arrange ##
        Map<String, Integer> map = DfStringKeyMap.createAsFlexibleKey();

        // ## Act ##
        map.put("ABC_DEF", 1);
        map.put("GHI_JKL", 2);
        map.put("MNO_PQR", 3);

        // ## Assert ##
        log(map);
        assertEquals(3, map.size());
        assertEquals(1, map.get("ABC_DEF"));
        assertEquals(1, map.get("ABCDEF"));
        assertEquals(1, map.get("abcDEF"));
        assertEquals(1, map.get("abc_def"));
        assertEquals(1, map.get("ABC__DEF"));
        assertEquals(1, map.get("ABC_DEF_"));
        assertNull(map.get("ABC_dDEF"));
        assertEquals(2, map.get("GHI_JKL"));
        assertEquals(2, map.get("GHIJKL"));
        assertEquals(2, map.get("ghiJKL"));
        assertEquals(2, map.get("ghi_jkl"));
        assertEquals(2, map.get("GHI__JKL"));
        assertEquals(2, map.get("GHI_JKL_"));
        assertNull(map.get("GHI_jJKL"));
    }

    @Test
    public void test_caseInsensitiveKey_Tx() throws Exception {
        // ## Arrange ##
        Map<String, Integer> map = DfStringKeyMap.createAsCaseInsensitiveKey();

        // ## Act ##
        map.put("ABC_DEF", 1);
        map.put("GHI_JKL", 2);
        map.put("MNO_PQR", 3);

        // ## Assert ##
        log(map);
        assertEquals(3, map.size());
        assertEquals(1, map.get("ABC_DEF"));
        assertEquals(1, map.get("abc_def"));
        assertNull(map.get("ABCDEF"));
        assertNull(map.get("abcDEF"));
        assertNull(map.get("ABC__DEF"));
        assertNull(map.get("ABC_DEF_"));
    }
}
