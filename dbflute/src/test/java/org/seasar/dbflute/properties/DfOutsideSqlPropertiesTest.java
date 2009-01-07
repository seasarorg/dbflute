package org.seasar.dbflute.properties;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;
import org.seasar.dbflute.unit.DfDBFluteTestCase;

/**
 * @author jflute
 * @since 0.8.3 (2008/11/17 Monday)
 */
public class DfOutsideSqlPropertiesTest extends DfDBFluteTestCase {

    @Test
    public void test_resolveSqlPackageFileSeparatorWithFlatDirectory_contains() {
        // ## Arrange ##
        final DfOutsideSqlDefinitionProperties prop = new DfOutsideSqlDefinitionProperties(new Properties());

        // ## Act ##
        final String actual = prop.resolveSqlPackageFileSeparatorWithFlatDirectory("abc.def.ghi.dbflute", "def.ghi");

        // ## Assert ##
        log(actual);
        assertEquals("abc/def.ghi/dbflute", actual);
    }

    @Test
    public void test_resolveFileSeparatorWithFlatDirectory_startsWith() {
        // ## Arrange ##
        final DfOutsideSqlDefinitionProperties prop = new DfOutsideSqlDefinitionProperties(new Properties());

        // ## Act ##
        final String actual = prop.resolveSqlPackageFileSeparatorWithFlatDirectory("abc.def.ghi.dbflute", "abc.def");

        // ## Assert ##
        log(actual);
        assertEquals("abc.def/ghi/dbflute", actual);
    }

    @Test
    public void test_resolveFileSeparatorWithFlatDirectory_endsWith() {
        // ## Arrange ##
        final DfOutsideSqlDefinitionProperties prop = new DfOutsideSqlDefinitionProperties(new Properties());

        // ## Act ##
        final String actual = prop
                .resolveSqlPackageFileSeparatorWithFlatDirectory("abc.def.ghi.dbflute", "ghi.dbflute");

        // ## Assert ##
        log(actual);
        assertEquals("abc/def/ghi.dbflute", actual);
    }
}
