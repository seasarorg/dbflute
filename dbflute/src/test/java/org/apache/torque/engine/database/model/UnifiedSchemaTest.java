package org.apache.torque.engine.database.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 */
public class UnifiedSchemaTest extends PlainTestCase {

    @Test
    public void test_equals_basic() {
        // ## Arrange ##
        UnifiedSchema first = createTarget("foo", "bar");
        UnifiedSchema second = createTarget("foo", "bar");

        // ## Act ##
        boolean actual = first.equals(second);

        // ## Assert ##
        assertTrue(actual);
    }

    @Test
    public void test_equals_catalog_diff() {
        // ## Arrange ##
        UnifiedSchema first = createTarget("foo", "bar");
        UnifiedSchema second = createTarget("baz", "bar");

        // ## Act ##
        boolean actual = first.equals(second);

        // ## Assert ##
        assertFalse(actual);
    }

    @Test
    public void test_equals_schema_diff() {
        // ## Arrange ##
        UnifiedSchema first = createTarget("foo", "bar");
        UnifiedSchema second = createTarget("foo", "baz");

        // ## Act ##
        boolean actual = first.equals(second);

        // ## Assert ##
        assertFalse(actual);
    }

    @Test
    public void test_equals_null() {
        // ## Arrange ##
        UnifiedSchema first = createTarget("foo", "bar");
        UnifiedSchema second = null;

        // ## Act ##
        boolean actual = first.equals(second);

        // ## Assert ##
        assertFalse(actual);
    }

    protected UnifiedSchema createTarget(String catalog, String schema) {
        return new UnifiedSchema(catalog, schema) {
            @Override
            protected boolean isCompletelyUnsupportedDBMS() {
                return false;
            }
        };
    }
}
