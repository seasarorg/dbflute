package com.example.dbflute.basic.dbflute.allcommon;

import org.dbflute.DBDef;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.sqlclause.*;
import org.dbflute.dbmeta.DBMetaProvider;


/**
 * The creator of SQL clause.
 * @author DBFlute(AutoGenerator)
 */
public class ImplementedSqlClauseCreator implements SqlClauseCreator {

	/**
	 * Create SQL clause. {for condition-bean}
	 * @param cb Condition-bean. (NotNull) 
	 * @return SQL clause. (NotNull)
	 */
    public SqlClause createSqlClause(ConditionBean cb) {
        final String tableSqlName = cb.getTableSqlName();
		final SqlClause sqlClause = createSqlClause(tableSqlName);
        return sqlClause;
    }

	/**
	 * Create SQL clause.
	 * @param tableDbName The DB name of table. (NotNull) 
	 * @return SQL clause. (NotNull)
	 */
    public SqlClause createSqlClause(String tableDbName) {
        DBMetaProvider dbmetaProvider = new DBMetaInstanceHandler();
        if (isCurrentDBDef(DBDef.MySQL)) {
            return new SqlClauseMySql(tableDbName).provider(dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.PostgreSQL)) {
            return new SqlClausePostgreSql(tableDbName).provider(dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.Oracle)) {
            return new SqlClauseOracle(tableDbName).provider(dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.DB2)) {
            return new SqlClauseDb2(tableDbName).provider(dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.SQLServer)) {
            return new SqlClauseSqlServer(tableDbName).provider(dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.FireBird)) {
            return new SqlClauseFirebird(tableDbName).provider(dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.H2)) {
            return new SqlClauseH2(tableDbName).provider(dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.Derby)) {
            return new SqlClauseDerby(tableDbName).provider(dbmetaProvider);
        } else {
            return new SqlClauseH2(tableDbName).provider(dbmetaProvider);
        }
    }

    protected boolean isCurrentDBDef(DBDef currentDBDef) {
	    return DBCurrent.getInstance().isCurrentDBDef(currentDBDef);
    }
}
