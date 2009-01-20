package org.seasar.dbflute.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * The creator of statement.
 * @author jflute
 */
public interface StatementFactory {

    PreparedStatement createPreparedStatement(Connection connection, String s);

    CallableStatement createCallableStatement(Connection connection, String s);
}
