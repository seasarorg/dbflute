package org.seasar.dbflute.util;

import static org.seasar.dbflute.util.Srl.camelize;
import static org.seasar.dbflute.util.Srl.connectPrefix;
import static org.seasar.dbflute.util.Srl.connectSuffix;
import static org.seasar.dbflute.util.Srl.count;
import static org.seasar.dbflute.util.Srl.decamelize;
import static org.seasar.dbflute.util.Srl.extractDelimiterList;
import static org.seasar.dbflute.util.Srl.extractScopeFirst;
import static org.seasar.dbflute.util.Srl.extractScopeList;
import static org.seasar.dbflute.util.Srl.initBeansProp;
import static org.seasar.dbflute.util.Srl.ltrim;
import static org.seasar.dbflute.util.Srl.removeBlockComment;
import static org.seasar.dbflute.util.Srl.removeEmptyLine;
import static org.seasar.dbflute.util.Srl.removeLineComment;
import static org.seasar.dbflute.util.Srl.replaceScopeContent;
import static org.seasar.dbflute.util.Srl.replaceScopeInterspace;
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
import org.seasar.dbflute.util.Srl.DelimiterInfo;
import org.seasar.dbflute.util.Srl.ScopeInfo;

/**
 * @author jflute
 * @since 0.9.5 (2009/04/10 Friday)
 */
