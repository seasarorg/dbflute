package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.dbmeta.info.ColumnInfo;

/**
 * @author jflute
 */
public class HpSpecifiedInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _tableAliasName;

    protected ColumnInfo _specifiedColumn;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public HpSpecifiedInfo(String tableAliasName, ColumnInfo specifiedColumn) {
        _tableAliasName = tableAliasName;
        _specifiedColumn = specifiedColumn;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getTableAliasName() {
        return _tableAliasName;
    }

    public ColumnInfo getSpecifiedColumn() {
        return _specifiedColumn;
    }
}
