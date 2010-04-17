package org.seasar.dbflute.logic.urlanalyzer;

import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.6.8 (2010/04/17 Saturday)
 */
public class DfUrlAnalyzerH2 extends DfUrlAnalyzerBase {

    public DfUrlAnalyzerH2(String url) {
        super(url);
    }

    protected String doExtractCatalog() {
        final String pureUrl = Srl.substringFirstFront(_url, ";", "?", "&");
        final String catalog = Srl.substringLastRear(pureUrl, "/", ":");
        return !catalog.equals(pureUrl) ? catalog : null;
    }
}
