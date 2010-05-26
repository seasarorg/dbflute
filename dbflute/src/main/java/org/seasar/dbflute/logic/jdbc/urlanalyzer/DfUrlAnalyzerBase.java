package org.seasar.dbflute.logic.jdbc.urlanalyzer;

/**
 * @author jflute
 * @since 0.9.6.8 (2010/04/17 Saturday)
 */
public abstract class DfUrlAnalyzerBase implements DfUrlAnalyzer {

    protected String _url;

    public DfUrlAnalyzerBase(String url) {
        this._url = url;
    }

    public String extractCatalog() {
        if (_url == null) {
            return null;
        }
        final String catalog = doExtractCatalog();
        if (catalog == null) {
            return null;
        }
        if (catalog.equals(_url)) {
            return null;
        }
        return catalog;
    }

    protected abstract String doExtractCatalog();
}
