package org.seasar.dbflute.bhv.batch;

import org.seasar.dbflute.helper.token.file.FileMakingOption;

/**
 * @author jflute
 */
public class TokenFileOutputOption {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected FileMakingOption _fileMakingOption = new FileMakingOption();

    // ===================================================================================
    //                                                                         Easy-to-Use
    //                                                                         ===========
    public TokenFileOutputOption delimitateByComma() {
        _fileMakingOption.delimitateByComma();
        return this;
    }

    public TokenFileOutputOption delimitateByTab() {
        _fileMakingOption.delimitateByTab();
        return this;
    }

    public TokenFileOutputOption encodeAsUTF8() {
        _fileMakingOption.encodeAsUTF8();
        return this;
    }

    public TokenFileOutputOption encodeAsWindows31J() {
        _fileMakingOption.encodeAsWindows31J();
        return this;
    }

    public TokenFileOutputOption separateCrLf() {
        _fileMakingOption.separateCrLf();
        return this;
    }

    public TokenFileOutputOption separateLf() {
        _fileMakingOption.separateLf();
        return this;
    }

    public TokenFileOutputOption goodByeDoubleQuotation() {
        _fileMakingOption.goodByeDoubleQuotation();
        return this;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getEncoding() {
        return _fileMakingOption.getEncoding();
    }

    public void setEncoding(String encoding) {
        _fileMakingOption.setDelimiter(encoding);
    }

    public String getDelimiter() {
        return _fileMakingOption.getDelimiter();
    }

    public void setDelimiter(String delimiter) {
        _fileMakingOption.setDelimiter(delimiter);
    }

    public String getLineSeparator() {
        return _fileMakingOption.getLineSeparator();
    }

    public void setLineSeparator(String lineSeparator) {
        _fileMakingOption.setLineSeparator(lineSeparator);
    }

    public boolean isGoodByeDoubleQuotation() {
        return _fileMakingOption.isGoodByeDoubleQuotation();
    }

    public FileMakingOption getFileMakingOption() {
        return _fileMakingOption;
    }
}
