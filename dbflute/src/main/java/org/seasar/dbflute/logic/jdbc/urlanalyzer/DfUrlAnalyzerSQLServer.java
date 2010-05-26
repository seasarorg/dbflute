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
        final String rear = Srl.substringFirstRear(pureUrl, "DatabaseName=");
        final String catalog = Srl.substringFirstFront(rear, ";");
        return !catalog.equals(pureUrl) ? catalog : null;
    }
}
