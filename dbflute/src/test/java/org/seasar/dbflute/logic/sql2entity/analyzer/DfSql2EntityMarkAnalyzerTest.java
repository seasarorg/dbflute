package org.seasar.dbflute.logic.sql2entity.analyzer;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * 
 * @author jflute
 * @since 0.9.6 (2009/10/26 Monday)
 */
public class DfSql2EntityMarkAnalyzerTest extends PlainTestCase {

    @Test
    public void test_getDescription_basic() {
        // ## Arrange ##
        DfSql2EntityMarkAnalyzer analyzer = new DfSql2EntityMarkAnalyzer();
        String descriptionMark = DfSql2EntityMarkAnalyzer.DESCRIPTION_MARK;
        String sql = "/*" + descriptionMark + "\n foo \n*/ select from";

        // ## Act ##
        String description = analyzer.getDescription(sql);

        // ## Assert ##
        assertEquals(" foo", description);
    }

    @Test
    public void test_getSelectColumnCommentMap_basic() {
        // ## Arrange ##
        DfSql2EntityMarkAnalyzer analyzer = new DfSql2EntityMarkAnalyzer();
        StringBuilder sb = new StringBuilder();
        sb.append("select FOO as FOO_ID -- // comment1");
        sb.append(ln()).append(" , foo.BAR_NAME -- // comment2");
        sb.append(ln()).append(" , BAZ_DATE -- // comment3");

        // ## Act ##
        Map<String, String> commentMap = analyzer.getSelectColumnCommentMap(sb.toString());

        // ## Assert ##
        log(commentMap);
        assertEquals("comment1", commentMap.get("FOO_ID"));
        assertEquals("comment2", commentMap.get("BAR_NAME"));
        assertEquals("comment3", commentMap.get("BAZ_DATE"));
    }

    @Test
    public void test_getSelectColumnCommentMap_irregular() {
        // ## Arrange ##
        DfSql2EntityMarkAnalyzer analyzer = new DfSql2EntityMarkAnalyzer();
        StringBuilder sb = new StringBuilder();
        sb.append("select FOO AS FOO_ID -- //comment1");
        sb.append(ln()).append(" , foo.BAR_NAME -- abc // comment2");
        sb.append(ln()).append(" BAZ_DATE -- // comment3");

        // ## Act ##
        Map<String, String> commentMap = analyzer.getSelectColumnCommentMap(sb.toString());

        // ## Assert ##
        log(commentMap);
        assertEquals("comment1", commentMap.get("FOO_ID"));
        assertEquals("comment2", commentMap.get("BAR_NAME"));
        assertEquals(null, commentMap.get("BAZ_DATE"));
    }
}
