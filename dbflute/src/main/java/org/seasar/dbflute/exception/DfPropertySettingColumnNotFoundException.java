package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfPropertySettingColumnNotFoundException extends DfColumnNotFoundException {

    private static final long serialVersionUID = 1L;

    public DfPropertySettingColumnNotFoundException(String msg) {
        super(msg);
    }

    public DfPropertySettingColumnNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
