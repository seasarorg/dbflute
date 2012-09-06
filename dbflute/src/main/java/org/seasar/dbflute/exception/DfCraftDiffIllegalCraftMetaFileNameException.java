package org.seasar.dbflute.exception;

/**
 * @author jflute
 */
public class DfCraftDiffIllegalCraftMetaFileNameException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DfCraftDiffIllegalCraftMetaFileNameException(String msg) {
        super(msg);
    }

    public DfCraftDiffIllegalCraftMetaFileNameException(String msg, Throwable e) {
        super(msg, e);
    }
}
