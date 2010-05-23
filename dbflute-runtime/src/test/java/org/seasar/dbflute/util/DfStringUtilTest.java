package org.seasar.dbflute.util;

import static org.seasar.dbflute.util.Srl.camelize;
import static org.seasar.dbflute.util.Srl.connectPrefix;
import static org.seasar.dbflute.util.Srl.connectSuffix;
import static org.seasar.dbflute.util.Srl.count;
import static org.seasar.dbflute.util.Srl.decamelize;
import static org.seasar.dbflute.util.Srl.extractScopeFirst;
import static org.seasar.dbflute.util.Srl.extractScopeList;
import static org.seasar.dbflute.util.Srl.initBeansProp;
import static org.seasar.dbflute.util.Srl.ltrim;
import static org.seasar.dbflute.util.Srl.removeLineComment;
import static org.seasar.dbflute.util.Srl.rtrim;
import static org.seasar.dbflute.util.Srl.splitList;
import static org.seasar.dbflute.util.Srl.splitListTrimmed;
import static org.seasar.dbflute.util.Srl.substringFirstFront;
import static org.seasar.dbflute.util.Srl.substringFirstRear;
import static org.seasar.dbflute.util.Srl.substringLastFront;
import static org.seasar.dbflute.util.Srl.substringLastRear;
import static org.seasar.dbflute.util.Srl.trim;
import static org.seasar.dbflute.util.Srl.unquoteDouble;
import static org.seasar.dbflute.util.Srl.unquoteSingle;

import java.util.List;

