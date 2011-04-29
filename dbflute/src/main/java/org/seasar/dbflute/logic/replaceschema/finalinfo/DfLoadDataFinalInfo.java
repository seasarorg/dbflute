package org.seasar.dbflute.logic.replaceschema.finalinfo;

/**
 * @author jflute
 */
public class DfLoadDataFinalInfo extends DfAbstractSchemaTaskFinalInfo {

    protected RuntimeException _loadEx;

    public RuntimeException getLoadEx() {
        return _loadEx;
    }

    public void setLoadEx(RuntimeException loadEx) {
        this._loadEx = loadEx;
    }
}
