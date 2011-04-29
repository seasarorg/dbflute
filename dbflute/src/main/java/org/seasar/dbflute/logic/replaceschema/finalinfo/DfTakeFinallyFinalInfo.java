package org.seasar.dbflute.logic.replaceschema.finalinfo;

import org.seasar.dbflute.exception.DfTakeFinallyAssertionFailureException;

/**
 * @author jflute
 */
public class DfTakeFinallyFinalInfo extends DfAbstractSchemaTaskFinalInfo {

    protected DfTakeFinallyAssertionFailureException _assertionEx;

    public DfTakeFinallyAssertionFailureException getAssertionEx() {
        return _assertionEx;
    }

    public void setAssertionEx(DfTakeFinallyAssertionFailureException assertionEx) {
        this._assertionEx = assertionEx;
    }
}
