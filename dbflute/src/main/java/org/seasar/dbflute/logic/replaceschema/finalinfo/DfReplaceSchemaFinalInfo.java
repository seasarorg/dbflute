package org.seasar.dbflute.logic.replaceschema.finalinfo;

/**
 * @author jflute
 */
public class DfReplaceSchemaFinalInfo {

    protected DfCreateSchemaFinalInfo _createSchemaFinalInfo;
    protected DfLoadDataFinalInfo _loadDataFinalInfo;
    protected DfTakeFinallyFinalInfo _takeFinallyFinalInfo;
    protected DfAlterSchemaFinalInfo _alterSchemaFinalInfo;

    public DfReplaceSchemaFinalInfo(DfCreateSchemaFinalInfo createSchemaFinalInfo,
            DfLoadDataFinalInfo loadDataFinalInfo, DfTakeFinallyFinalInfo takeFinallyFinalInfo,
            DfAlterSchemaFinalInfo alterSchemaFinalInfo) {
        _createSchemaFinalInfo = createSchemaFinalInfo;
        _loadDataFinalInfo = loadDataFinalInfo;
        _takeFinallyFinalInfo = takeFinallyFinalInfo;
        _alterSchemaFinalInfo = alterSchemaFinalInfo;
    }

    public boolean hasFailure() {
        return isCreateSchemaFailure() || isLoadDataFailure() || isTakeFinallyFailure() || isAlterSchemaFailure();
    }

    public boolean isCreateSchemaFailure() {
        if (_createSchemaFinalInfo != null && _createSchemaFinalInfo.isFailure()) {
            return true;
        }
        return false;
    }

    public boolean isLoadDataFailure() {
        if (_loadDataFinalInfo != null && _loadDataFinalInfo.isFailure()) {
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

    public boolean isAlterSchemaFailure() {
        if (_alterSchemaFinalInfo != null && _alterSchemaFinalInfo.isFailure()) {
            return true;
        }
        return false;
    }

    public DfCreateSchemaFinalInfo getCreateSchemaFinalInfo() {
        return _createSchemaFinalInfo;
    }

    public DfLoadDataFinalInfo getLoadDataFinalInfo() {
        return _loadDataFinalInfo;
    }

    public DfTakeFinallyFinalInfo getTakeFinallyFinalInfo() {
        return _takeFinallyFinalInfo;
    }

    public DfAlterSchemaFinalInfo getAlterSchemaFinalInfo() {
        return _alterSchemaFinalInfo;
    }
}
