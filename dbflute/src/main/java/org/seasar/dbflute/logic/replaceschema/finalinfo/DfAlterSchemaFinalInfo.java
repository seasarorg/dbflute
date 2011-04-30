package org.seasar.dbflute.logic.replaceschema.finalinfo;

import org.seasar.dbflute.exception.DfAlterCheckAlterSqlFailureException;
import org.seasar.dbflute.exception.DfAlterCheckDifferenceFoundException;

/**
 * @author jflute
 */
public class DfAlterSchemaFinalInfo extends DfAbstractSchemaTaskFinalInfo {

    // one exists, the other always does not exist
    protected DfAlterCheckAlterSqlFailureException _alterSqlFailureEx;
    protected DfAlterCheckDifferenceFoundException _diffFoundEx;

    public void throwAlterCheckExceptionIfExists() {
        if (_alterSqlFailureEx != null) {
            throw _alterSqlFailureEx;
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

    public DfAlterCheckDifferenceFoundException getDiffFoundEx() {
        return _diffFoundEx;
    }

    public void setDiffFoundEx(DfAlterCheckDifferenceFoundException diffFoundEx) {
        this._diffFoundEx = diffFoundEx;
    }
}
