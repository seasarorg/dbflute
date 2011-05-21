package org.seasar.dbflute.cbean.cipher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.helper.StringKeyMap;

/**
 * @author jflute
 * @since 0.9.8.4 (2011/05/21 Saturday)
 */
public class GearedCipher {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Map<String, Map<String, CipherColumnFunction>> _columnFunctionMap = StringKeyMap.createAsFlexible();

    // ===================================================================================
    //                                                                             Prepare
    //                                                                             =======
    public void addFunctionFilter(ColumnInfo columnInfo, CipherFunctionFilter filter) {
        final String tableDbName = columnInfo.getDBMeta().getTableDbName();
        final String columnDbName = columnInfo.getColumnDbName();
        Map<String, CipherColumnFunction> elementMap = _columnFunctionMap.get(tableDbName);
        if (elementMap == null) {
            elementMap = StringKeyMap.createAsFlexible();
            _columnFunctionMap.put(tableDbName, elementMap);
        }
        CipherColumnFunction function = elementMap.get(columnDbName);
        if (function == null) {
            function = new CipherColumnFunction(columnInfo);
            elementMap.put(columnDbName, function);
        }
        function.addFunctionFilter(filter);
    }

    // ===================================================================================
    //                                                                              Cipher
    //                                                                              ======
    public String encryptByFunction(String tableDbName, String columnDbName, String valueExp) {
        final CipherColumnFunction function = findColumnFunction(tableDbName, columnDbName);
        return function != null ? function.encrypt(valueExp) : valueExp;
    }

    public String decryptByFunction(String tableDbName, String columnDbName, String valueExp) {
        final CipherColumnFunction function = findColumnFunction(tableDbName, columnDbName);
        return function != null ? function.decrypt(valueExp) : valueExp;
    }

    protected CipherColumnFunction findColumnFunction(ColumnInfo columnInfo) {
        return findColumnFunction(columnInfo.getDBMeta().getTableDbName(), columnInfo.getColumnDbName());
    }

    protected CipherColumnFunction findColumnFunction(String tableDbName, String columnDbName) {
        final Map<String, CipherColumnFunction> elementMap = _columnFunctionMap.get(tableDbName);
        if (elementMap == null) {
            return null;
        }
        return elementMap.get(columnDbName);
    }

    // ===================================================================================
    //                                                                      Function Class
    //                                                                      ==============
    protected static class CipherColumnFunction {
        protected final ColumnInfo _columnInfo;
        protected final List<CipherFunctionFilter> _functionFilterList = new ArrayList<CipherFunctionFilter>();;

        public CipherColumnFunction(ColumnInfo columnInfo) {
            _columnInfo = columnInfo;
        }

        public String encrypt(String valueExp) {
            for (CipherFunctionFilter filter : _functionFilterList) {
                valueExp = filter.encrypt(valueExp);
            }
            return valueExp;
        }

        public String decrypt(String valueExp) {
            for (CipherFunctionFilter filter : _functionFilterList) {
                valueExp = filter.decrypt(valueExp);
            }
            return valueExp;
        }

        public ColumnInfo getColumnInfo() {
            return _columnInfo;
        }

        public void addFunctionFilter(CipherFunctionFilter filter) {
            _functionFilterList.add(filter);
        }
    }
}
