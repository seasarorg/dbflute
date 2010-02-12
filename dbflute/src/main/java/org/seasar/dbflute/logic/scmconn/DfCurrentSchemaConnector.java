package org.seasar.dbflute.logic.scmconn;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.jdbc.sqlfile.DfSqlFileGetter;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfo;
import org.seasar.dbflute.helper.language.DfLanguageDependencyInfoJava;
import org.seasar.dbflute.properties.DfBasicProperties;

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
    protected String _schema;
    protected DfBasicProperties _basicProperties;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfCurrentSchemaConnector(String schema, DfBasicProperties basicProperties) {
        _schema = schema;
        _basicProperties = basicProperties;
    }

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    public void connectSchema(DataSource dataSource) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            String msg = "Failed to connect the database!";
            throw new IllegalStateException(msg, e);
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

    public void connectSchema(Connection conn) {
        if (_basicProperties.isDatabaseDB2() && _schema != null) {
            final String sql = "SET CURRENT SCHEMA = " + _schema.trim();
            Statement st = null;
            try {
                st = conn.createStatement();
            } catch (SQLException e) {
                _log.warn("Failed to create statement: " + e.getMessage());
                return;
            }
            try {
                _log.info("...Executing helper SQL:\n" + sql);
                st.execute(sql);
            } catch (SQLException continued) {
                String msg = "Failed to execute the SQL: " + sql;
                _log.warn(msg, continued);
                return;
            } finally {
                if (st != null) {
                    try {
                        st.close();
                    } catch (SQLException ignored) {
                    }
                }
            }
        } else if (_basicProperties.isDatabaseOracle() && _schema != null) {
            final String sql = "ALTER SESSION SET CURRENT_SCHEMA = " + _schema.trim();
            Statement st = null;
            try {
                st = conn.createStatement();
            } catch (SQLException e) {
                _log.warn("Failed to create statement: " + e.getMessage());
                return;
            }
            try {
                _log.info("...Executing helper SQL:\n" + sql);
                st.execute(sql);
            } catch (SQLException continued) {
                String msg = "Failed to execute the SQL: " + sql;
                _log.warn(msg, continued);
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
    }

    protected List<File> collectSqlFile(String sqlDirectory) {
        return createSqlFileGetter().getSqlFileList(sqlDirectory);
    }

    protected DfSqlFileGetter createSqlFileGetter() {
        final DfLanguageDependencyInfo dependencyInfo = _basicProperties.getLanguageDependencyInfo();
        return new DfSqlFileGetter() {
            @Override
            protected boolean acceptSqlFile(File file) {
                if (!dependencyInfo.isCompileTargetFile(file)) {
                    return false;
                }
                return super.acceptSqlFile(file);
            }
        };
    }

    protected boolean containsSrcMainJava(String sqlDirectory) {
        return DfLanguageDependencyInfoJava.containsSrcMainJava(sqlDirectory);
    }

    protected String replaceSrcMainJavaToSrcMainResources(String sqlDirectory) {
        return DfLanguageDependencyInfoJava.replaceSrcMainJavaToSrcMainResources(sqlDirectory);
    }
}
