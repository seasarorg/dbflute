package org.seasar.dbflute.logic.replaceschema.finalinfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.exception.DfAlterCheckAlterSqlFailureException;
import org.seasar.dbflute.exception.DfAlterCheckDifferenceFoundException;
import org.seasar.dbflute.exception.DfAlterCheckReplaceSchemaFailureException;
import org.seasar.dbflute.exception.DfAlterCheckSavePreviousFailureException;
import org.seasar.dbflute.exception.DfTakeFinallyAssertionFailureException;
import org.seasar.dbflute.exception.SQLFailureException;

/**
 * @author jflute
 */
public class DfAlterCheckFinalInfo extends DfAbstractSchemaTaskFinalInfo {

    // one exists, the others always does not exist
    protected final List<File> _alterSqlFileList = new ArrayList<File>();
    protected final List<File> _submittedDraftFileList = new ArrayList<File>();
    protected SQLFailureException _breakCause;
    protected DfAlterCheckSavePreviousFailureException _savePreviousFailureEx;
    protected DfAlterCheckAlterSqlFailureException _alterSqlFailureEx;
    protected DfTakeFinallyAssertionFailureException _takeFinallyAssertionEx;
    protected DfAlterCheckReplaceSchemaFailureException _replaceSchemaFailureEx;
    protected DfAlterCheckDifferenceFoundException _diffFoundEx;

    public boolean hasAlterCheckDiff() {
        return _diffFoundEx != null;
    }

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
        if (_takeFinallyAssertionEx != null) {
            throw _takeFinallyAssertionEx;
        }
        if (_replaceSchemaFailureEx != null) {
            throw _replaceSchemaFailureEx;
        }
        if (_diffFoundEx != null) {
            throw _diffFoundEx;
        }
    }

    public List<File> getAlterSqlFileList() {
        return _alterSqlFileList;
    }

    public void addAlterSqlFileAll(List<File> alterSqlFileList) {
        this._alterSqlFileList.addAll(alterSqlFileList);
    }

    public List<File> getSubmittedDraftFileList() {
        return _submittedDraftFileList;
    }

    public void addSubmittedDraftFileAll(List<File> submittedDraftFileList) {
        this._submittedDraftFileList.addAll(submittedDraftFileList);
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

    public DfTakeFinallyAssertionFailureException getTakeFinallyAssertionEx() {
        return _takeFinallyAssertionEx;
    }

    public void setTakeFinallyAssertionEx(DfTakeFinallyAssertionFailureException takeFinallyAssertionEx) {
        this._takeFinallyAssertionEx = takeFinallyAssertionEx;
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
