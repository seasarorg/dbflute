package org.seasar.dbflute.twowaysql.node;

import java.util.List;

import org.seasar.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.dbflute.twowaysql.context.CommandContext;
import org.seasar.dbflute.twowaysql.context.CommandContextCreator;
import org.seasar.dbflute.unit.PlainTestCase;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class ForNodeTest extends PlainTestCase {

    public void test_accept_basic() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" where").append(ln());
        sb.append("   /*FOR pmb.memberNameList*/").append(ln());
        sb.append("   and MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("select * from MEMBER"));
        assertTrue(actual.contains(" where"));
        assertFalse(actual.contains("/*FOR "));
        assertFalse(actual.contains("/*END*/"));
        assertFalse(actual.contains("FIRST"));
        assertFalse(actual.contains("NEXT"));
        assertFalse(actual.contains("LAST"));
        assertTrue(actual.contains(" and MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertEquals("foo%", ctx.getBindVariables()[0]);
        assertEquals("bar%", ctx.getBindVariables()[1]);
        assertEquals("baz%", ctx.getBindVariables()[2]);
    }

    public void test_accept_FirstAnd() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" where").append(ln());
        sb.append("   /*FOR pmb.memberNameList*/").append(ln());
        sb.append("   /*FIRST*/and /*END*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains(" and MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertEquals("foo%", ctx.getBindVariables()[0]);
        assertEquals("bar%", ctx.getBindVariables()[1]);
        assertEquals("baz%", ctx.getBindVariables()[2]);
    }

    public void test_accept_NextAnd() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likeSuffix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" where").append(ln());
        sb.append("   /*FOR pmb.memberNameList*/").append(ln());
        sb.append("   /*NEXT 'and '*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" and MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertEquals("%foo", ctx.getBindVariables()[0]);
        assertEquals("%bar", ctx.getBindVariables()[1]);
        assertEquals("%baz", ctx.getBindVariables()[2]);
    }

    public void test_accept_LastAnd() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" where").append(ln());
        sb.append("   /*FOR pmb.memberNameList*/").append(ln());
        sb.append("   /*LAST*/and /*END*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" and MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertEquals("foo%", ctx.getBindVariables()[0]);
        assertEquals("bar%", ctx.getBindVariables()[1]);
        assertEquals("baz%", ctx.getBindVariables()[2]);
    }

    public void test_accept_BEGIN_connectorAdjustment_go() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*/").append(ln());
        sb.append("   and MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" and MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertEquals("foo%", ctx.getBindVariables()[0]);
        assertEquals("bar%", ctx.getBindVariables()[1]);
        assertEquals("baz%", ctx.getBindVariables()[2]);
    }

    public void test_accept_BEGIN_connectorAdjustment_not() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberId(3);
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*/").append(ln());
        sb.append("   and MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  MEMBER_ID = ?"));
        assertTrue(actual.contains(" and MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertEquals(3, ctx.getBindVariables()[0]);
        assertEquals("foo%", ctx.getBindVariables()[1]);
        assertEquals("bar%", ctx.getBindVariables()[2]);
        assertEquals("baz%", ctx.getBindVariables()[3]);
    }

    public void test_accept_loopVariable_in_IF() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likeContain());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*//*IF true*//*FIRST*/and (/*END*//*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  ("));
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertTrue(actual.contains(" )"));
        assertEquals("%foo%", ctx.getBindVariables()[0]);
        assertEquals("%bar%", ctx.getBindVariables()[1]);
        assertEquals("%baz%", ctx.getBindVariables()[2]);
    }

    public void test_accept_allStars_all_true() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberId(4);
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("fo|o", "ba%r", "b_a|z"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  MEMBER_ID = ?"));
        assertTrue(actual.contains(" and ("));
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertTrue(actual.contains(" )"));
        assertEquals(4, ctx.getBindVariables()[0]);
        assertEquals("fo||o%", ctx.getBindVariables()[1]);
        assertEquals("ba|%r%", ctx.getBindVariables()[2]);
        assertEquals("b|_a||z%", ctx.getBindVariables()[3]);
    }

    public void test_accept_allStars_connectorAdjustment() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likeContain());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  ("));
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertTrue(actual.contains(" )"));
        assertEquals("%foo%", ctx.getBindVariables()[0]);
        assertEquals("%bar%", ctx.getBindVariables()[1]);
        assertEquals("%baz%", ctx.getBindVariables()[2]);
    }

    public void test_accept_allStars_connectorAdjustment_noLn() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER");
        sb.append(" /*BEGIN*/where");
        sb.append("   /*IF pmb.memberId != null*/");
        sb.append("   MEMBER_ID = /*pmb.memberId*/");
        sb.append("   /*END*/");
        sb.append("   /*FOR pmb.memberNameList*//*FIRST*/and (/*END*/");
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'");
        sb.append("   /*LAST*/)/*END*//*END*/");
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  ("));
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertTrue(actual.contains(" )"));
        assertEquals("foo%", ctx.getBindVariables()[0]);
        assertEquals("bar%", ctx.getBindVariables()[1]);
        assertEquals("baz%", ctx.getBindVariables()[2]);
    }

    public void test_accept_allStars_connectorAdjustment_LAST() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likeContain());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*//*LAST '@'*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  ("));
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains("  @"));
        assertTrue(Srl.count(actual, "@") == 1);
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertTrue(actual.contains(" )"));
        assertEquals("%foo%", ctx.getBindVariables()[0]);
        assertEquals("%bar%", ctx.getBindVariables()[1]);
        assertEquals("%baz%", ctx.getBindVariables()[2]);
    }

    public void test_accept_emptyList() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        List<String> emptyList = DfCollectionUtil.emptyList();
        pmb.setMemberNameList(emptyList);
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER");
        sb.append(" /*BEGIN*/where");
        sb.append("   /*IF pmb.memberId != null*/");
        sb.append("   MEMBER_ID = /*pmb.memberId*/");
        sb.append("   /*END*/");
        sb.append("   /*FOR pmb.memberNameList*//*FIRST*/and (/*END*/");
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'");
        sb.append("   /*LAST*/)/*END*//*END*/");
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("select * from MEMBER "));
        assertFalse(actual.contains("where"));
        assertFalse(actual.contains("  ("));
        assertFalse(actual.contains("  MEMBER_NAME like ?"));
        assertFalse(actual.contains(" or MEMBER_NAME like ?"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 0);
        assertFalse(actual.contains(" )"));
        assertEquals(0, ctx.getBindVariables().length);
    }

    public void test_accept_nullList() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(null);
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER");
        sb.append(" /*BEGIN*/where");
        sb.append("   /*IF pmb.memberId != null*/");
        sb.append("   MEMBER_ID = /*pmb.memberId*/");
        sb.append("   /*END*/");
        sb.append("   /*FOR pmb.memberNameList*//*FIRST*/and (/*END*/");
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'");
        sb.append("   /*LAST*/)/*END*//*END*/");
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("select * from MEMBER "));
        assertFalse(actual.contains("where"));
        assertFalse(actual.contains("  ("));
        assertFalse(actual.contains("  MEMBER_NAME like ?"));
        assertFalse(actual.contains(" or MEMBER_NAME like ?"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 0);
        assertFalse(actual.contains(" )"));
        assertEquals(0, ctx.getBindVariables().length);
    }

    public void test_accept_allStars_embedded_either_true() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("'foo'", "'bar'", "'baz'"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*$#current*/'foo%'").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  ("));
        // unsupported embedded comment with like search option
        assertTrue(actual.contains("  MEMBER_NAME like 'foo'% escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like 'bar'% escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like 'baz'% escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertTrue(actual.contains(" )"));
        assertEquals(0, ctx.getBindVariables().length);
    }

    public void test_accept_allStars_several_basic() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likeContain());
        pmb.setMemberAccountList(DfCollectionUtil.newArrayList("ab%c", "%def"));
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append("   /*FOR pmb.memberAccountList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_ACCOUNT like /*#current*/'foo%'").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  ("));
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertTrue(actual.contains(" and ("));
        assertTrue(actual.contains("  MEMBER_ACCOUNT like ?"));
        assertTrue(actual.contains(" or MEMBER_ACCOUNT like ?"));
        assertTrue(Srl.count(actual, "MEMBER_ACCOUNT") == 2);
        assertTrue(actual.contains(" )"));
        assertTrue(Srl.count(actual, " )") == 2);
        assertEquals("%foo%", ctx.getBindVariables()[0]);
        assertEquals("%bar%", ctx.getBindVariables()[1]);
        assertEquals("%baz%", ctx.getBindVariables()[2]);
        assertEquals("ab%c", ctx.getBindVariables()[3]);
        assertEquals("%def", ctx.getBindVariables()[4]);
    }

    public void test_accept_allStars_nested_basic() throws Exception {
        // ## Arrange ##
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("foo", "bar", "baz"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likeContain());
        pmb.setMemberAccountList(DfCollectionUtil.newArrayList("ab%c", "%def"));
        StringBuilder sb = new StringBuilder();
        sb.append("select * from MEMBER").append(ln());
        sb.append(" /*BEGIN*/where").append(ln());
        sb.append("   /*IF pmb.memberId != null*/").append(ln());
        sb.append("   MEMBER_ID = /*pmb.memberId*/").append(ln());
        sb.append("   /*END*/").append(ln());
        sb.append("   /*FOR pmb.memberNameList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("     /*NEXT 'or '*/MEMBER_NAME like /*#current*/'foo%'").append(ln());
        sb.append("     /*FOR pmb.memberAccountList*//*FIRST*/and (/*END*/").append(ln());
        sb.append("       /*NEXT 'or '*/MEMBER_ACCOUNT like /*#current*/'foo%'").append(ln());
        sb.append("     /*LAST*/)/*END*//*END*/").append(ln());
        sb.append("   /*LAST*/)/*END*//*END*/").append(ln());
        sb.append(" /*END*/");
        SqlAnalyzer analyzer = new SqlAnalyzer(sb.toString(), false);
        Node rootNode = analyzer.analyze();
        CommandContext ctx = createCtx(pmb);

        // ## Act ##
        rootNode.accept(ctx);

        // ## Assert ##
        String actual = ctx.getSql();
        log(ln() + actual);
        assertTrue(actual.contains("  ("));
        assertTrue(actual.contains("  MEMBER_NAME like ? escape '|'"));
        assertTrue(actual.contains(" or MEMBER_NAME like ? escape '|'"));
        assertTrue(Srl.count(actual, "MEMBER_NAME") == 3);
        assertTrue(actual.contains(" and ("));
        assertTrue(actual.contains("  MEMBER_ACCOUNT like ?"));
        assertTrue(actual.contains(" or MEMBER_ACCOUNT like ?"));
        assertTrue(Srl.count(actual, "MEMBER_ACCOUNT") == 6);
        assertTrue(actual.contains(" )"));
        assertTrue(Srl.count(actual, " )") == 4);
        assertEquals("%foo%", ctx.getBindVariables()[0]);
        assertEquals("ab%c", ctx.getBindVariables()[1]);
        assertEquals("%def", ctx.getBindVariables()[2]);
        assertEquals("%bar%", ctx.getBindVariables()[3]);
        assertEquals("ab%c", ctx.getBindVariables()[4]);
        assertEquals("%def", ctx.getBindVariables()[5]);
        assertEquals("%baz%", ctx.getBindVariables()[6]);
        assertEquals("ab%c", ctx.getBindVariables()[7]);
        assertEquals("%def", ctx.getBindVariables()[8]);
    }

    // ===================================================================================
    //                                                                         Test Helper
    //                                                                         ===========
    protected static class MockPmb {
        protected Integer _memberId;
        protected List<String> _memberNameList;
        protected LikeSearchOption _memberNameListInternalLikeSearchOption;
        protected List<String> _memberAccountList;
        protected MockPmb _nestPmb;
        protected MockPmb _nestLikePmb;
        protected LikeSearchOption _nestLikePmbInternalLikeSearchOption;

        public Integer getMemberId() {
            return _memberId;
        }

        public void setMemberId(Integer memberId) {
            this._memberId = memberId;
        }

        public List<String> getMemberNameList() {
            return _memberNameList;
        }

        public void setMemberNameList(List<String> memberNameList) {
            this._memberNameList = memberNameList;
        }

        public LikeSearchOption getMemberNameListInternalLikeSearchOption() {
            return _memberNameListInternalLikeSearchOption;
        }

        public void setMemberNameListInternalLikeSearchOption(LikeSearchOption memberNameInternalLikeSearchOption) {
            this._memberNameListInternalLikeSearchOption = memberNameInternalLikeSearchOption;
        }

        public List<String> getMemberAccountList() {
            return _memberAccountList;
        }

        public void setMemberAccountList(List<String> memberAccountList) {
            this._memberAccountList = memberAccountList;
        }

        public MockPmb getNestPmb() {
            return _nestPmb;
        }

        public void setNestPmb(MockPmb nestPmb) {
            this._nestPmb = nestPmb;
        }

        public MockPmb getNestLikePmb() {
            return _nestLikePmb;
        }

        public void setNestLikePmb(MockPmb nestLikePmb) {
            this._nestLikePmb = nestLikePmb;
        }

        public LikeSearchOption getNestLikePmbInternalLikeSearchOption() {
            return _nestLikePmbInternalLikeSearchOption;
        }

        public void setNestLikePmbInternalLikeSearchOption(LikeSearchOption nestLikePmbInternalLikeSearchOption) {
            this._nestLikePmbInternalLikeSearchOption = nestLikePmbInternalLikeSearchOption;
        }
    }

    private CommandContext createCtx(Object pmb) {
        return xcreateCommandContext(new Object[] { pmb }, new String[] { "pmb" }, new Class<?>[] { pmb.getClass() });
    }

    private CommandContext xcreateCommandContext(Object[] args, String[] argNames, Class<?>[] argTypes) {
        return xcreateCommandContextCreator(argNames, argTypes).createCommandContext(args);
    }

    private CommandContextCreator xcreateCommandContextCreator(String[] argNames, Class<?>[] argTypes) {
        return new CommandContextCreator(argNames, argTypes);
    }
}
