package org.seasar.dbflute.logic.jdbc.urlanalyzer;

import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.6.8 (2010/04/17 Saturday)
 */
public class DfUrlAnalyzerSQLServer extends DfUrlAnalyzerBase {

    public DfUrlAnalyzerSQLServer(String url) {
        super(url);
    }

    protected String doExtractCatalog() {
        final String pureUrl = Srl.substringFirstRear(_url, "?");

        // because the JDBC driver for SQLServer
        // treats a URL as case insensitive
        final String key = "databasename=";
        if (!Srl.containsAllIgnoreCase(pureUrl, key)) {
            return null;
        }
        final String rear = Srl.substringFirstRearIgnoreCase(pureUrl, key);
        return Srl.substringFirstFront(rear, ";");
    }
}
