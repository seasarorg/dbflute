package org.dbflute.resource;

import org.dbflute.jdbc.LatestSqlProvider;

/**
 * The provider of latest SQL using SqlLogRegistry.
 * This instance should be singleton.
 * @author DBFlute(AutoGenerator)
 */
public class SqlLogRegistryLatestSqlProvider implements LatestSqlProvider {

    public String getDisplaySql() {
        return TnSqlLogRegistry.peekCompleteSql();
    }

	public void clearSqlCache() {
        TnSqlLogRegistry.clearSqlLogRegistry();
	}
}
