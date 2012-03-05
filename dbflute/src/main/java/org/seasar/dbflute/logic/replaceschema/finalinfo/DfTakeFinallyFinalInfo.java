package org.seasar.dbflute.logic.replaceschema.finalinfo;

import org.seasar.dbflute.exception.DfTakeFinallyAssertionFailureException;
import org.seasar.dbflute.exception.SQLFailureException;

/**
 * @author jflute
 */
public class DfTakeFinallyFinalInfo extends DfAbstractSchemaTaskFinalInfo {

    protected SQLFailureException _breakCause;

    protected DfTakeFinallyAssertionFailureException _assertionEx;

    public SQLFailureException getBreakCause() {
        return _breakCause;
    }

    public void setBreakCause(SQLFailureException breakCause) {
        this._breakCause = breakCause;
    }

    public DfTakeFinallyAssertionFailureException getAssertionEx() {
        return _assertionEx;
    }

    public void setAssertionEx(DfTakeFinallyAssertionFailureException assertionEx) {
        this._assertionEx = assertionEx;
    }
}
