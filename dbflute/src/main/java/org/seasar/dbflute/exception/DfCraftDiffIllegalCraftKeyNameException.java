package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfCraftDiffIllegalCraftKeyNameException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfCraftDiffIllegalCraftKeyNameException(String msg) {
        super(msg);
    }

    public DfCraftDiffIllegalCraftKeyNameException(String msg, Throwable e) {
        super(msg, e);
    }
}
