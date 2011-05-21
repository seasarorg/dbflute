package org.seasar.dbflute.cbean.cipher;

import java.util.Map;

import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.helper.StringKeyMap;

/**
 * @author jflute
 * @since 0.9.8.4 (2011/05/21 Saturday)
 */
public class GearedCipherManager {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Map<String, Map<String, ColumnFunctionCipher>> _columnFunctionMap = StringKeyMap.createAsFlexible();

    // ===================================================================================
    //                                                                             Prepare
    //                                                                             =======
    public void addFunctionFilter(ColumnInfo columnInfo, CipherFunctionFilter filter) {
        final String tableDbName = columnInfo.getDBMeta().getTableDbName();
        final String columnDbName = columnInfo.getColumnDbName();
        Map<String, ColumnFunctionCipher> elementMap = _columnFunctionMap.get(tableDbName);
        if (elementMap == null) {
            elementMap = StringKeyMap.createAsFlexible();
            _columnFunctionMap.put(tableDbName, elementMap);
        }
        ColumnFunctionCipher function = elementMap.get(columnDbName);
        if (function == null) {
            function = new ColumnFunctionCipher(columnInfo);
            elementMap.put(columnDbName, function);
        }
        function.addFunctionFilter(filter);
    }

    // ===================================================================================
    //                                                                              Cipher
    //                                                                              ======
    public ColumnFunctionCipher findColumnFunctionCipher(ColumnInfo columnInfo) {
        return findColumnFunctionCipher(columnInfo.getDBMeta().getTableDbName(), columnInfo.getColumnDbName());
    }

    public ColumnFunctionCipher findColumnFunctionCipher(String tableDbName, String columnDbName) {
        final Map<String, ColumnFunctionCipher> elementMap = _columnFunctionMap.get(tableDbName);
        if (elementMap == null) {
            return null;
        }
        return elementMap.get(columnDbName);
    }
}
