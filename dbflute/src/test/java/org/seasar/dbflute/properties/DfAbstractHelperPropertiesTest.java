package org.seasar.dbflute.properties;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author jflute
 * @since 0.9.5.1 (2009/06/06 Saturday)
 */
public class DfAbstractHelperPropertiesTest {

    @Test
    public void test_deriveBooleanAnotherKey() {
        assertEquals("aaa", DfAbstractHelperProperties.deriveBooleanAnotherKey("isAaa"));
        assertEquals(null, DfAbstractHelperProperties.deriveBooleanAnotherKey("aaa"));
        assertEquals(null, DfAbstractHelperProperties.deriveBooleanAnotherKey("isaaa"));
        assertEquals(null, DfAbstractHelperProperties.deriveBooleanAnotherKey("is"));
    }
}
