package org.apache.torque.engine.database.model;

import org.junit.Assert;
import org.junit.Test;
import org.seasar.dbflute.unit.PlainTestCase;

public class ForeignKeyTest extends PlainTestCase {

    @Test
    public void test_toString() {
        // ## Arrange ##
        final ForeignKey foreignKey = new ForeignKey();
        
        // ## Act ##
        final String actual = foreignKey.toString();
        
        // ## Assert ##
        log(actual);
        Assert.assertNotNull(actual);
    }
}
