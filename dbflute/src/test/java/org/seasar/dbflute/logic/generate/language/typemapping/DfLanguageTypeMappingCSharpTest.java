package org.seasar.dbflute.logic.generate.language.typemapping;

import org.seasar.dbflute.unit.core.PlainTestCase;

/**
 * @author jflute
 */
public class DfLanguageTypeMappingCSharpTest extends PlainTestCase {

    public void test_switchParameterBeanTestValueType() throws Exception {
        // ## Arrange ##
        DfLanguageTypeMappingCSharp target = createTarget();

        // ## Act && Assert ##
        assertEquals("String", target.switchParameterBeanTestValueType("String"));
        assertEquals("int?", target.switchParameterBeanTestValueType("Integer"));
        assertEquals("long?", target.switchParameterBeanTestValueType("Long"));
        assertEquals("decimal?", target.switchParameterBeanTestValueType("BigDecimal"));
        assertEquals("DateTime?", target.switchParameterBeanTestValueType("Date"));
        assertEquals("DateTime?", target.switchParameterBeanTestValueType("Timestamp"));
        assertEquals("DateTime?", target.switchParameterBeanTestValueType("Time"));
        assertEquals("IList<String>", target.switchParameterBeanTestValueType("List<String>"));
        assertEquals("IList<int?>", target.switchParameterBeanTestValueType("List<Integer>"));
        assertEquals("IList<long?>", target.switchParameterBeanTestValueType("List<Long>"));
        assertEquals("IList<decimal?>", target.switchParameterBeanTestValueType("List<BigDecimal>"));
    }

    protected DfLanguageTypeMappingCSharp createTarget() {
        return new DfLanguageTypeMappingCSharp();
    }
}
