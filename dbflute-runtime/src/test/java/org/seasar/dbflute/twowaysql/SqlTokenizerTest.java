package org.seasar.dbflute.twowaysql;

import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.5 (2009/04/08)
 */
public class SqlTokenizerTest extends PlainTestCase {

    // ===================================================================================
    //                                                                                Skip
    //                                                                                ====
    public void test_skipToken() {
        // ## Arrange ##
        String sql = "/*IF pmb.memberName != null*/and member.MEMBER_NAME = 'TEST'/*END*/";
        SqlTokenizer tokenizer = new SqlTokenizer(sql);

        // ## Act ##
        tokenizer.next();
        tokenizer.skipWhitespace();
        String skippedToken = tokenizer.skipToken();
        tokenizer.skipWhitespace();

        // ## Assert ##
        log("skippedToken : " + skippedToken);
        log("before       : " + tokenizer.getBefore());
        log("after        : " + tokenizer.getAfter());
        assertEquals("and", skippedToken);
    }

    public void test_skipWhitespace() {
        // ## Arrange ##
        String sql = "/*IF pmb.memberName != null*/ and member.MEMBER_NAME = 'TEST'/*END*/";
        SqlTokenizer tokenizer = new SqlTokenizer(sql);

        // ## Act ##
        tokenizer.next();
        tokenizer.skipWhitespace();
        String skippedToken = tokenizer.skipToken();
        tokenizer.skipWhitespace();

        // ## Assert ##
        log("skippedToken : " + skippedToken);
        log("before       : " + tokenizer.getBefore());
        log("after        : " + tokenizer.getAfter());
        assertEquals("and", skippedToken);
    }

    // ===================================================================================
    //                                                                          Show Token
    //                                                                          ==========
    public void test_show_next_with_BEGIN_comment() {
        String sql = "select * from MEMBER";
        sql = sql + " /*BEGIN*/";
        sql = sql + " where";
        sql = sql + "   /*IF pmb.memberId != null*/member.MEMBER_ID = 3/*END*/";
        sql = sql + "   /*IF pmb.memberName != null*/and member.MEMBER_NAME = 'TEST'/*END*/";
        sql = sql + " /*END*/";
        SqlTokenizer tokenizer = new SqlTokenizer(sql);

        log("01: " + tokenizer.token);
        tokenizer.next();
        log("02: " + tokenizer.token);
        tokenizer.next();
        log("03: " + tokenizer.token);
        tokenizer.next();
        log("04: " + tokenizer.token);
        tokenizer.next();
        log("05: " + tokenizer.token);
        tokenizer.next();
        log("06: " + tokenizer.token);
        tokenizer.next();
        log("07: " + tokenizer.token);
        tokenizer.next();
        log("08: " + tokenizer.token);
        tokenizer.next();
        log("09: " + tokenizer.token);
        tokenizer.next();
        log("10: " + tokenizer.token);
        tokenizer.next();
        log("11: " + tokenizer.token);
        tokenizer.next();
        log("12: " + tokenizer.token);
        tokenizer.next();
        log("13: " + tokenizer.token);
        tokenizer.next();
        log("14: " + tokenizer.token);
    }

    public void test_show_next_without_BEGIN_comment() {
        String sql = "select * from MEMBER";
        sql = sql + " where";
        sql = sql + "   /*IF pmb.memberId != null*/member.MEMBER_ID = 3/*END*/";
        sql = sql + "   /*IF pmb.memberName != null*/and member.MEMBER_NAME = 'TEST'/*END*/";
        SqlTokenizer tokenizer = new SqlTokenizer(sql);

        log("01: " + tokenizer.token);
        tokenizer.next();
        log("02: " + tokenizer.token);
        tokenizer.next();
        log("03: " + tokenizer.token);
        tokenizer.next();
        log("04: " + tokenizer.token);
        tokenizer.next();
        log("05: " + tokenizer.token);
        tokenizer.next();
        log("06: " + tokenizer.token);
        tokenizer.next();
        log("07: " + tokenizer.token);
        tokenizer.next();
        log("08: " + tokenizer.token);
        tokenizer.next();
        log("09: " + tokenizer.token);
        tokenizer.next();
        log("10: " + tokenizer.token);
        tokenizer.next();
        log("11: " + tokenizer.token);
        tokenizer.next();
        log("12: " + tokenizer.token);
        tokenizer.next();
        log("13: " + tokenizer.token);
        tokenizer.next();
        log("14: " + tokenizer.token);
    }
}
