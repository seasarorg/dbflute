/*
 * Copyright 2004-2012 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.jdbc.metadata.info;

import java.util.Map;

import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 */
public class DfForeignKeyMeta {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _foreignKeyName;

    protected String _localTableName;

    protected String _foreignTableName;

    protected Map<String, String> _columnNameMap = DfCollectionUtil.newLinkedHashMap();

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return _foreignKeyName + "-{" + _localTableName + ":" + _foreignTableName + "--" + _columnNameMap + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getForeignKeyName() {
        return _foreignKeyName;
    }

    public void setForeignKeyName(String foreignKeyName) {
        this._foreignKeyName = foreignKeyName;
    }

    public String getLocalTableName() {
        return _localTableName;
    }

    public void setLocalTableName(String localtableName) {
        this._localTableName = localtableName;
    }

    public String getForeignTableName() {
        return _foreignTableName;
    }

    public void setForeignTableName(String foreignTableName) {
        this._foreignTableName = foreignTableName;
    }

    public Map<String, String> getColumnNameMap() {
        return _columnNameMap;
    }

    public void setColumnNameMap(Map<String, String> columnNameMap) {
        if (columnNameMap == null) {
            throw new IllegalArgumentException("The argument 'columnNameMap' should not be null!");
        }
        this._columnNameMap = columnNameMap;
    }

    public void putColumnNameMap(String localColumnName, String foreignColumnName) {
        this._columnNameMap.put(localColumnName, foreignColumnName);
    }
}