public class DfStringUtilTest extends PlainTestCase {

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
        assertEquals("don.moo", substringFirstRear("foo/bar.don.moo", ".", "/"));
        assertEquals("bar.don.moo", substringFirstRear("foo.bar.don.moo", ".", "/"));
        assertEquals("bar.don.moo", substringFirstRear("foo.bar.don.moo", "/", "."));
    }

    public void test_substringLastFront_basic() {
        assertEquals("foo", substringLastFront("foo.bar", "."));
        assertEquals("foo.bar", substringLastFront("foo.bar.don", "."));
        assertEquals("foobar", substringLastFront("foobar", "."));
        assertEquals("foo.bar", substringLastFront("foo.bar.don/moo", "."));
        assertEquals("foo.bar", substringLastFront("foo.bar.don/moo", ".", "/"));
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
    //                                                                               Split
    //                                                                               =====
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
    //                                                                             Replace
    //                                                                             =======
    public void test_replaceScopeContent_basic() {
        // ## Arrange ##
        String str = "/*foo*/foo/*bar*/bar/*foobarbaz*/";

        // ## Act ##
        String actual = replaceScopeContent(str, "foo", "jflute", "/*", "*/");

        // ## Assert ##
        assertEquals("/*jflute*/foo/*bar*/bar/*jflutebarbaz*/", actual);
    }

    public void test_replaceInterspaceContent_basic() {
        // ## Arrange ##
        String str = "/*foo*/foo/*bar*/bar/*foobarbaz*/";

        // ## Act ##
        String actual = replaceScopeInterspace(str, "foo", "jflute", "/*", "*/");

        // ## Assert ##
        assertEquals("/*foo*/jflute/*bar*/bar/*foobarbaz*/", actual);
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
    //                                                                  Delimiter Handling
    //                                                                  ==================
    public void test_extractDelimiterList_basic() {
        // ## Arrange ##
        String str = "foo--bar--baz--and";

        // ## Act ##
        List<DelimiterInfo> list = extractDelimiterList(str, "--");

        // ## Assert ##
        assertEquals(3, list.size());
        assertEquals(3, list.get(0).getBeginIndex());
        assertEquals(8, list.get(1).getBeginIndex());
        assertEquals(13, list.get(2).getBeginIndex());
        assertEquals(5, list.get(0).getEndIndex());
        assertEquals(10, list.get(1).getEndIndex());
        assertEquals(15, list.get(2).getEndIndex());
        assertEquals("foo", list.get(0).substringInterspaceToPrevious());
        assertEquals("bar", list.get(1).substringInterspaceToPrevious());
        assertEquals("baz", list.get(2).substringInterspaceToPrevious());
        assertEquals("bar", list.get(0).substringInterspaceToNext());
        assertEquals("baz", list.get(1).substringInterspaceToNext());
        assertEquals("and", list.get(2).substringInterspaceToNext());
    }

    public void test_extractDelimiterList_bothSide() {
        // ## Arrange ##
        String str = "--foo--bar--baz--and--";

        // ## Act ##
        List<DelimiterInfo> list = extractDelimiterList(str, "--");

        // ## Assert ##
        assertEquals(5, list.size());
        assertEquals(0, list.get(0).getBeginIndex());
        assertEquals(5, list.get(1).getBeginIndex());
        assertEquals(10, list.get(2).getBeginIndex());
        assertEquals(15, list.get(3).getBeginIndex());
        assertEquals(20, list.get(4).getBeginIndex());
    }

    public void test_extractDelimiterList_noDelimiter() {
        // ## Arrange ##
        String str = "foo-bar-baz-and";

        // ## Act ##
        List<DelimiterInfo> list = extractDelimiterList(str, "--");

        // ## Assert ##
        assertEquals(0, list.size());
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
        assertEquals("baz", list.get(0).substringInterspaceToPrevious());
        assertEquals("where ", list.get(1).substringInterspaceToPrevious());
        assertEquals(" ", list.get(2).substringInterspaceToPrevious());
        assertEquals("member...", list.get(3).substringInterspaceToPrevious());
        assertEquals("", list.get(4).substringInterspaceToPrevious());
        assertEquals("where ", list.get(0).substringInterspaceToNext());
        assertEquals(" ", list.get(1).substringInterspaceToNext());
        assertEquals("member...", list.get(2).substringInterspaceToNext());
        assertEquals("", list.get(3).substringInterspaceToNext());
        assertEquals("bar", list.get(4).substringInterspaceToNext());
        assertEquals("baz/*BEGIN*/", list.get(0).substringScopeToPrevious());
        assertEquals("/*BEGIN*/where /*FOR pmb*/", list.get(1).substringScopeToPrevious());
        assertEquals("/*FOR pmb*/ /*FIRST 'foo'*/", list.get(2).substringScopeToPrevious());
        assertEquals("/*FIRST 'foo'*/member.../*END FOR*/", list.get(3).substringScopeToPrevious());
        assertEquals("/*END FOR*//* END */", list.get(4).substringScopeToPrevious());
        assertEquals("/*BEGIN*/where /*FOR pmb*/", list.get(0).substringScopeToNext());
        assertEquals("/*FOR pmb*/ /*FIRST 'foo'*/", list.get(1).substringScopeToNext());
        assertEquals("/*FIRST 'foo'*/member.../*END FOR*/", list.get(2).substringScopeToNext());
        assertEquals("/*END FOR*//* END */", list.get(3).substringScopeToNext());
        assertEquals("/* END */bar", list.get(4).substringScopeToNext());
    }

    public void test_extractScopeList_replaceContentOnBaseString() {
        // ## Arrange ##
        String str = "/*foo*/foo/*bar*/bar/*foobarbaz*/";

        // ## Act ##
        List<ScopeInfo> list = extractScopeList(str, "/*", "*/");

        // ## Assert ##
        ScopeInfo scope = list.get(1);
        String baseString1 = scope.replaceContentOnBaseString("foo", "jflute");
        assertEquals("/*jflute*/foo/*bar*/bar/*jflutebarbaz*/", baseString1);
        String baseString2 = scope.replaceContentOnBaseString("*", "jflute");
        assertEquals(str, baseString2); // marks no change
        assertEquals(str, scope.getBaseString()); // no change
    }

    public void test_extractScopeList_replaceInterspaceOnBaseString() {
        // ## Arrange ##
        String str = "/*foo*/foo/*bar*/bar/*foobarbaz*/";

        // ## Act ##
        List<ScopeInfo> list = extractScopeList(str, "/*", "*/");

        // ## Assert ##
        ScopeInfo scope = list.get(1);
        String baseString1 = scope.replaceInterspaceOnBaseString("foo", "jflute");
        assertEquals("/*foo*/jflute/*bar*/bar/*foobarbaz*/", baseString1);
        String baseString2 = scope.replaceInterspaceOnBaseString("*", "jflute");
        assertEquals(str, baseString2); // marks no change
        assertEquals(str, scope.getBaseString()); // no change
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
        assertEquals("FOR pmb", list.get(0).getNext().getContent());
        assertEquals("FIRST 'foo'", list.get(1).getNext().getContent());
        assertEquals("END FOR", list.get(2).getNext().getContent());
        assertEquals(" END ", list.get(3).getNext().getContent());
        assertEquals(null, list.get(4).getNext());
        assertEquals("where ", list.get(0).substringInterspaceToNext());
        assertEquals(" ", list.get(1).substringInterspaceToNext());
        assertEquals("member...", list.get(2).substringInterspaceToNext());
        assertEquals("", list.get(3).substringInterspaceToNext());
        assertEquals("bar", list.get(4).substringInterspaceToNext());
    }

    // ===================================================================================
    //                                                                       Line Handling
    //                                                                       =============
    public void test_removeEmptyLine_basic() {
        // ## Arrange ##
        String sql = "aaaa\r\n";
        sql = sql + "bbbb\r\n";
        sql = sql + "--\r\n";
        sql = sql + "\r\n";
        sql = sql + "\n";
        sql = sql + "cccc\r\n";

        // ## Act ##
        String actual = removeEmptyLine(sql);

        // ## Assert ##
        assertEquals("aaaa\nbbbb\n--\ncccc", actual);
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
    public void test_removeBlockComment_basic() {
        // ## Arrange ##
        String sql = "baz/*BEGIN*/where /*FOR pmb*/ /*FIRST 'foo'*/member.../*END FOR*//* END */bar";

        // ## Act ##
        String actual = removeBlockComment(sql);

        // ## Assert ##
        assertEquals("bazwhere  member...bar", actual);
    }

    public void test_removeBlockComment_noComment() {
        // ## Arrange ##
        String sql = "barbaz";

        // ## Act ##
        String actual = removeBlockComment(sql);

        // ## Assert ##
        assertEquals("barbaz", actual);
    }

    public void test_removeLineComment_basic() throws Exception {
        // ## Arrange ##
        String sql = "aaa\n";
        sql = sql + "bbb\n";
        sql = sql + "--\n";
        sql = sql + "ccc -- foo\n";
        sql = sql + "ddd /* -- foo */\n";
        sql = sql + "eee\n";

        // ## Act ##
        String actual = removeLineComment(sql);

        // ## Assert ##
        log(actual);
        assertEquals("aaa\nbbb\nccc \nddd /* -- foo */\neee\n", actual);
    }

    public void test_removeLineComment_Lf() throws Exception {
        // ## Arrange ##
        String sql = "aaaa\n";
        sql = sql + "bbbb\n";
        sql = sql + "--\n";
        sql = sql + "cccc\n";

        // ## Act ##
        String actual = removeLineComment(sql);

        // ## Assert ##
        log(actual);
        assertEquals("aaaa\nbbbb\ncccc\n", actual);
    }

    public void test_removeLineComment_CrLf() throws Exception {
        String sql = "aaaa\r\n";
        sql = sql + "bbbb\r\n";
        sql = sql + "--\r\n";
        sql = sql + "cccc\r\n";
        String actual = DfStringUtil.removeLineComment(sql);
        log(actual);
        assertFalse(actual.contains("--"));
        assertFalse(actual.contains("\r"));
        assertEquals("aaaa\nbbbb\ncccc\n", actual);
    }
}
