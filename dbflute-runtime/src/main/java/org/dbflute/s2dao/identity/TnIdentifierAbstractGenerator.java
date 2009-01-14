package org.dbflute.s2dao.identity;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.dbflute.jdbc.StatementFactory;
import org.dbflute.resource.SQLExceptionHandler;
import org.dbflute.s2dao.beans.TnPropertyDesc;
import org.dbflute.s2dao.metadata.TnPropertyType;
import org.dbflute.s2dao.sqlhandler.InternalBasicSelectHandler;
import org.dbflute.s2dao.valuetype.TnValueType;
import org.seasar.extension.jdbc.ResultSetHandler;


/**
 * @author DBFlute(AutoGenerator)
 */
public abstract class TnIdentifierAbstractGenerator implements TnIdentifierGenerator {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected TnPropertyType propertyType;
    protected ResultSetHandler resultSetHandler;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnIdentifierAbstractGenerator(TnPropertyType propertyType) {
        this.propertyType = propertyType;
        resultSetHandler = new InternalIdentifierResultSetHandler(propertyType.getValueType());
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected Object executeSql(DataSource ds, String sql, Object[] args) {
        InternalBasicSelectHandler selectHandler = createSelectHandler(ds, sql);
        if (args != null) {
            selectHandler.setLoggingMessageSqlArgs(args);
        }
        return selectHandler.execute(args);
    }

    protected InternalBasicSelectHandler createSelectHandler(DataSource ds, String sql) {
        // Use original statement factory for identifier generator.
        return new InternalBasicSelectHandler(ds, sql, resultSetHandler, createStatementFactory(ds, sql));
    }

    protected StatementFactory createStatementFactory(DataSource ds, String sql) {
        return new InternalIdentifierGeneratorStatementFactory();
    }

    protected void reflectIdentifier(Object bean, Object value) {
        if (propertyType == null) {
            String msg = "The arguement[propertyType] should not be null: value=" + value;
            throw new IllegalArgumentException(msg);
        }
        TnPropertyDesc pd = propertyType.getPropertyDesc();
        pd.setValue(bean, value);
    }

    // ===================================================================================
    //                                                                  Result Set Handler
    //                                                                  ==================
    protected static class InternalIdentifierResultSetHandler implements ResultSetHandler {
        private TnValueType valueType;
        public InternalIdentifierResultSetHandler(TnValueType valueType) {
            this.valueType = valueType;
        }
        public Object handle(ResultSet rs) throws SQLException {
            if (rs.next()) {
                return valueType.getValue(rs, 1);
            }
            return null;
        }
    }

    // ===================================================================================
    //                                                                   Statement Factory
    //                                                                   =================
    protected static class InternalIdentifierGeneratorStatementFactory implements StatementFactory {
        public PreparedStatement createPreparedStatement(Connection conn, String sql) {
            try {
                return conn.prepareStatement(sql);
            } catch (SQLException e) {
                handleSQLException(e, null);
                return null; // Unreachable!
            }
        }
        public CallableStatement createCallableStatement(Connection conn, String sql) {
            try {
                return conn.prepareCall(sql);
            } catch (SQLException e) {
                handleSQLException(e, null);
                return null; // Unreachable!
            }
        }

        protected void handleSQLException(SQLException e, Statement statement) {
            new SQLExceptionHandler().handleSQLException(e, statement);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getPropertyName() {
        return propertyType.getPropertyName();
    }
}
