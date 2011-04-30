package org.seasar.dbflute.logic.replaceschema.finalinfo;

import org.seasar.dbflute.exception.DfAlterCheckAlterSqlFailureException;
import org.seasar.dbflute.exception.DfAlterCheckDifferenceFoundException;
import org.seasar.dbflute.exception.DfAlterCheckReplaceSchemaFailureException;

/**
 * @author jflute
 */
public class DfAlterSchemaFinalInfo extends DfAbstractSchemaTaskFinalInfo {

    // one exists, the others always does not exist
    protected DfAlterCheckAlterSqlFailureException _alterSqlFailureEx;
    protected DfAlterCheckReplaceSchemaFailureException _replaceSchemaFailureEx;
    protected DfAlterCheckDifferenceFoundException _diffFoundEx;

    public void throwAlterCheckExceptionIfExists() {
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
