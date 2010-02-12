package org.seasar.dbflute.helper.jdbc.facade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.seasar.dbflute.exception.SQLFailureException;
import org.seasar.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 */
public class DfJdbcFacadeTest extends PlainTestCase {

    @Test
    public void test_handleSQLException() throws Exception {
        // ## Arrange ##
        DfJdbcFacade facade = new DfJdbcFacade((DataSource) null);
        String sql = "select * from dual";
        SQLException e = new SQLException("foo message");

        // ## Act ##
        try {
            facade.handleSQLException(sql, e);

            // ## Assert ##
            fail();
        } catch (SQLFailureException actual) {
            // OK
            log(actual.getMessage());
            assertEquals(e, actual.getCause());
        }
    }
}
