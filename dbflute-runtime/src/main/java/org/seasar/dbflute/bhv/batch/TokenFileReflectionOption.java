package org.seasar.dbflute.bhv.batch;

import org.seasar.dbflute.helper.token.file.FileTokenizingOption;

/**
 * @author jflute
 */
public class TokenFileReflectionOption {

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========
    protected boolean _interruptIfError;

    protected FileTokenizingOption _fileTokenizingOption = new FileTokenizingOption();

    // =====================================================================================
    //                                                                           Easy-to-Use
    //                                                                           ===========
    public TokenFileReflectionOption delimitateByComma() {
        _fileTokenizingOption.delimitateByComma();
        return this;
    }

    public TokenFileReflectionOption delimitateByTab() {
        _fileTokenizingOption.delimitateByTab();
        return this;
    }

    public TokenFileReflectionOption encodeAsUTF8() {
        _fileTokenizingOption.encodeAsUTF8();
        return this;
    }

    public TokenFileReflectionOption encodeAsWindows31J() {
        _fileTokenizingOption.encodeAsWindows31J();
        return this;
    }

    public TokenFileReflectionOption handleEmptyAsNull() {
        _fileTokenizingOption.handleEmptyAsNull();
        return this;
    }

    public TokenFileReflectionOption interruptIfError() {
        _interruptIfError = true;
        return this;
    }

    // =====================================================================================
    //                                                                              Accessor
    //                                                                              ========
    public String getDelimiter() {
        return _fileTokenizingOption.getDelimiter();
    }

    public void setDelimiter(String delimiter) {
        _fileTokenizingOption.setDelimiter(delimiter);
    }

    public String getEncoding() {
        return _fileTokenizingOption.getEncoding();
    }

    public void setEncoding(String encoding) {
        _fileTokenizingOption.setDelimiter(encoding);
    }

    public boolean isHandleEmptyAsNull() {
        return _fileTokenizingOption.isHandleEmptyAsNull();
    }

    public boolean isInterruptIfError() {
        return _interruptIfError;
    }
}
