/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.dbflute.dbmeta.name.ColumnSqlName;
import org.seasar.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * @author jflute
 */
public class HpSpecifiedColumn {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _tableAliasName;
    protected final ColumnInfo _columnInfo; // required
    protected final ConditionBean _baseCB; // required
    protected final String _columnDirectName;
    protected final boolean _derived;
    protected HpSpecifiedColumn _mappedSpecifiedColumn;
    protected String _mappedDerivedAlias;
    protected String _onQueryName;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public HpSpecifiedColumn(String tableAliasName, ColumnInfo columnInfo, ConditionBean baseCB) {
        assertColumnInfo(tableAliasName, columnInfo);
        assertBaseCB(tableAliasName, baseCB);
        _tableAliasName = tableAliasName;
        _columnInfo = columnInfo;
        _baseCB = baseCB;
        _columnDirectName = null;
        _derived = false;
    }

    public HpSpecifiedColumn(String tableAliasName, ColumnInfo columnInfo, ConditionBean baseCB,
            String columnDirectName, boolean derived) {
        assertColumnInfo(tableAliasName, columnInfo);
        assertBaseCB(tableAliasName, baseCB);
        _tableAliasName = tableAliasName;
        _columnInfo = columnInfo;
        _baseCB = baseCB;
        _columnDirectName = columnDirectName;
        _derived = derived;
    }

    protected void assertColumnInfo(String tableAliasName, ColumnInfo columnInfo) {
        if (columnInfo == null) {
            String msg = "The argument 'columnInfo' should not be null: tableAliasName=" + tableAliasName;
            throw new IllegalArgumentException(msg);
        }
    }

    protected void assertBaseCB(String tableAliasName, ConditionBean baseCB) {
        if (baseCB == null) {
            String msg = "The argument 'baseCB' should not be null: tableAliasName=" + tableAliasName;
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                        Dream Cruise
    //                                                                        ============
    public boolean isDreamCruiseTicket() {
        return _baseCB.xisDreamCruiseShip();
    }

    public void setupSelectDreamCruiseJourneyLogBookIfUnionExists() {
        if (!isDreamCruiseTicket()) {
            String msg = "This method is only allowed at Dream Cruise.";
            throw new IllegalConditionBeanOperationException(msg);
        }
        _baseCB.xsetupSelectDreamCruiseJourneyLogBookIfUnionExists();
    }

    // ===================================================================================
    //                                                                         Column Name
    //                                                                         ===========
    public String getColumnDbName() {
        return _columnInfo.getColumnDbName();
    }

    public ColumnSqlName toColumnSqlName() {
        return _columnDirectName != null ? new ColumnSqlName(_columnDirectName) : _columnInfo.getColumnSqlName();
    }

    public ColumnRealName toColumnRealName() {
        return ColumnRealName.create(_tableAliasName, toColumnSqlName());
    }

    // ===================================================================================
    //                                                                             Mapping
    //                                                                             =======
    public void mappedFrom(HpSpecifiedColumn mappedSpecifiedInfo) {
        _mappedSpecifiedColumn = mappedSpecifiedInfo;
    }

    public void mappedFromDerived(String mappedDerivedAlias) {
        _mappedDerivedAlias = mappedDerivedAlias;
    }

    public String getValidMappedOnQueryName() {
        if (_mappedSpecifiedColumn != null) {
            return _mappedSpecifiedColumn.getOnQueryName();
        } else if (_mappedDerivedAlias != null) {
            return _mappedDerivedAlias;
        } else {
            return null;
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{").append(_tableAliasName).append(", ");
        if (_columnDirectName != null) {
            sb.append(_columnDirectName + ", ");
        }
        sb.append(_columnInfo).append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getTableAliasName() {
        return _tableAliasName;
    }

    public ColumnInfo getColumnInfo() {
        return _columnInfo;
    }

    public String getColumnDirectName() {
        return _columnDirectName;
    }

    public boolean isDerived() {
        return _derived;
    }

    public HpSpecifiedColumn getMappedSpecifiedInfo() {
        return _mappedSpecifiedColumn;
    }

    public String getMappedAliasName() {
        return _mappedDerivedAlias;
    }

    public String getOnQueryName() {
        return _onQueryName;
    }

    public void setOnQueryName(String onQueryName) {
        this._onQueryName = onQueryName;
    }
}
