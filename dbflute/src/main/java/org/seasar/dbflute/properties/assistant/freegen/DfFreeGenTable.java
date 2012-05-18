package org.seasar.dbflute.properties.assistant.freegen;

import java.util.List;
import java.util.Map;

/**
 * @author jflute
 */
public class DfFreeGenTable {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Map<String, Object> _tableMap;
    protected final String _tableName;
    protected final List<Map<String, Object>> _columnList;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfFreeGenTable(Map<String, Object> tableMap, String tableName, List<Map<String, Object>> columnList) {
        _tableMap = tableMap;
        _tableName = tableName;
        _columnList = columnList;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "{tableName=" + _tableName + ", rowList.size()=" + _columnList + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Map<String, Object> getTableMap() {
        return _tableMap;
    }

    public String getTableName() {
        return _tableName;
    }

    public List<Map<String, Object>> getColumnList() {
        return _columnList;
    }
}
