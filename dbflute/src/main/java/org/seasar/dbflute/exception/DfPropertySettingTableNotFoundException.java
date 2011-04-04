package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfPropertySettingTableNotFoundException extends DfTableNotFoundException {

    private static final long serialVersionUID = 1L;

    public DfPropertySettingTableNotFoundException(String msg) {
        super(msg);
    }

    public DfPropertySettingTableNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
