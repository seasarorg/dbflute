package org.apache.torque.engine.database.model;

import org.junit.Assert;
import org.junit.Test;
import org.seasar.dbflute.unit.DfDBFluteTestCase;

public class ForeignKeyTest extends DfDBFluteTestCase {

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
