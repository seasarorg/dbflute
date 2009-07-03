package org.seasar.dbflute.properties.assistant.classification;

import org.seasar.dbflute.exception.DfIllegalPropertyTypeException;

/**
 * @author jflute
 * @since 0.9.5.1 (2009/07/03 Friday)
 */
public class DfClassificationIllegalPropertyTypeException extends DfIllegalPropertyTypeException {

    private static final long serialVersionUID = 1L;

    public DfClassificationIllegalPropertyTypeException(String msg) {
        super(msg);
    }

    public DfClassificationIllegalPropertyTypeException(String msg, Throwable e) {
        super(msg, e);
    }
}
