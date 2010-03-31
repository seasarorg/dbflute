package org.seasar.dbflute.util;

import static org.seasar.dbflute.util.DfStringUtil.camelize;
import static org.seasar.dbflute.util.DfStringUtil.decamelize;
import static org.seasar.dbflute.util.DfStringUtil.extractFirstScope;
import static org.seasar.dbflute.util.DfStringUtil.removeLineComment;
import static org.seasar.dbflute.util.DfStringUtil.rtrim;
import static org.seasar.dbflute.util.DfStringUtil.splitList;
import static org.seasar.dbflute.util.DfStringUtil.splitListTrimmed;
import static org.seasar.dbflute.util.DfStringUtil.toBeansPropertyName;

import java.util.List;

import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.5 (2009/04/10 Friday)
 */
public class DfStringUtilTest extends PlainTestCase {

    public void test_splitList() {
        String ln = DfSystemUtil.getLineSeparator();
        List<String> splitList = splitList("aaa" + ln + "bbb" + ln + "ccc", ln);
        assertEquals("aaa", splitList.get(0));
        assertEquals("bbb", splitList.get(1));
        assertEquals("ccc", splitList.get(2));
    }

    public void test_splitList_notTrim() {
        String ln = DfSystemUtil.getLineSeparator();
        List<String> splitList = DfStringUtil.splitList("aaa " + ln + "bbb" + ln + " ccc", ln);
        assertEquals("aaa ", splitList.get(0));
        assertEquals("bbb", splitList.get(1));
        assertEquals(" ccc", splitList.get(2));
    }

    public void test_splitListTrimmed_trim() {
        String ln = DfSystemUtil.getLineSeparator();
        List<String> splitList = splitListTrimmed("aaa " + ln + "bbb" + ln + " ccc", ln);
        assertEquals("aaa", splitList.get(0));
        assertEquals("bbb", splitList.get(1));
        assertEquals("ccc", splitList.get(2));
    }

    // ===================================================================================
    //                                                                                Trim
    //                                                                                ====
    public void test_rtrim_default() {
        assertNull(rtrim(null));
        assertEquals(" foo", rtrim(" foo "));
        assertEquals(" foo", rtrim(" foo \n "));
        assertEquals(" foo", rtrim(" foo \n \n"));
        assertEquals(" foo", rtrim(" foo \r\n "));
        assertEquals(" foo", rtrim(" foo \r\n \r\n"));
    }

    public void test_rtrim_originalTrimTarget() {
        assertNull(DfStringUtil.rtrim(null, "\n"));
        assertEquals(" foo ", DfStringUtil.rtrim(" foo \n", "\n"));
        assertEquals(" foo \n ", DfStringUtil.rtrim(" foo \n ", "\n"));
        assertEquals(" foo \r", DfStringUtil.rtrim(" foo \r\n", "\n"));
        assertEquals(" foo ", DfStringUtil.rtrim(" foo \r\n", "\r\n"));
    }

    // ===================================================================================
    //                                                                      Scope Handling
    //                                                                      ==============
    public void test_extractFirstScope_basic() {
        assertEquals("BAR", DfStringUtil.extractFirstScope("FOObeginBARendDODO", "begin", "end"));
        assertEquals("BAR", extractFirstScope("FOObeginBARend", "begin", "end"));
        assertEquals("BAR", DfStringUtil.extractFirstScope("beginBARendDODO", "begin", "end"));
        assertEquals(null, DfStringUtil.extractFirstScope("beginBARedDODO", "begin", "end"));
        assertEquals(null, DfStringUtil.extractFirstScope("begnBARendDODO", "begin", "end"));
        assertEquals(null, DfStringUtil.extractFirstScope("begnBARedDODO", "begin", "end"));
    }

    // ===================================================================================
    //                                                                       Name Handling
    //                                                                       =============
    public void test_camelize_basic() {
        assertEquals("FooName", DfStringUtil.camelize("FOO_NAME"));
        assertEquals("FooName", DfStringUtil.camelize("foo_name"));
        assertEquals("FooNameBar", DfStringUtil.camelize("foo_nameBar"));
        assertEquals("FooBar", DfStringUtil.camelize("foo_Bar"));
        assertEquals("FooNBar", camelize("foo_nBar"));
        assertEquals("FooNBar", DfStringUtil.camelize("FOO_nBar"));
        assertEquals("Foo", DfStringUtil.camelize("FOO"));
    }

    public void test_decamelize_basic() {
        assertEquals("FOO_NAME", DfStringUtil.decamelize("FooName"));
        assertEquals("FOO_NAME", decamelize("fooName"));
        assertEquals("F", DfStringUtil.decamelize("f"));
        assertEquals("F_O_O__NAME_BAR", DfStringUtil.decamelize("FOO_NameBar"));
        assertEquals("FOO__NAME_BAR", DfStringUtil.decamelize("foo_NameBar"));
        assertEquals("F_O_O__N_A_M_E", DfStringUtil.decamelize("FOO_NAME"));
        assertEquals("FOO_NAME", DfStringUtil.decamelize("foo_name"));
    }

    public void test_toBeanPropertyName_basic() {
        assertEquals("fooName", DfStringUtil.toBeansPropertyName("FooName"));
        assertEquals("fooName", DfStringUtil.toBeansPropertyName("fooName"));
        assertEquals("BFooName", toBeansPropertyName("BFooName"));
        assertEquals("BFooName", DfStringUtil.toBeansPropertyName("bFooName"));
        assertEquals("bbFooName", DfStringUtil.toBeansPropertyName("bbFooName"));
        assertEquals("f", DfStringUtil.toBeansPropertyName("f"));
        assertEquals("f", DfStringUtil.toBeansPropertyName("F"));
        assertEquals("fooName", DfStringUtil.toBeansPropertyName("FOO_NAME"));
        assertEquals("fooName", DfStringUtil.toBeansPropertyName("foo_name"));
    }

    // ===================================================================================
    //                                                                        SQL Handling
    //                                                                        ============
    public void test_removeLineComment_Lf() throws Exception {
        String sql = "aaaa\n";
        sql = sql + "bbbb\n";
        sql = sql + "--\n";
        sql = sql + "cccc\n";
        String removed = removeLineComment(sql);
        System.out.println(removed);
        assertEquals("aaaa\nbbbb\ncccc\n", removed);
    }

    public void test_removeLineComment_CrLf() throws Exception {
        String sql = "aaaa\r\n";
        sql = sql + "bbbb\r\n";
        sql = sql + "--\r\n";
        sql = sql + "cccc\r\n";
        String removed = DfStringUtil.removeLineComment(sql);
        System.out.println(removed);
        assertFalse(removed.contains("--"));
        assertFalse(removed.contains("\r"));
        assertEquals("aaaa\nbbbb\ncccc\n", removed);
    }
}
