package org.seasar.dbflute;

import org.seasar.dbflute.Entity.InternalUtil;
import org.seasar.dbflute.unit.core.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.6.9 (2010/05/13 Thursday)
 */
public class EntityTest extends PlainTestCase {

    public void test_InternalUtil_convertEmptyToNull() {
        assertNull(InternalUtil.convertEmptyToNull(null));
        assertNull(InternalUtil.convertEmptyToNull(""));
        assertNotNull(InternalUtil.convertEmptyToNull(" "));
        assertNotNull(InternalUtil.convertEmptyToNull("a"));
    }
}
