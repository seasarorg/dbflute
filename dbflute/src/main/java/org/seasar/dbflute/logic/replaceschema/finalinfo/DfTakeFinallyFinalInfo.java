package org.seasar.dbflute.logic.replaceschema.finalinfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.exception.DfTakeFinallyAssertionFailureException;
import org.seasar.dbflute.exception.SQLFailureException;

/**
 * @author jflute
 */
public class DfTakeFinallyFinalInfo extends DfAbstractSchemaTaskFinalInfo {

    protected final List<File> _takeFinallySqlFileList = new ArrayList<File>();
    protected SQLFailureException _breakCause;
    protected DfTakeFinallyAssertionFailureException _assertionEx;

    public List<File> getTakeFinallySqlFileList() {
        return _takeFinallySqlFileList;
    }

    public void addTakeFinallySqlFileAll(List<File> takeFinallySqlFileList) {
        this._takeFinallySqlFileList.addAll(takeFinallySqlFileList);
    }

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
