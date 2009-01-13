package com.example.dbflute.basic.dbflute.allcommon;

import javax.sql.XADataSource;

import org.dbflute.cbean.ConditionBeanContext;
import org.dbflute.resource.TnSqlLogRegistry;
import org.dbflute.util.SimpleSystemUtil;
import org.seasar.extension.dbcp.impl.XADataSourceImpl;



/**
 * @author DBFlute(AutoGenerator)
 */
public class DBFluteInitializer {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log-instance. */
    private static final org.apache.commons.logging.Log _log = org.apache.commons.logging.LogFactory.getLog(DBFluteInitializer.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor. And initialize various components.
     * @param xaDataSource The data source of XA. (Nullable)
     */
    public DBFluteInitializer(XADataSource xaDataSource) {
        _log.info("/* * * * * * * * * * * * * * * * * * * * * * * * * * * * {DBFlute}");
        DBFluteConfig.getInstance().unlock();
        handleSqlLogRegistry();
        loadCoolClasses();
        if (xaDataSource != null) {
            showInformation(xaDataSource);
        }
        DBFluteConfig.getInstance().lock();
        _log.info("* * * * */");
    }

    // ===================================================================================
    //                                                                        Cool Classes
    //                                                                        ============
    protected void loadCoolClasses() { // for S2Container basically 
        ConditionBeanContext.loadCoolClasses(); // Against the ClassLoader Headache!
    }

    // ===================================================================================
    //                                                                    SQL Log Registry
    //                                                                    ================
    protected void handleSqlLogRegistry() {
        if (DBFluteConfig.getInstance().isUseSqlLogRegistry()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("{SqlLog Information}").append(getLineSeparator());
            sb.append("  [SqlLogRegistry]").append(getLineSeparator());
            if (TnSqlLogRegistry.setupSqlLogRegistry()) {
                sb.append("    ...Setting up SqlLogRegistry(org.seasar.extension.jdbc)!").append(getLineSeparator());
                sb.append("    Because the property 'useSqlLogRegistry' of the config of DBFlute is true.");
            } else {
                sb.append("    SqlLogRegistry(org.seasar.extension.jdbc) is not supported at the version!");
            }
           _log.info(sb);
        } else {
            final Object sqlLogRegistry = TnSqlLogRegistry.findContainerSqlLogRegistry();
            if (sqlLogRegistry != null) {
                TnSqlLogRegistry.closeRegistration();
            }
        }
    }

    // ===================================================================================
    //                                                                         Information
    //                                                                         ===========
    protected void showInformation(XADataSource xaDataSource) {
        if (xaDataSource != null && xaDataSource instanceof XADataSourceImpl) { // by Seasar
            final StringBuilder sb = new StringBuilder();
            final XADataSourceImpl xaDataSourceImpl = (XADataSourceImpl)xaDataSource;
            final String driverClassName = xaDataSourceImpl.getDriverClassName();
            final String url = xaDataSourceImpl.getURL();
            final String user = xaDataSourceImpl.getUser();
            sb.append("  [XADataSource]:").append(getLineSeparator());
            sb.append("    driver = " + driverClassName).append(getLineSeparator());
            sb.append("    url    = " + url).append(getLineSeparator());
            sb.append("    user   = " + user);
            _log.info("{Injection Information}" + getLineSeparator() + sb);
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String getLineSeparator() {
        return SimpleSystemUtil.getLineSeparator();
    }
}
