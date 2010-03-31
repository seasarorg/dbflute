package org.seasar.dbflute.s2dao.sqlcommand;

import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 */
public class TnProcedureCommandTest extends PlainTestCase {

    public void test_doBuildSql_kakou() throws Exception {
        // ## Arrange ##
        TnProcedureCommand target = createTarget();

        // ## Act ##
        String sql = target.doBuildSql("SP_FOO", 3, true, true);

        // ## Assert ##
        log(sql);
        assertEquals("{? = call SP_FOO(?, ?)}", sql);
    }

    public void test_doBuildSql_kakowanai() throws Exception {
        // ## Arrange ##
        TnProcedureCommand target = createTarget();

        // ## Act ##
        String sql = target.doBuildSql("SP_FOO", 3, true, false);

        // ## Assert ##
        log(sql);
        assertEquals("? = call SP_FOO(?, ?)", sql);
    }

    protected TnProcedureCommand createTarget() {
        return new TnProcedureCommand(null, null, null, null);
    }
}
