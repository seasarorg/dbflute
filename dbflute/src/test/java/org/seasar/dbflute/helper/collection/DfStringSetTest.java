package org.seasar.dbflute.helper.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;
import org.seasar.dbflute.unit.DfDBFluteTestCase;

/**
 * @author jflute
 * @since 0.8.3 (2008/11/08 Saturday)
 */
public class DfStringSetTest extends DfDBFluteTestCase {

    @Test
    public void test_flexible_Tx() throws Exception {
        // ## Arrange ##
        Set<String> set = DfStringSet.createAsFlexible();

        // ## Act ##
        set.add("ABC_DEF");
        set.add("GHI_JKL");
        set.add("MNO_PQR");

        // ## Assert ##
        log(set);
        assertEquals(3, set.size());
        assertTrue(set.contains("ABC_DEF"));
        assertTrue(set.contains("abcDEF"));
        assertTrue(set.contains("abc_def"));
        assertTrue(set.contains("ABC__DEF"));
        assertTrue(set.contains("ABC_DEF_"));
        assertFalse(set.contains("ABC_dDEF"));
        assertTrue(set.contains("GHI_JKL"));
        assertTrue(set.contains("ghiJKL"));
        assertTrue(set.contains("ghi_jkl"));
        assertTrue(set.contains("GHI__JKL"));
        assertTrue(set.contains("GHI_JKL_"));
        assertFalse(set.contains("GHI_jJKL"));
    }

    @Test
    public void test_caseInsensitive_Tx() throws Exception {
        // ## Arrange ##
        Set<String> set = DfStringSet.createAsCaseInsensitive();

        // ## Act ##
        set.add("ABC_DEF");
        set.add("GHI_JKL");
        set.add("MNO_PQR");

        // ## Assert ##
        log(set);
        assertEquals(3, set.size());
        assertTrue(set.contains("ABC_DEF"));
        assertFalse(set.contains("abcDEF"));
        assertTrue(set.contains("abc_def"));
        assertFalse(set.contains("ABC__DEF"));
        assertFalse(set.contains("ABC_DEF_"));
        assertFalse(set.contains("ABC_dDEF"));
        assertTrue(set.contains("GHI_JKL"));
        assertFalse(set.contains("ghiJKL"));
        assertTrue(set.contains("ghi_jkl"));
        assertFalse(set.contains("GHI__JKL"));
        assertFalse(set.contains("GHI_JKL_"));
        assertFalse(set.contains("GHI_jJKL"));
    }
}
