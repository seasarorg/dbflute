package org.apache.torque.engine.database.model;

import org.junit.Assert;
import org.junit.Test;

public class ForeignKeyTest {

    @Test
    public void test_xxx() throws Exception {
        final ForeignKey foreignKey = new ForeignKey();
        final String toString = foreignKey.toString();
        System.out.println(toString);
        Assert.assertNotNull(toString);
    }
}
