package org.seasar.dbflute.helper.token;

/**
 * @author DBFlute(AutoGenerator)
 */
public class DfLineMakingOption {

    protected String _delimiter;

    protected boolean _quoteByDoubleQuotation;

    protected boolean _trimSpace;

    public DfLineMakingOption delimitateByComma() {
        _delimiter = ",";
        return this;
    }

    public DfLineMakingOption delimitateByTab() {
        _delimiter = "\t";
        return this;
    }

    public String getDelimiter() {
        return _delimiter;
    }

    public void setDelimiter(String delimiter) {
        _delimiter = delimiter;
    }

    public DfLineMakingOption quoteByDoubleQuotation() {
        _quoteByDoubleQuotation = true;
        return this;
    }

    public boolean isQuoteByDoubleQuotation() {
        return _quoteByDoubleQuotation;
    }

    public DfLineMakingOption trimSpace() {
        _trimSpace = true;
        return this;
    }

    public boolean isTrimSpace() {
        return _trimSpace;
    }
}