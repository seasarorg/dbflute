package org.seasar.dbflute.cbean.cipher;

import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.dbmeta.info.ColumnInfo;

/**
 * @author jflute
 * @since 0.9.8.4 (2011/05/21 Saturday)
 */
public class ColumnFunctionCipher {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final ColumnInfo _columnInfo;
    protected final List<CipherFunctionFilter> _functionFilterList = new ArrayList<CipherFunctionFilter>(1);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ColumnFunctionCipher(ColumnInfo columnInfo) {
        _columnInfo = columnInfo;
    }

    // ===================================================================================
    //                                                                              Cipher
    //                                                                              ======
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

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public ColumnInfo getColumnInfo() {
        return _columnInfo;
    }

    public void addFunctionFilter(CipherFunctionFilter filter) {
        _functionFilterList.add(filter);
    }
}
