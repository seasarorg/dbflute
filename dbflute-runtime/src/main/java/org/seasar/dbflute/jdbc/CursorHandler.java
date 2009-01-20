package org.seasar.dbflute.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The handler of cursor.
 * @author jflute
 */
public interface CursorHandler {

    /**
     * @param resultSet Result set. (NotNull)
     * @return Result
     * @throws java.sql.SQLException
     */
    Object handle(ResultSet resultSet) throws SQLException;
}
