package org.seasar.dbflute.exception;

/**
 * The exception of when the value of bind variable is null about outsideSql.
 * @author DBFlute(AutoGenerator)
 */
public class BindVariableParameterNullValueException extends RuntimeException {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * @param msg Exception message. (NotNull)
     */
    public BindVariableParameterNullValueException(String msg) {
        super(msg);
    }
}
