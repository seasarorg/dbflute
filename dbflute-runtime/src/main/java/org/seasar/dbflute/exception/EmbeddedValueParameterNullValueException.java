package org.seasar.dbflute.exception;

/**
 * The exception of when the value of embedded value is null about outsideSql.
 * @author DBFlute(AutoGenerator)
 */
public class EmbeddedValueParameterNullValueException extends RuntimeException {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * @param msg Exception message. (NotNull)
     */
    public EmbeddedValueParameterNullValueException(String msg) {
        super(msg);
    }
}
