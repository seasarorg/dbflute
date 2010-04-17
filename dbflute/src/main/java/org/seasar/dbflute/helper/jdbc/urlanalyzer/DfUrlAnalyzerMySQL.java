package org.seasar.dbflute.helper.jdbc.urlanalyzer;

import org.seasar.dbflute.util.Srl;

/**
 * 
 * @author jflute
 * @since 0.9.6 (2009/10/31 Saturday)
 */
public class DfUrlAnalyzerMySQL implements DfUrlAnalyzer {

    protected String _url;

    public DfUrlAnalyzerMySQL(String url) {
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
