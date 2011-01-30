package org.seasar.dbflute.logic.sql2entity.analyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.8.0 (2011/01/28 Friday)
 */
public class DfParameterBeanResolverTest extends PlainTestCase {

    @Test
    public void test_doDerivePropertyTypeFromTestValue_String() throws Exception {
        // ## Arrange ##
        DfParameterBeanResolver target = createTarget();
        String strType = "String";

        // ## Act && Assert ##
        assertEquals(strType, target.doDerivePropertyTypeFromTestValue("'foo'"));
        assertEquals(strType, target.doDerivePropertyTypeFromTestValue("'foo%'"));
        assertEquals(strType, target.doDerivePropertyTypeFromTestValue("'%foo%'"));
        assertEquals(strType, target.doDerivePropertyTypeFromTestValue("'%foo%'"));
        assertEquals(strType, target.doDerivePropertyTypeFromTestValue("'0'"));
    }

    @Test
    public void test_doDerivePropertyTypeFromTestValue_Number() throws Exception {
        // ## Arrange ##
        DfParameterBeanResolver target = createTarget();
        String integerType = "Integer";
        String longType = "Long";
        String bigDecimalType = "BigDecimal";

        // ## Act && Assert ##
        assertEquals(integerType, target.doDerivePropertyTypeFromTestValue("0"));
        assertEquals(integerType, target.doDerivePropertyTypeFromTestValue("123"));
        assertEquals(integerType, target.doDerivePropertyTypeFromTestValue("" + Integer.MAX_VALUE));
        assertEquals(longType, target.doDerivePropertyTypeFromTestValue("" + Long.valueOf(Integer.MAX_VALUE + 1L)));
        assertEquals(bigDecimalType, target.doDerivePropertyTypeFromTestValue("123.45"));
    }

    @Test
    public void test_doDerivePropertyTypeFromTestValue_Date() throws Exception {
        // ## Arrange ##
        DfParameterBeanResolver target = createTarget();
        String dateType = "Date";
        String timestampType = "Timestamp";
        String timeType = "Time";

        // ## Act && Assert ##
        assertEquals(dateType, target.doDerivePropertyTypeFromTestValue("date'foo'"));
        assertEquals(dateType, target.doDerivePropertyTypeFromTestValue("date 'foo'"));
        assertEquals(timestampType, target.doDerivePropertyTypeFromTestValue("timestamp'foo'"));
        assertEquals(timestampType, target.doDerivePropertyTypeFromTestValue("timestamp 'foo'"));
        assertEquals(timeType, target.doDerivePropertyTypeFromTestValue("time'foo'"));
        assertEquals(timeType, target.doDerivePropertyTypeFromTestValue("time 'foo'"));
        assertEquals(dateType, target.doDerivePropertyTypeFromTestValue("'2011-01-28'"));
        assertEquals(dateType, target.doDerivePropertyTypeFromTestValue("'2011-01-28 00:00:00'"));
        assertEquals(dateType, target.doDerivePropertyTypeFromTestValue("'2011-01-28 00:00:00.000'"));
        assertEquals(timestampType, target.doDerivePropertyTypeFromTestValue("'2011-01-28 00:00:00.001'"));
        assertEquals(timestampType, target.doDerivePropertyTypeFromTestValue("'2011-01-28 00:01:00.000'"));
        assertEquals(timeType, target.doDerivePropertyTypeFromTestValue("'12:34:56'"));
    }

