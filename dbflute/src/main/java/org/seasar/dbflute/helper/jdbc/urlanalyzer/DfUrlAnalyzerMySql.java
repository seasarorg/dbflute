package org.seasar.dbflute.helper.jdbc.urlanalyzer;

/**
 * 
 * @author jflute
 * @since 0.9.6 (2009/10/31 Saturday)
 */
public class DfUrlAnalyzerMySql implements DfUrlAnalyzer {

    protected String _url;

    public String extractSchema() {
        return extractSchemaFromMySqlUrl();
    }

    protected String extractSchemaFromMySqlUrl() {
        if (_url == null) {
            return null;
        }
        final int schemaIndex = _url.lastIndexOf("/");
        if (schemaIndex < 0) {
            return null;
        }
        final String rear = _url.substring(schemaIndex + "/".length());
        final int attributeIndex = rear.indexOf("?");
        if (attributeIndex < 0) {
            return rear;
        }
        return rear.substring(0, attributeIndex);
    }

    public void setUrl(String url) {
        _url = url;
    }
}
