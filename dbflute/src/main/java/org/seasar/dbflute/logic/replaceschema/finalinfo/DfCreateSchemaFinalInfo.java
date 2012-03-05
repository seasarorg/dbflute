package org.seasar.dbflute.logic.replaceschema.finalinfo;

import org.seasar.dbflute.exception.SQLFailureException;

/**
 * @author jflute
 */
public class DfCreateSchemaFinalInfo extends DfAbstractSchemaTaskFinalInfo {

    protected SQLFailureException _breakCause;

    public SQLFailureException getBreakCause() {
        return _breakCause;
    }

    public void setBreakCause(SQLFailureException breakCause) {
        this._breakCause = breakCause;
    }
}
