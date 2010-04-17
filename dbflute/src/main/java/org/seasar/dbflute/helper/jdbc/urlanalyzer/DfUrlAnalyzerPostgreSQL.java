package org.seasar.dbflute.helper.jdbc.urlanalyzer;

import org.seasar.dbflute.util.Srl;

/**
 * 
 * @author jflute
 * @since 0.9.6 (2009/10/31 Saturday)
 */
public class DfUrlAnalyzerPostgreSQL implements DfUrlAnalyzer {

    protected String _url;

    public DfUrlAnalyzerPostgreSQL(String url) {
        this._url = url;
    }

    public String extractCatalog() {
        if (_url == null) {
            return null;
        }
        final String pureUrl = Srl.substringFirstFront(_url, "?");
        return Srl.substringFirstRear(pureUrl, "/");
    }
}
