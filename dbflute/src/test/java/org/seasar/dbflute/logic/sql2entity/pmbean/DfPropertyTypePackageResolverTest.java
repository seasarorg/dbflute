package org.seasar.dbflute.logic.sql2entity.pmbean;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 */
public class DfPropertyTypePackageResolverTest extends PlainTestCase {

    @Test
    public void test_doResolvePackageName_Java_Date_basic() throws Exception {
        // ## Arrange ##
        DfPropertyTypePackageResolver resolver = createJavaTarget();

        // ## Act & Assert ##
        assertEquals("java.util.Date", resolver.doResolvePackageName("Date", false));
        assertEquals("Date", resolver.doResolvePackageName("Date", true));
    }

    @Test
    public void test_doResolvePackageName_Java_List_basic() throws Exception {
        // ## Arrange ##
        DfPropertyTypePackageResolver resolver = createJavaTarget();

        // ## Act & Assert ##
        assertEquals("java.util.List<String>", resolver.doResolvePackageName("List<String>", false));
        assertEquals("List<String>", resolver.doResolvePackageName("List<String>", true));
    }

    @Test
    public void test_doResolvePackageName_Java_List_nest() throws Exception {
        // ## Arrange ##
        DfPropertyTypePackageResolver resolver = createJavaTarget();

        // ## Act ##
        String actual = resolver.doResolvePackageName("List<List<Date>>", false);

        // ## Assert ##
        assertEquals("java.util.List<java.util.List<java.util.Date>>", actual);
    }

    @Test
    public void test_doResolvePackageName_Java_List_nest_Map() throws Exception {
        // ## Arrange ##
        DfPropertyTypePackageResolver resolver = createJavaTarget();

        // ## Act ##
        String actual = resolver.doResolvePackageName("List<List<Map<Date, Date>>>", false);

        // ## Assert ##
        log(actual);
        assertEquals("java.util.List<java.util.List<java.util.Map<Date, java.util.Date>>>", actual);
    }

    @Test
    public void test_doResolvePackageName_Java_Map_basic() throws Exception {
        // ## Arrange ##
        DfPropertyTypePackageResolver resolver = createJavaTarget();

        // ## Act & Assert ##
        assertEquals("java.util.Map<String, String>", resolver.doResolvePackageName("Map<String, String>", false));
        assertEquals("java.util.Map<String, java.util.Date>", resolver.doResolvePackageName("Map<String, Date>", false));
    }

    @Test
    public void test_doResolvePackageName_Java_Map_nest() throws Exception {
        // ## Arrange ##
        DfPropertyTypePackageResolver resolver = createJavaTarget();

        // ## Act ##
        String actual = resolver.doResolvePackageName("Map<Date, List<Map<Date, Date>>>", false);

        // ## Assert ##
        log(actual);
        assertEquals("java.util.Map<Date, java.util.List<java.util.Map<Date, java.util.Date>>>", actual);
    }

    @Test
    public void test_doResolvePackageName_CSharp_List_nest_Map() throws Exception {
        // ## Arrange ##
        DfPropertyTypePackageResolver resolver = createCSharpTarget();

        // ## Act ##
        String actual = resolver.doResolvePackageName("IList<IList<IDictionary<Date, Date>>>", false);

        // ## Assert ##
        log(actual);
        assertEquals(
                "System.Collections.Generic.IList<System.Collections.Generic.IList<System.Collections.Generic.IDictionary<Date, Date>>>",
                actual);
    }

    protected DfPropertyTypePackageResolver createJavaTarget() {
        return new DfPropertyTypePackageResolver() {
            @Override
            protected boolean isTargetLanguageJava() {
                return true;
            }

            @Override
            protected boolean isTargetLanguageCSharp() {
                return false;
            }
        };
    }

    protected DfPropertyTypePackageResolver createCSharpTarget() {
        return new DfPropertyTypePackageResolver() {
            @Override
            protected boolean isTargetLanguageJava() {
                return false;
            }

            @Override
            protected boolean isTargetLanguageCSharp() {
                return true;
            }
        };
    }
}
