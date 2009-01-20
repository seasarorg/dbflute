package org.seasar.dbflute.cbean.sqlclause;

import org.seasar.dbflute.cbean.ConditionBean;

/**
 * The creator of SQL clause.
 * @author jflute
 */
public interface SqlClauseCreator {

	/**
	 * Create SQL clause. {for condition-bean}
	 * @param cb Condition-bean. (NotNull) 
	 * @return SQL clause. (NotNull)
	 */
    public SqlClause createSqlClause(ConditionBean cb);

	/**
	 * Create SQL clause.
	 * @param tableDbName The DB name of table. (NotNull) 
	 * @return SQL clause. (NotNull)
	 */
    public SqlClause createSqlClause(String tableDbName);
}
