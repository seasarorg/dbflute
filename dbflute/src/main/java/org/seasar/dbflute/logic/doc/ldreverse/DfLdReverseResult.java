package org.seasar.dbflute.logic.doc.ldreverse;

import java.util.List;
import java.util.Map;

import org.apache.torque.engine.database.model.Column;

/**
 * @author jflute
 */
public class DfLdReverseResult {

    protected Map<String, List<Column>> _overTableColumnMap;
    protected Map<String, List<Map<String, String>>> _overTemplateDataMap;

    public Map<String, List<Column>> getOverTableColumnMap() {
        return _overTableColumnMap;
    }

    public void setOverTableColumnMap(Map<String, List<Column>> overTableColumnMap) {
        this._overTableColumnMap = overTableColumnMap;
    }

    public Map<String, List<Map<String, String>>> getOverTemplateDataMap() {
        return _overTemplateDataMap;
    }

    public void setOverTemplateDataMap(Map<String, List<Map<String, String>>> overTemplateDataMap) {
        this._overTemplateDataMap = overTemplateDataMap;
    }
}
