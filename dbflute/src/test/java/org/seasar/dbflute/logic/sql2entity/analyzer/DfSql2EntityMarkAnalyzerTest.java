package org.seasar.dbflute.logic.sql2entity.analyzer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.seasar.dbflute.logic.sql2entity.analyzer.DfSql2EntityMarkAnalyzer;

/**
 * 
 * @author jflute
 * @since 0.9.6 (2009/10/26 Monday)
 */
public class DfSql2EntityMarkAnalyzerTest {

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
}
