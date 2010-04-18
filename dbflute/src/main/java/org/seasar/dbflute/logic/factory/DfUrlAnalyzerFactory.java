package org.seasar.dbflute.logic.factory;

import org.seasar.dbflute.logic.urlanalyzer.DfUrlAnalyzer;
import org.seasar.dbflute.logic.urlanalyzer.DfUrlAnalyzerDerby;
import org.seasar.dbflute.logic.urlanalyzer.DfUrlAnalyzerH2;
import org.seasar.dbflute.logic.urlanalyzer.DfUrlAnalyzerMSAccess;
import org.seasar.dbflute.logic.urlanalyzer.DfUrlAnalyzerMySQL;
import org.seasar.dbflute.logic.urlanalyzer.DfUrlAnalyzerPostgreSQL;
import org.seasar.dbflute.logic.urlanalyzer.DfUrlAnalyzerSQLServer;
import org.seasar.dbflute.logic.urlanalyzer.DfUrlAnalyzerSQLite;
import org.seasar.dbflute.properties.DfBasicProperties;

/**
 * @author jflute
 * @since 0.8.1 (2008/10/10 Friday)
 */
public class DfUrlAnalyzerFactory {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DfBasicProperties _basicProperties;
    protected String _url;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfUrlAnalyzerFactory(DfBasicProperties basicProperties, String url) {
        _basicProperties = basicProperties;
        _url = url;
    }

    // ===================================================================================
    //                                                                              Create
    //                                                                              ======
    /**
     * @return The analyzer of URL. (NotNull)
     */
    public DfUrlAnalyzer createAnalyzer() {
        if (_basicProperties.isDatabaseMySQL()) {
            return new DfUrlAnalyzerMySQL(_url);
        } else if (_basicProperties.isDatabasePostgreSQL()) {
            return new DfUrlAnalyzerPostgreSQL(_url);
        } else if (_basicProperties.isDatabaseOracle()) {
            // Oracle does not support catalog
            return createNullAnalyzer();
        } else if (_basicProperties.isDatabaseDB2()) {
            // DB2 (JDBC driver) does not support catalog
            //return new DfUrlAnalyzerDB2(_url);
            return createNullAnalyzer();
        } else if (_basicProperties.isDatabaseSQLServer()) {
            return new DfUrlAnalyzerSQLServer(_url);
        } else if (_basicProperties.isDatabaseH2()) {
            return new DfUrlAnalyzerH2(_url);
        } else if (_basicProperties.isDatabaseDerby()) {
            return new DfUrlAnalyzerDerby(_url);
        } else if (_basicProperties.isDatabaseSQLite()) {
            return new DfUrlAnalyzerSQLite(_url);
        } else if (_basicProperties.isDatabaseMSAccess()) {
            return new DfUrlAnalyzerMSAccess(_url);
        }
        return createNullAnalyzer();
    }

    protected DfUrlAnalyzer createNullAnalyzer() {
        return new DfUrlAnalyzer() {
            public String extractCatalog() {
                return null;
            }
        };
    }
}