    @Test
    public void test_doDerivePropertyTypeFromTestValue_List_Number() throws Exception {
        // ## Arrange ##
        DfParameterBeanResolver target = createTarget();
        String strType = "List<String>";
        String integerType = "List<Integer>";
        String longType = "List<Long>";
        String bigDecimalType = "List<BigDecimal>";

        // ## Act && Assert ##
        assertEquals(strType, target.doDerivePropertyTypeFromTestValue("('foo')"));
        assertEquals(strType, target.doDerivePropertyTypeFromTestValue("('foo', 'bar')"));
        assertEquals(strType, target.doDerivePropertyTypeFromTestValue("('foo', 0)"));
        assertEquals(integerType, target.doDerivePropertyTypeFromTestValue("(0)"));
        assertEquals(integerType, target.doDerivePropertyTypeFromTestValue("(0, 123)"));
        assertEquals(integerType, target.doDerivePropertyTypeFromTestValue("(0, 'foo')"));
        assertEquals(integerType, target.doDerivePropertyTypeFromTestValue("(12345, 123)"));
        assertEquals(integerType, target.doDerivePropertyTypeFromTestValue("(12345, 123.45)"));
        assertEquals(longType, target.doDerivePropertyTypeFromTestValue("(123456789012, 123)"));
        assertEquals(bigDecimalType, target.doDerivePropertyTypeFromTestValue("(12.345, 123)"));
    }

    @Test
    public void test_doDerivePropertyTypeFromTestValue_illegal() throws Exception {
        // ## Arrange ##
        DfParameterBeanResolver target = createTarget();
        String strType = "String";

        // ## Act && Assert ##
        try {
            target.doDerivePropertyTypeFromTestValue(null);

            fail();
        } catch (IllegalArgumentException e) {
            // OK
            log(e.getMessage());
        }
        assertEquals(strType, target.doDerivePropertyTypeFromTestValue(""));
        assertEquals(strType, target.doDerivePropertyTypeFromTestValue(" "));
        assertEquals(strType, target.doDerivePropertyTypeFromTestValue("a12.34"));
        assertEquals(strType, target.doDerivePropertyTypeFromTestValue("'20110128'"));
    }

    @Test
    public void test_doSwitchPlainTypeNameIfCSharp() throws Exception {
        // ## Arrange ##
        DfParameterBeanResolver target = createTarget();

        // ## Act && Assert ##
        assertEquals("String", target.doSwitchPlainTypeNameIfCSharp("String"));
        assertEquals("int?", target.doSwitchPlainTypeNameIfCSharp("Integer"));
        assertEquals("long?", target.doSwitchPlainTypeNameIfCSharp("Long"));
        assertEquals("decimal?", target.doSwitchPlainTypeNameIfCSharp("BigDecimal"));
        assertEquals("DateTime?", target.doSwitchPlainTypeNameIfCSharp("Date"));
        assertEquals("DateTime?", target.doSwitchPlainTypeNameIfCSharp("Timestamp"));
        assertEquals("DateTime?", target.doSwitchPlainTypeNameIfCSharp("Time"));
        assertEquals("IList<String>", target.doSwitchPlainTypeNameIfCSharp("List<String>"));
        assertEquals("IList<int?>", target.doSwitchPlainTypeNameIfCSharp("List<Integer>"));
        assertEquals("IList<long?>", target.doSwitchPlainTypeNameIfCSharp("List<Long>"));
        assertEquals("IList<decimal?>", target.doSwitchPlainTypeNameIfCSharp("List<BigDecimal>"));
    }

    @Test
    public void test_derivePropertyOptionFromTestValue() throws Exception {
        // ## Arrange ##
        DfParameterBeanResolver target = createTarget();

        // ## Act && Assert ##
        assertEquals(null, target.derivePropertyOptionFromTestValue("'foo'"));
        assertEquals("likePrefix", target.derivePropertyOptionFromTestValue("'foo%'"));
        assertEquals("likeSuffix", target.derivePropertyOptionFromTestValue("'%foo'"));
        assertEquals("likeContain", target.derivePropertyOptionFromTestValue("'%foo%'"));
        assertEquals("like", target.derivePropertyOptionFromTestValue("'%f%oo%'"));
        assertEquals("like", target.derivePropertyOptionFromTestValue("'f%oo'"));
        assertEquals("like", target.derivePropertyOptionFromTestValue("'f%oo%'"));
    }

    protected DfParameterBeanResolver createTarget() {
        return new DfParameterBeanResolver(null, null, null);
    }
}
