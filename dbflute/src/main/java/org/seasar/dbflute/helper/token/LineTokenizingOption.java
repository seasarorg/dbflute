package org.seasar.dbflute.helper.token;

/**
 * @author DBFlute(AutoGenerator)
 */
public class LineTokenizingOption {

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========
    protected String _delimiter;

    protected boolean _trimDoubleQuotation;

    protected boolean _handleEmtpyAsNull;

    // =====================================================================================
    //                                                                           Easy-to-Use
    //                                                                           ===========
    public LineTokenizingOption delimitateByComma() {
        _delimiter = ",";
        return this;
    }

    public LineTokenizingOption delimitateByTab() {
        _delimiter = "\t";
        return this;
    }

    public LineTokenizingOption trimDoubleQuotation() {
        _trimDoubleQuotation = true;
        return this;
    }

    public LineTokenizingOption handleEmtpyAsNull() {
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