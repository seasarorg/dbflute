package org.seasar.dbflute.helper.token.line;

/**
 * @author jflute
 */
public class LineMakingOption {

    protected String _delimiter;

    protected boolean _quoteByDoubleQuotation;

    protected boolean _trimSpace;

    public LineMakingOption delimitateByComma() {
        _delimiter = ",";
        return this;
    }

    public LineMakingOption delimitateByTab() {
        _delimiter = "\t";
        return this;
    }

    public String getDelimiter() {
        return _delimiter;
    }

    public void setDelimiter(String delimiter) {
        _delimiter = delimiter;
    }

    public LineMakingOption quoteByDoubleQuotation() {
        _quoteByDoubleQuotation = true;
        return this;
    }

    public boolean isQuoteByDoubleQuotation() {
        return _quoteByDoubleQuotation;
    }

    public LineMakingOption trimSpace() {
        _trimSpace = true;
        return this;
    }

    public boolean isTrimSpace() {
        return _trimSpace;
    }
}