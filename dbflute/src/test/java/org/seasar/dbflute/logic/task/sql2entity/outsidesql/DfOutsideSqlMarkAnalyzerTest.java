package org.seasar.dbflute.logic.task.sql2entity.outsidesql;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.seasar.dbflute.logic.sql2entity.outsidesql.DfOutsideSqlMarkAnalyzer;

/**
 * 
 * @author jflute
 * @since 0.9.6 (2009/10/26 Monday)
 */
public class DfOutsideSqlMarkAnalyzerTest {

    @Test
    public void test_getDescription_basic() {
        // ## Arrange ##
        DfOutsideSqlMarkAnalyzer analyzer = new DfOutsideSqlMarkAnalyzer();
        String descriptionMark = DfOutsideSqlMarkAnalyzer.DESCRIPTION_MARK;
        String sql = "/*" + descriptionMark + "\n foo \n*/ select from";

        // ## Act ##
        String description = analyzer.getDescription(sql);

        // ## Assert ##
        assertEquals(" foo", description);
    }
}
