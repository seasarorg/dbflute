package org.seasar.dbflute.logic.jdbc.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.properties.facade.DfDatabaseTypeFacadeProp;
import org.seasar.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 * @since 0.9.4 (2009/03/03 Tuesday)
 */
public class DfCurrentSchemaConnector {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfCurrentSchemaConnector.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected UnifiedSchema _unifiedSchema;
    protected DfDatabaseTypeFacadeProp _databaseTypeFacadeProp;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfCurrentSchemaConnector(UnifiedSchema unifiedSchema, DfDatabaseTypeFacadeProp databaseTypeFacadeProp) {
        _unifiedSchema = unifiedSchema;
        _databaseTypeFacadeProp = databaseTypeFacadeProp;
    }

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    public void connectSchema(DataSource dataSource) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
        connectSchema(conn);
    }

    public void connectSchema(Connection conn) throws SQLException {
        if (!_unifiedSchema.existsPureSchema()) {
            return;
        }
        final String pureSchema = _unifiedSchema.getPureSchema();
        if (_databaseTypeFacadeProp.isDatabaseDB2()) {
            final String sql = "SET CURRENT SCHEMA = " + pureSchema;
            executeCurrentSchemaSql(conn, sql);
        } else if (_databaseTypeFacadeProp.isDatabaseOracle()) {
            final String sql = "ALTER SESSION SET CURRENT_SCHEMA = " + pureSchema;
            executeCurrentSchemaSql(conn, sql);
        } else if (_databaseTypeFacadeProp.isDatabasePostgreSQL()) {
            final String sql = "set search_path to " + pureSchema;
            executeCurrentSchemaSql(conn, sql);
        }
    }

    protected void executeCurrentSchemaSql(Connection conn, String sql) throws SQLException {
        final Statement st = conn.createStatement();
        try {
            _log.info("...Connecting to the schema: " + _unifiedSchema);
            _log.info(sql);
            st.execute(sql);
        } catch (SQLException continued) { // continue because it's supplementary SQL
            String msg = "Failed to execute the SQL:" + ln() + sql + ln() + continued.getMessage();
            _log.warn(msg);
            return;
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
