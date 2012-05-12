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
    protected final String _tableName;
    protected final List<Map<String, String>> _columnList;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfFreeGenTable(String tableName, List<Map<String, String>> columnList) {
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
    public List<Map<String, String>> getColumnList() {
        return _columnList;
    }
}