import org.seasar.dbflute.unit.PlainTestCase;
import org.seasar.dbflute.util.Srl.ScopeInfo;

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
    public void test_trim_default() {
        assertEquals("foo", trim(" foo "));
        assertEquals("foo", trim("\n foo "));
        assertEquals("foo", trim("\n \n foo "));
        assertEquals("foo", trim(" \r\n foo "));
        assertEquals("foo", trim(" \r\n \r\n foo "));
    }

    public void test_trim_originalTrimTarget() {
        assertEquals(" foo ", trim("\n foo ", "\n"));
        assertEquals(" \n foo ", trim(" \n foo ", "\n"));
        assertEquals("\r foo ", trim("\n\r foo ", "\n"));
        assertEquals(" foo ", trim("\r\n foo ", "\r\n"));
        assertEquals("foo", trim("'foo'", "'"));
        assertEquals("f'o'o", trim("'f'o'o'", "'"));
        assertEquals("fo''o", trim("'fo''o'", "'"));
        assertEquals("foo", trim("\"foo\"", "\""));
        assertEquals("f\"o\"o", trim("\"f\"o\"o\"", "\""));
        assertEquals("fo\"\"o", trim("\"fo\"\"o\"", "\""));
    }

    public void test_ltrim_default() {
        assertEquals("foo ", ltrim(" foo "));
        assertEquals("foo ", ltrim("\n foo "));
        assertEquals("foo ", ltrim("\n \n foo "));
        assertEquals("foo ", ltrim(" \r\n foo "));
        assertEquals("foo ", ltrim(" \r\n \r\n foo "));
    }

    public void test_ltrim_originalTrimTarget() {
        assertEquals(" foo ", ltrim("\n foo ", "\n"));
        assertEquals(" \n foo ", ltrim(" \n foo ", "\n"));
        assertEquals("\r foo ", ltrim("\n\r foo ", "\n"));
        assertEquals(" foo ", ltrim("\r\n foo ", "\r\n"));
    }

    public void test_rtrim_default() {
        assertEquals(" foo", rtrim(" foo "));
        assertEquals(" foo", rtrim(" foo \n "));
        assertEquals(" foo", rtrim(" foo \n \n"));
        assertEquals(" foo", rtrim(" foo \r\n "));
        assertEquals(" foo", rtrim(" foo \r\n \r\n"));
    }

    public void test_rtrim_originalTrimTarget() {
        assertEquals(" foo ", DfStringUtil.rtrim(" foo \n", "\n"));
        assertEquals(" foo \n ", DfStringUtil.rtrim(" foo \n ", "\n"));
        assertEquals(" foo \r", DfStringUtil.rtrim(" foo \r\n", "\n"));
        assertEquals(" foo ", DfStringUtil.rtrim(" foo \r\n", "\r\n"));
    }

    // ===================================================================================
    //                                                                           SubString
    //                                                                           =========
    public void test_substringFirstFront_basic() {
        assertEquals("foo", substringFirstFront("foo.bar", "."));
        assertEquals("foo", substringFirstFront("foo.bar.don", "."));
        assertEquals("foobar", substringFirstFront("foobar", "."));
        assertEquals("foo/bar", substringFirstFront("foo/bar.don.moo", "."));
        assertEquals("foo", substringFirstFront("foo/bar.don.moo", ".", "/"));
        assertEquals("foo", substringFirstFront("foo.bar.don.moo", ".", "/"));
        assertEquals("foo", substringFirstFront("foo.bar.don.moo", "/", "."));
    }

    public void test_substringFirstRear_basic() {
        assertEquals("bar", substringFirstRear("foo.bar", "."));
        assertEquals("bar.don", substringFirstRear("foo.bar.don", "."));
        assertEquals("foobar", substringFirstRear("foobar", "."));
        assertEquals("don.moo", substringFirstRear("foo/bar.don.moo", "."));
        assertEquals("bar.don.moo", substringFirstRear("foo/bar.don.moo", ".", "/"));
        assertEquals("bar.don.moo", substringFirstRear("foo.bar.don.moo", ".", "/"));
        assertEquals("bar.don.moo", substringFirstRear("foo.bar.don.moo", "/", "."));
    }

    public void test_substringLastFront_basic() {
        assertEquals("foo", substringLastFront("foo.bar", "."));
        assertEquals("foo.bar", substringLastFront("foo.bar.don", "."));
        assertEquals("foobar", substringLastFront("foobar", "."));
        assertEquals("foo.bar", substringLastFront("foo.bar.don/moo", "."));
        assertEquals("foo.bar.don", substringLastFront("foo.bar.don/moo", ".", "/"));
        assertEquals("foo.bar.don", substringLastFront("foo.bar.don.moo", ".", "/"));
        assertEquals("foo.bar.don", substringLastFront("foo.bar.don.moo", "/", "."));

    }

    public void test_substringLastRear_basic() {
        assertEquals("bar", substringLastRear("foo.bar", "."));
        assertEquals("don", substringLastRear("foo.bar.don", "."));
        assertEquals("foobar", substringLastRear("foobar", "."));
        assertEquals("don/moo", substringLastRear("foo.bar.don/moo", "."));
        assertEquals("moo", substringLastRear("foo.bar.don/moo", ".", "/"));
        assertEquals("moo", substringLastRear("foo.bar.don.moo", ".", "/"));
        assertEquals("moo", substringLastRear("foo.bar.don.moo", "/", "."));
    }

    // ===================================================================================
    //                                                                               Count
    //                                                                               =====
    public void test_count_basic() {
        assertEquals(0, count("foobar", "."));
        assertEquals(1, count("foo.bar", "."));
        assertEquals(1, count("foo.bar", "foo"));
        assertEquals(2, count("foo.bar.baz", "."));
        assertEquals(4, count(".foo.bar.baz.", "."));
    }

    // ===================================================================================
    //                                                                             Connect
    //                                                                             =======
    public void test_connectPrefix_basic() {
        assertEquals("foo", connectPrefix("foo", null, "."));
        assertEquals("foo", connectPrefix("foo", "", "."));
        assertEquals("foo", connectPrefix("foo", " ", "."));
        assertEquals("bar.foo", connectPrefix("foo", "bar", "."));
        assertEquals("bar/foo", connectPrefix("foo", "bar", "/"));
    }

    public void test_connectSuffix_basic() {
        assertEquals("foo", connectSuffix("foo", null, "."));
        assertEquals("foo", connectSuffix("foo", "", "."));
        assertEquals("foo", connectSuffix("foo", " ", "."));
        assertEquals("foo.bar", connectSuffix("foo", "bar", "."));
        assertEquals("foo/bar", connectSuffix("foo", "bar", "/"));
    }

    // ===================================================================================
    //                                                                  Quotation Handling
    //                                                                  ==================
    public void test_unquoteSingle_basic() {
        assertEquals("", unquoteSingle(""));
        assertEquals("", unquoteSingle("''"));
        assertEquals("f", unquoteSingle("'f'"));
        assertEquals("foo", unquoteSingle("'foo'"));
        assertEquals("foo", unquoteSingle("foo"));
        assertEquals("\"foo\"", unquoteSingle("\"foo\""));
        assertEquals("'foo", unquoteSingle("'foo"));
        assertEquals("\"foo\"", unquoteSingle("'\"foo\"'"));
    }

    public void test_unquoteDouble_basic() {
        assertEquals("", unquoteDouble(""));
        assertEquals("", unquoteDouble("\"\""));
        assertEquals("f", unquoteDouble("\"f\""));
        assertEquals("foo", unquoteDouble("\"foo\""));
        assertEquals("foo", unquoteDouble("foo"));
        assertEquals("'foo'", unquoteDouble("'foo'"));
        assertEquals("\"foo", unquoteDouble("\"foo"));
        assertEquals("'foo'", unquoteDouble("\"'foo'\""));
    }

    // ===================================================================================
    //                                                                      Scope Handling
    //                                                                      ==============
    public void test_extractScopeFirst_content() {
        assertEquals("BAR", extractScopeFirst("FOObeginBARendDODO", "begin", "end").getContent());
        assertEquals("BAR", extractScopeFirst("FOObeginBARend", "begin", "end").getContent());
        assertEquals("BAR", extractScopeFirst("beginBARendDODO", "begin", "end").getContent());
        assertEquals(null, extractScopeFirst("beginBARedDODO", "begin", "end"));
        assertEquals(null, extractScopeFirst("begnBARendDODO", "begin", "end"));
        assertEquals(null, extractScopeFirst("begnBARedDODO", "begin", "end"));
        assertEquals("9", extractScopeFirst("get(9)", "get(", ")").getContent());
        assertEquals("99", extractScopeFirst("get(99)", "get(", ")").getContent());
        assertEquals(" 99 ", extractScopeFirst("get( 99 )", "get(", ")").getContent()); // not trimmed
        assertEquals("foo", extractScopeFirst("get(foo)-get(bar)", "get(", ")").getContent());
        assertEquals("foo", extractScopeFirst("@foo@-get@bar@", "@", "@").getContent());
    }

    public void test_extractScopeFirst_scope() {
        assertEquals("beginBARend", extractScopeFirst("FOObeginBARendDODO", "begin", "end").getScope());
        assertEquals("beginBARend", extractScopeFirst("FOObeginBARend", "begin", "end").getScope());
        assertEquals("beginBARend", extractScopeFirst("beginBARendDODO", "begin", "end").getScope());
        assertEquals(null, extractScopeFirst("beginBARedDODO", "begin", "end"));
        assertEquals(null, extractScopeFirst("begnBARendDODO", "begin", "end"));
        assertEquals(null, extractScopeFirst("begnBARedDODO", "begin", "end"));
        assertEquals("get(9)", extractScopeFirst("xget(9)x", "get(", ")").getScope());
        assertEquals("get(99)", extractScopeFirst("xget(99)x", "get(", ")").getScope());
        assertEquals("get( 99 )", extractScopeFirst("xget( 99 )x", "get(", ")").getScope()); // not trimmed
        assertEquals("get(foo)", extractScopeFirst("get(foo)-get(bar)", "get(", ")").getScope());
        assertEquals("@foo@", extractScopeFirst("@foo@-get@bar@", "@", "@").getScope());
    }

    public void test_extractScopeList_basic() {
        // ## Arrange ##
        String str = "baz/*BEGIN*/where /*FOR pmb*/ /*FIRST 'foo'*/member.../*END FOR*//* END */bar";

        // ## Act ##
        List<ScopeInfo> list = extractScopeList(str, "/*", "*/");

        // ## Assert ##
        assertEquals(5, list.size());
        assertEquals(str.indexOf("/*BEGIN*/"), list.get(0).getBeginIndex());
        assertEquals(str.indexOf("/*FOR pmb*/"), list.get(1).getBeginIndex());
        assertEquals(str.indexOf("/*FIRST 'foo'*/"), list.get(2).getBeginIndex());
        assertEquals(str.indexOf("/*END FOR*/"), list.get(3).getBeginIndex());
        assertEquals(str.indexOf("/* END */"), list.get(4).getBeginIndex());
        assertEquals(str.indexOf("/*BEGIN*/") + "/*BEGIN*/".length(), list.get(0).getEndIndex());
        assertEquals(str.indexOf("/*FOR pmb*/") + "/*FOR pmb*/".length(), list.get(1).getEndIndex());
        assertEquals(str.indexOf("/*FIRST 'foo'*/") + "/*FIRST 'foo'*/".length(), list.get(2).getEndIndex());
        assertEquals(str.indexOf("/*END FOR*/") + "/*END FOR*/".length(), list.get(3).getEndIndex());
        assertEquals(str.indexOf("/* END */") + "/* END */".length(), list.get(4).getEndIndex());
        assertEquals("BEGIN", list.get(0).getContent());
        assertEquals("FOR pmb", list.get(1).getContent());
        assertEquals("FIRST 'foo'", list.get(2).getContent());
        assertEquals("END FOR", list.get(3).getContent());
        assertEquals(" END ", list.get(4).getContent()); // not trimmed
        assertEquals("/*BEGIN*/", list.get(0).getScope());
        assertEquals("/*FOR pmb*/", list.get(1).getScope());
        assertEquals("/*FIRST 'foo'*/", list.get(2).getScope());
        assertEquals("/*END FOR*/", list.get(3).getScope());
        assertEquals("/* END */", list.get(4).getScope()); // not trimmed
    }

    public void test_extractScopeList_sameMark() {
        // ## Arrange ##
        String str = "baz@@BEGIN@@where @@FOR pmb@@ @@FIRST 'foo'@@member...@@END FOR@@@@ END @@bar";

        // ## Act ##
        List<ScopeInfo> list = extractScopeList(str, "@@", "@@");

        // ## Assert ##
        assertEquals(5, list.size());
        assertEquals(str.indexOf("@@BEGIN@@"), list.get(0).getBeginIndex());
        assertEquals(str.indexOf("@@FOR pmb@@"), list.get(1).getBeginIndex());
        assertEquals(str.indexOf("@@FIRST 'foo'@@"), list.get(2).getBeginIndex());
        assertEquals(str.indexOf("@@END FOR@@"), list.get(3).getBeginIndex());
        assertEquals(str.indexOf("@@ END @@"), list.get(4).getBeginIndex());
        assertEquals(str.indexOf("@@BEGIN@@") + "@@BEGIN@@".length(), list.get(0).getEndIndex());
        assertEquals(str.indexOf("@@FOR pmb@@") + "@@FOR pmb@@".length(), list.get(1).getEndIndex());
        assertEquals(str.indexOf("@@FIRST 'foo'@@") + "@@FIRST 'foo'@@".length(), list.get(2).getEndIndex());
        assertEquals(str.indexOf("@@END FOR@@") + "@@END FOR@@".length(), list.get(3).getEndIndex());
        assertEquals(str.indexOf("@@ END @@") + "@@ END @@".length(), list.get(4).getEndIndex());
        assertEquals("BEGIN", list.get(0).getContent());
        assertEquals("FOR pmb", list.get(1).getContent());
        assertEquals("FIRST 'foo'", list.get(2).getContent());
        assertEquals("END FOR", list.get(3).getContent());
        assertEquals(" END ", list.get(4).getContent()); // not trimmed
        assertEquals("@@BEGIN@@", list.get(0).getScope());
        assertEquals("@@FOR pmb@@", list.get(1).getScope());
        assertEquals("@@FIRST 'foo'@@", list.get(2).getScope());
        assertEquals("@@END FOR@@", list.get(3).getScope());
        assertEquals("@@ END @@", list.get(4).getScope()); // not trimmed
    }

    // ===================================================================================
    //                                                                    Initial Handling
    //                                                                    ================
    public void test_initBeansProp_basic() {
        assertEquals("fooName", initBeansProp("FooName"));
        assertEquals("fooName", initBeansProp("fooName"));
        assertEquals("BFooName", initBeansProp("BFooName"));
        assertEquals("bFooName", initBeansProp("bFooName"));
        assertEquals("bbFooName", initBeansProp("bbFooName"));
        assertEquals("f", initBeansProp("f"));
        assertEquals("f", initBeansProp("F"));
        assertEquals("FOO_NAME", initBeansProp("FOO_NAME"));
        assertEquals("foo_name", initBeansProp("foo_name"));
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
        assertEquals("FooName", DfStringUtil.camelize("FooName"));
        assertEquals("FName", DfStringUtil.camelize("FName"));
        assertEquals("FooName", DfStringUtil.camelize("foo__name"));
        assertEquals("FooName", DfStringUtil.camelize("FOO _ NAME"));
        assertEquals("FooNa me", DfStringUtil.camelize("FOO _ NA ME"));
    }

    public void test_camelize_delimiters() {
        assertEquals("FooName", DfStringUtil.camelize("FOO_NAME", "_"));
        assertEquals("FooNaMe", DfStringUtil.camelize("foo_na-me", "_", "-"));
        assertEquals("FooNaMeBarId", DfStringUtil.camelize("foo_na-me_bar@id", "_", "-", "@"));
        assertEquals("FooNaMeBId", DfStringUtil.camelize("foo_na-me_b@id", "_", "-", "@"));
        assertEquals("FooNaMeBarId", DfStringUtil.camelize("FOO_NA-ME_BAR@ID", "_", "-", "@"));
        assertEquals("FooName", DfStringUtil.camelize("foo--name", "-"));
        assertEquals("FooName", DfStringUtil.camelize("foo-_@name", "-", "_", "@"));
        assertEquals("FooNaMe", DfStringUtil.camelize("FOO _ NA - ME", "_", "-"));
        assertEquals("FooNa - me", DfStringUtil.camelize("FOO _ NA - ME", "_"));
        assertEquals("FooNameBarId", DfStringUtil.camelize("FOO NAME BAR ID", " "));
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

    public void test_decamelize_delimiter() {
        assertEquals("FOO-NAME", DfStringUtil.decamelize("FooName", "-"));
        assertEquals("FOO@NAME", decamelize("fooName", "@"));
        assertEquals("F", DfStringUtil.decamelize("f", "_"));
        assertEquals("F*O*O_*NAME*BAR", DfStringUtil.decamelize("FOO_NameBar", "*"));
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
