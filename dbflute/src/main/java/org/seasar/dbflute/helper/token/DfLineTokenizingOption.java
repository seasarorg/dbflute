package org.seasar.dbflute.helper.token;

/**
 * @author DBFlute(AutoGenerator)
 */
public class DfLineTokenizingOption {

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========
    protected String _delimiter;

    protected boolean _trimDoubleQuotation;

    protected boolean _handleEmtpyAsNull;

    // =====================================================================================
    //                                                                           Easy-to-Use
    //                                                                           ===========
    public DfLineTokenizingOption delimitateByComma() {
        _delimiter = ",";
        return this;
    }

    public DfLineTokenizingOption delimitateByTab() {
        _delimiter = "\t";
        return this;
    }

    public DfLineTokenizingOption trimDoubleQuotation() {
        _trimDoubleQuotation = true;
        return this;
    }

    public DfLineTokenizingOption handleEmtpyAsNull() {
        _handleEmtpyAsNull = true;
        return this;
    }

    // =====================================================================================
    //                                                                              Accessor
    //                                                                              ========
    public String getDelimiter() {
        return _delimiter;
    }

    public void setDelimiter(String delimiter) {
        _delimiter = delimiter;
    }

    public boolean isTrimDoubleQuotation() {
        return _trimDoubleQuotation;
    }

    public boolean isHandleEmtpyAsNull() {
        return _handleEmtpyAsNull;
    }
}