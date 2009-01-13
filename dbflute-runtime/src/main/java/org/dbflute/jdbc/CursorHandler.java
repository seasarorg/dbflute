package org.dbflute.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The handler of cursor.
 * @author DBFlute(AutoGenerator)
 */
public interface CursorHandler {

    /**
     * @param resultSet Result set. (NotNull)
     * @return Result
     * @throws java.sql.SQLException
     */
    Object handle(ResultSet resultSet) throws SQLException;
}
