package org.seasar.dbflute.logic.replaceschema.finalinfo;

import org.seasar.dbflute.exception.DfAlterCheckAlterSqlFailureException;
import org.seasar.dbflute.exception.DfAlterCheckDifferenceFoundException;
import org.seasar.dbflute.exception.DfAlterCheckReplaceSchemaFailureException;
import org.seasar.dbflute.exception.DfAlterCheckSavePreviousFailureException;
import org.seasar.dbflute.exception.SQLFailureException;

/**
 * @author jflute
 */
public class DfAlterCheckFinalInfo extends DfAbstractSchemaTaskFinalInfo {

    // one exists, the others always does not exist
    protected SQLFailureException _breakCause;
    protected DfAlterCheckSavePreviousFailureException _savePreviousFailureEx;
    protected DfAlterCheckAlterSqlFailureException _alterSqlFailureEx;
    protected DfAlterCheckReplaceSchemaFailureException _replaceSchemaFailureEx;
    protected DfAlterCheckDifferenceFoundException _diffFoundEx;

    public void throwAlterCheckExceptionIfExists() {
        if (_breakCause != null) {
            throw _breakCause;
        }
        if (_savePreviousFailureEx != null) {
            throw _savePreviousFailureEx;
        }
        if (_alterSqlFailureEx != null) {
            throw _alterSqlFailureEx;
        }
        if (_replaceSchemaFailureEx != null) {
            throw _replaceSchemaFailureEx;
        }
        if (_diffFoundEx != null) {
            throw _diffFoundEx;
        }
    }

    public SQLFailureException getBreakCause() {
        return _breakCause;
    }

    public void setBreakCause(SQLFailureException breakCause) {
        this._breakCause = breakCause;
    }

    public DfAlterCheckSavePreviousFailureException getSavePreviousFailureEx() {
        return _savePreviousFailureEx;
    }

    public void setSavePreviousFailureEx(DfAlterCheckSavePreviousFailureException savePreviousFailureEx) {
        _savePreviousFailureEx = savePreviousFailureEx;
    }

    public DfAlterCheckAlterSqlFailureException getAlterSqlFailureEx() {
        return _alterSqlFailureEx;
    }

    public void setAlterSqlFailureEx(DfAlterCheckAlterSqlFailureException alterSqlFailureEx) {
        this._alterSqlFailureEx = alterSqlFailureEx;
    }

    public DfAlterCheckReplaceSchemaFailureException getReplaceSchemaFailureEx() {
        return _replaceSchemaFailureEx;
    }

    public void setReplaceSchemaFailureEx(DfAlterCheckReplaceSchemaFailureException replaceSchemaFailureEx) {
        this._replaceSchemaFailureEx = replaceSchemaFailureEx;
    }

    public DfAlterCheckDifferenceFoundException getDiffFoundEx() {
        return _diffFoundEx;
    }

    public void setDiffFoundEx(DfAlterCheckDifferenceFoundException diffFoundEx) {
        this._diffFoundEx = diffFoundEx;
    }
}
