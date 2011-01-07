package org.seasar.dbflute.logic.replaceschema.finalinfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jflute
 */
public class DfAbstractSchemaTaskFinalInfo {

    protected String _resultMessage;
    protected final List<String> _detailMessageList = new ArrayList<String>();
    protected boolean _failure;

    public String getResultMessage() {
        return _resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this._resultMessage = resultMessage;
    }

    public List<String> getDetailMessageList() {
        return _detailMessageList;
    }

    public void addDetailMessage(String detailMessage) {
        this._detailMessageList.add(detailMessage);
    }

    public boolean isFailure() {
        return _failure;
    }

    public void setFailure(boolean failure) {
        this._failure = failure;
    }
}
