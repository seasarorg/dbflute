package org.seasar.dbflute.helper.jdbc.sqlfile;

import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;

import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileRunnerExecute.DfRunnerDispatchResult;

/**
 * @author jflute
 * @since 0.9.5.4 (2009/08/07 Friday)
 */
public interface DfSqlFileRunnerDispatcher {

    /**
     * Dispatch executing a SQL.
     * @param sqlFile The SQL file that contains the SQL. (NotNull)
     * @param st Statement. (NotNull)
     * @param sql SQL string. (NotNull)
     * @return The type of dispatch result. (NotNull)
     * @throws SQLException
     */
    DfRunnerDispatchResult dispatch(File sqlFile, Statement st, String sql) throws SQLException;
}
