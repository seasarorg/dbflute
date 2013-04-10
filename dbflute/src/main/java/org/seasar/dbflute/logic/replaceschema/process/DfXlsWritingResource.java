package org.seasar.dbflute.logic.replaceschema.process;

/**
 * @author jflute
 */
public class DfXlsWritingResource {

    protected boolean _application;
    protected boolean _commonType;
    protected boolean _firstXls;
    protected boolean _reverseXls;

    public boolean isApplication() {
        return _application;
    }

    public DfXlsWritingResource application() {
        _application = true;
        return this;
    }

    public boolean isCommonType() {
        return _commonType;
    }

    public DfXlsWritingResource commonType() {
        _commonType = true;
        return this;
    }

    public boolean isFirstXls() {
        return _firstXls;
    }

    public DfXlsWritingResource firstXls() {
        _firstXls = true;
        return this;
    }

    public boolean isReverseXls() {
        return _reverseXls;
    }

    public DfXlsWritingResource reverseXls() {
        _reverseXls = true;
        return this;
    }
}
