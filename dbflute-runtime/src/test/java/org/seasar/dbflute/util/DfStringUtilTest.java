package org.seasar.dbflute.util;

import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.5 (2009/04/10 Friday)
 */
public class DfStringUtilTest extends PlainTestCase {

    public void test_extractFirstScope_basic() {
        assertEquals("BAR", DfStringUtil.extractFirstScope("FOObeginBARendDODO", "begin", "end"));
        assertEquals("BAR", DfStringUtil.extractFirstScope("FOObeginBARend", "begin", "end"));
        assertEquals("BAR", DfStringUtil.extractFirstScope("beginBARendDODO", "begin", "end"));
        assertEquals(null, DfStringUtil.extractFirstScope("beginBARedDODO", "begin", "end"));
        assertEquals(null, DfStringUtil.extractFirstScope("begnBARendDODO", "begin", "end"));
        assertEquals(null, DfStringUtil.extractFirstScope("begnBARedDODO", "begin", "end"));
    }
}
