package org.seasar.dbflute.logic.replaceschema.dataassert;

import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author jflute
 * @since 0.9.5.4 (2009/08/07 Friday)
 */
public interface DfDataAssertHandler {

    /**
     * Handle asserting.
     * @param sqlFile The SQL file that contains the SQL. (NotNull)
     * @param st Statement. (NotNull)
     * @param sql SQL string. (NotNull)
     * @throws SQLException
     */
    void handle(File sqlFile, Statement st, String sql) throws SQLException;
}
