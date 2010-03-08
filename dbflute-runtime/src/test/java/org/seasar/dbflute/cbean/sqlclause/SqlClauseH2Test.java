package org.seasar.dbflute.cbean.sqlclause;

import java.util.HashMap;
import java.util.Map;

import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.mock.MockDBMeta;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.1 (2009/02/08 Sunday)
 */
public class SqlClauseH2Test extends PlainTestCase {

    public void test_getLeftOuterJoinClause_outerJoin() {
        // ## Arrange ##
        SqlClauseH2 target = new SqlClauseH2("test") {
            @Override
            protected DBMeta findDBMeta(String tableDbName) {
                return new MockDBMeta() {
                    @Override
                    public String getTableSqlName() {
                        return "BAR";
                    }
                };
            }
        };

        Map<String, String> joinOnMap = new HashMap<String, String>();
        joinOnMap.put("LOCAL_BAR_ID", "FOREIGN_BAR_ID");

        // ## Act ##
        target.registerOuterJoin("BAR", "Bar", joinOnMap);
        String leftOuterJoinClause = target.getLeftOuterJoinClause();

        // ## Assert ##
        log(leftOuterJoinClause);
        String expected = "left outer join BAR Bar on LOCAL_BAR_ID = FOREIGN_BAR_ID";
        assertEquals(expected, leftOuterJoinClause.trim());
    }

    public void test_getLeftOuterJoinClause_innerJoin() {
        // ## Arrange ##
        SqlClauseH2 target = new SqlClauseH2("test") {
            @Override
            protected DBMeta findDBMeta(final String tableDbName) {
                return new MockDBMeta() {
                    @Override
                    public String getTableSqlName() {
                        return tableDbName.equals("FOO") ? "FOO" : "BAR";
                    }
                };
            }
        };

        Map<String, String> joinOnMap = new HashMap<String, String>();
        joinOnMap.put("LOCAL_FOO_ID", "FOREIGN_FOO_ID");

        // ## Act ##
        target.makeInnerJoinEffective();
        target.registerOuterJoin("FOO", "Foo", joinOnMap);
        target.backToOuterJoin();
        target.registerOuterJoin("BAR", "Bar", joinOnMap);
        String leftOuterJoinClause = target.getLeftOuterJoinClause();

        // ## Assert ##
        log(leftOuterJoinClause);
        String expected1 = "inner join FOO Foo on LOCAL_FOO_ID = FOREIGN_FOO_ID";
        assertTrue(leftOuterJoinClause.contains(expected1));
        String expected2 = "left outer join BAR Bar on LOCAL_FOO_ID = FOREIGN_FOO_ID";
        assertTrue(leftOuterJoinClause.contains(expected2));
    }
}
