package org.seasar.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.seasar.dbflute.bhv.core.SqlExecution;
import org.seasar.dbflute.jdbc.StatementFactory;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public abstract class TnAbstractSqlCommand implements TnSqlCommand, SqlExecution {

	// ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private DataSource dataSource;
    private StatementFactory statementFactory;
    private String sql;

	// ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnAbstractSqlCommand(DataSource dataSource, StatementFactory statementFactory) {
        this.dataSource = dataSource;
        this.statementFactory = statementFactory;
    }

	// ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DataSource getDataSource() {
        return dataSource;
    }

    public StatementFactory getStatementFactory() {
        return statementFactory;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
