package org.seasar.dbflute.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author jflute
 * @since 0.9.5 (2009/04/21 Tuesday)
 */
public class DfTypeMappingPropertiesTest {

    @Test
    public void test_isNameTypeMappingKey() {
        assertTrue(DfTypeMappingProperties.isNameTypeMappingKey("$$foo$$"));
        assertTrue(DfTypeMappingProperties.isNameTypeMappingKey("$$$foo$$$"));
        assertFalse(DfTypeMappingProperties.isNameTypeMappingKey("$foo$$"));
        assertFalse(DfTypeMappingProperties.isNameTypeMappingKey("$$foo$"));
        assertFalse(DfTypeMappingProperties.isNameTypeMappingKey("FOO"));
    }

    @Test
    public void test_extractDbTypeName() {
        assertEquals("foo", DfTypeMappingProperties.extractDbTypeName("$$foo$$"));
        assertEquals("$foo$", DfTypeMappingProperties.extractDbTypeName("$$$foo$$$"));
    }
}
