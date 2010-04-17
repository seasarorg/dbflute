package org.seasar.dbflute.logic.factory;

import org.seasar.dbflute.logic.urlanalyzer.DfUrlAnalyzer;
import org.seasar.dbflute.logic.urlanalyzer.DfUrlAnalyzerMySQL;
import org.seasar.dbflute.logic.urlanalyzer.DfUrlAnalyzerPostgreSQL;
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
            return createNullAnalyzer();
        } else if (_basicProperties.isDatabaseSQLServer()) {
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
