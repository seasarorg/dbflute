package org.dbflute.s2dao.sqlhandler;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import org.dbflute.jdbc.StatementFactory;

/**
 * @author DBFlute(AutoGenerator)
 */
public class TnBasicUpdateHandler extends TnBasicHandler {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnBasicUpdateHandler(DataSource dataSource, String sql, StatementFactory statementFactory) {
        super(dataSource, sql, statementFactory);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public int execute(Object[] args) {
        return execute(args, getArgTypes(args));
    }

    public int execute(Object[] args, Class<?>[] argTypes) {
        Connection connection = getConnection();
        try {
            return execute(connection, args, argTypes);
        } finally {
            close(connection);
        }
    }

    public int execute(Connection connection, Object[] args, Class<?>[] argTypes) {
        logSql(args, argTypes);
        PreparedStatement ps = prepareStatement(connection);
        try {
            bindArgs(ps, args, argTypes);
            return executeUpdate(ps);
        } finally {
            close(ps);
        }
    }
}
