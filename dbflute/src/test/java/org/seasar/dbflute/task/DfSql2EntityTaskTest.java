package org.seasar.dbflute.task;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DfSql2EntityTaskTest {

    @Test
    public void test_needsConvert() throws Exception {
        // ## Arrange ##
        final DfSql2EntityTask task = new DfSql2EntityTask();

        // ## Act & Assert ##
        assertTrue(task.needsConvert("ADDRESS"));
        assertFalse(task.needsConvert("address"));
        assertTrue(task.needsConvert("ADDRESS1"));
        assertFalse(task.needsConvert("address1"));
    }
}
