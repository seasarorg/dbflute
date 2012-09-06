package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfCraftDiffCraftTitleNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfCraftDiffCraftTitleNotFoundException(String msg) {
        super(msg);
    }

    public DfCraftDiffCraftTitleNotFoundException(String msg, Throwable e) {
        super(msg, e);
    }
}
