package org.seasar.dbflute.twowaysql;

import org.seasar.dbflute.twowaysql.context.CommandContext;
import org.seasar.dbflute.twowaysql.context.CommandContextCreator;
import org.seasar.dbflute.twowaysql.node.Node;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.5 (2009/04/08)
 */
public class SqlAnalyzerTest extends PlainTestCase {

    // ===================================================================================
    //                                                                               Parse
    //                                                                               =====
    public void test_parse_IF_true() {
        // ## Arrange ##
        String sql = "/*IF pmb.memberName != null*/and member.MEMBER_NAME = 'TEST'/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.parse();

        // ## Assert ##
        SimpleMemberPmb pmb = new SimpleMemberPmb();
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log(ctx.getSql());
        assertEquals("and member.MEMBER_NAME = 'TEST'", ctx.getSql());
    }

    public void test_parse_IF_false() {
        // ## Arrange ##
        String sql = "/*IF pmb.memberName != null*/and member.MEMBER_NAME = 'TEST'/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.parse();

        // ## Assert ##
        SimpleMemberPmb pmb = new SimpleMemberPmb();
        pmb.setMemberName(null);
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        assertEquals("", ctx.getSql().trim());
    }

    public void test_parse_BEGIN() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where" + ln();
        sql = sql + "/*IF pmb.memberId != null*/member.MEMBER_ID = 3/*END*/" + ln();
        sql = sql + "/*IF pmb.memberName != null*/and member.MEMBER_NAME = 'TEST'/*END*/" + ln();
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.parse();

        // ## Assert ##
        SimpleMemberPmb pmb = new SimpleMemberPmb();
        pmb.setMemberId(3);
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log(ln() + ctx.getSql());
        String expected = "where" + ln() + "member.MEMBER_ID = 3" + ln() + "and member.MEMBER_NAME = 'TEST'" + ln();
        assertEquals(expected, ctx.getSql());
    }

    public void test_parse_BEGIN_that_has_nested_IF() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where" + ln();
        sql = sql + "/*IF pmb.memberId != null*/" + ln();
        sql = sql + "and AAA /*IF true*/and BBB/*END*/ /*IF true*/and CCC/*END*/" + ln();
        sql = sql + "/*END*/" + ln();
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.parse();

        // ## Assert ##
        SimpleMemberPmb pmb = new SimpleMemberPmb();
        pmb.setMemberId(3);
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log(ln() + ctx.getSql());
        String expected = "where" + ln() + "AAA BBB and CCC" + ln() + ln();
        assertEquals(expected, ctx.getSql());
    }

    public void test_parse_BEGIN_that_has_nested_IFIF() {
        // ## Arrange ##
        String sql = "/*BEGIN*/where" + ln();
        sql = sql + "/*IF pmb.memberId != null*/" + ln();
        sql = sql + "and AAA /*IF true*/and BBB /*IF true*/and BBB-CCC/*END*//*END*/ /*IF true*/and CCC/*END*/" + ln();
        sql = sql + "/*END*/" + ln();
        sql = sql + "/*END*/";
        SqlAnalyzer analyzer = new SqlAnalyzer(sql, false);

        // ## Act ##
        Node rootNode = analyzer.parse();

        // ## Assert ##
        SimpleMemberPmb pmb = new SimpleMemberPmb();
        pmb.setMemberId(3);
        pmb.setMemberName("foo");
        CommandContext ctx = createCtx(pmb);
        rootNode.accept(ctx);
        log(ln() + ctx.getSql());
        String expected = "where" + ln() + "AAA BBB BBB-CCC and CCC" + ln() + ln();
        assertEquals(expected, ctx.getSql());
    }

    // ===================================================================================
    //                                                                         Test Helper
    //                                                                         ===========
    private static class SimpleMemberPmb {
        protected Integer memberId;
        protected String memberName;

        public Integer getMemberId() {
            return memberId;
        }

        public void setMemberId(Integer memberId) {
            this.memberId = memberId;
        }

        public String getMemberName() {
            return memberName;
        }

        public void setMemberName(String memberName) {
            this.memberName = memberName;
        }
    }

    private CommandContext createCtx(SimpleMemberPmb pmb) {
        return xcreateCommandContext(new Object[] { pmb }, new String[] { "pmb" }, new Class<?>[] { pmb.getClass() });
    }

    private CommandContext xcreateCommandContext(Object[] args, String[] argNames, Class<?>[] argTypes) {
        return xcreateCommandContextCreator(argNames, argTypes).createCommandContext(args);
    }

    private CommandContextCreator xcreateCommandContextCreator(String[] argNames, Class<?>[] argTypes) {
        return new CommandContextCreator(argNames, argTypes);
    }
}
