package org.seasar.dbflute.properties.assistant.freegenerate;

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
    protected final List<Map<String, String>> _rowList;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfFreeGenTable(String tableName, List<Map<String, String>> rowList) {
        _tableName = tableName;
        _rowList = rowList;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "{tableName=" + _tableName + ", rowList.size()=" + _rowList + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<Map<String, String>> getRowList() {
        return _rowList;
    }
}
