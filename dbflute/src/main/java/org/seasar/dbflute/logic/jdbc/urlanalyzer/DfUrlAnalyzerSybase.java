package org.seasar.dbflute.logic.jdbc.urlanalyzer;

import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.7.6 (2010/11/26 Friday)
 */
public class DfUrlAnalyzerSybase extends DfUrlAnalyzerBase {

    public DfUrlAnalyzerSybase(String url) {
        super(url);
    }

    protected String doExtractCatalog() {
        final String pureUrl = Srl.substringFirstFront(_url, ";", "?", "&");
        final String catalog = Srl.substringLastRear(pureUrl, "/", ":");
        return !catalog.equals(pureUrl) ? catalog : null;
    }
}
