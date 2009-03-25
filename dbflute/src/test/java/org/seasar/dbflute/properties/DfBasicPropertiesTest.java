package org.seasar.dbflute.properties;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;
import org.seasar.dbflute.unit.PlainTestCase;

public class DfBasicPropertiesTest extends PlainTestCase {

    @Test
    public void test_conditionBeanPackage() {
        // ## Arrange ##
        Properties prop = new Properties();
        prop.setProperty("torque.packageBase", "test.base");
        prop.setProperty("torque.conditionBeanPackage", "test.cbean");
        DfBasicProperties packageProperties = new DfBasicProperties(prop);

        // ## Act ##
        String conditionBeanPackage = packageProperties.getConditionBeanPackage();

        // ## Assert ##
        assertEquals("test.base.test.cbean", conditionBeanPackage);
    }

    @Test
    public void test_extendedConditionBeanPackage_witn_conditionBeanPackage() {
        // ## Arrange ##
        Properties prop = new Properties();
        prop.setProperty("torque.packageBase", "test.base");
        prop.setProperty("torque.conditionBeanPackage", "test.cbean");
        prop.setProperty("torque.extendedConditionBeanPackage", "extended.cbean");
        DfBasicProperties packageProperties = new DfBasicProperties(prop);

        // ## Act ##
        String conditionBeanPackage = packageProperties.getConditionBeanPackage();
        String extendedConditionBeanPackage = packageProperties.getExtendedConditionBeanPackage();

        // ## Assert ##
        assertEquals("test.base.test.cbean", conditionBeanPackage);
        assertEquals("test.base.extended.cbean", extendedConditionBeanPackage);
    }

    @Test
    public void test_extendedConditionBeanPackage_without_conditionBeanPackage() {
        // ## Arrange ##
        Properties prop = new Properties();
        prop.setProperty("torque.packageBase", "test.base");
        prop.setProperty("torque.extendedConditionBeanPackage", "extended.cbean");
        DfBasicProperties packageProperties = new DfBasicProperties(prop);

        // ## Act ##
        String extendedConditionBeanPackage = packageProperties.getExtendedConditionBeanPackage();

        // ## Assert ##
        assertEquals("test.base.extended.cbean", extendedConditionBeanPackage);
    }

    @Test
    public void test_extendedConditionBeanPackage_default_same_as_conditionBeanPackage() {
        // ## Arrange ##
        Properties prop = new Properties();
        prop.setProperty("torque.packageBase", "test.base");
        prop.setProperty("torque.conditionBeanPackage", "test.cbean");
        DfBasicProperties packageProperties = new DfBasicProperties(prop);

        // ## Act ##
        String conditionBeanPackage = packageProperties.getConditionBeanPackage();
        String extendedConditionBeanPackage = packageProperties.getExtendedConditionBeanPackage();

        // ## Assert ##
        assertEquals("test.base.test.cbean", conditionBeanPackage);
        assertEquals(conditionBeanPackage, extendedConditionBeanPackage);
    }
}
