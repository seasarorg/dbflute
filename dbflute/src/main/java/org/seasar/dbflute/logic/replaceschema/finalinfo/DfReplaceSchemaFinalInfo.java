package org.seasar.dbflute.logic.replaceschema.finalinfo;

/**
 * @author jflute
 */
public class DfReplaceSchemaFinalInfo {

    protected DfCreateSchemaFinalInfo _createSchemaFinalInfo;
    protected DfTakeFinallyFinalInfo _takeFinallyFinalInfo;

    public DfReplaceSchemaFinalInfo(DfCreateSchemaFinalInfo createSchemaFinalInfo,
            DfTakeFinallyFinalInfo takeFinallyFinalInfo) {
        _createSchemaFinalInfo = createSchemaFinalInfo;
        _takeFinallyFinalInfo = takeFinallyFinalInfo;
    }

    public boolean hasFailure() {
        return isCreateSchemaFailure() || isTakeFinallyFailure();
    }

    public boolean isCreateSchemaFailure() {
        if (_createSchemaFinalInfo != null && _createSchemaFinalInfo.isFailure()) {
            return true;
        }
        return false;
    }

    public boolean isTakeFinallyFailure() {
        if (_takeFinallyFinalInfo != null && _takeFinallyFinalInfo.isFailure()) {
            return true;
        }
        return false;
    }

    public DfCreateSchemaFinalInfo getCreateSchemaFinalInfo() {
        return _createSchemaFinalInfo;
    }

    public DfTakeFinallyFinalInfo getTakeFinallyFinalInfo() {
        return _takeFinallyFinalInfo;
    }
}
