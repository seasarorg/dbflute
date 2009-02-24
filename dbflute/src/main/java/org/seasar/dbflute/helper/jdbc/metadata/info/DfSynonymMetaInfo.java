/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.helper.jdbc.metadata.info;

import java.util.List;
import java.util.Map;

/**
 * @author jflute
 */
public class DfSynonymMetaInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String synonymName;
    protected String tableOwner;
    protected String tableName;
    protected List<String> primaryKeyNameList;
    protected boolean autoIncrement;
    protected Map<String, Map<Integer, String>> uniqueKeyMap;
    protected Map<String, DfForeignKeyMetaInfo> foreignKeyMetaInfoMap;
    protected Map<String, Map<Integer, String>> indexMap;

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "{" + synonymName + ": " + tableOwner + "." + tableName + ", " + primaryKeyNameList
                + (autoIncrement ? ", ID" : "") + ", " + (uniqueKeyMap != null ? "UQ=" + uniqueKeyMap.size() : null)
                + ", " + (foreignKeyMetaInfoMap != null ? "FK=" + foreignKeyMetaInfoMap.size() : null);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getSynonymName() {
        return synonymName;
    }

    public void setSynonymName(String synonymName) {
        this.synonymName = synonymName;
    }

    public String getTableOwner() {
        return tableOwner;
    }

    public void setTableOwner(String tableOwner) {
        this.tableOwner = tableOwner;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getPrimaryKeyNameList() {
        return primaryKeyNameList;
    }

    public void setPrimaryKeyNameList(List<String> primaryKeyNameList) {
        this.primaryKeyNameList = primaryKeyNameList;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public Map<String, Map<Integer, String>> getUniqueKeyMap() {
        return uniqueKeyMap;
    }

    public void setUniqueKeyMap(Map<String, Map<Integer, String>> uniqueKeyMap) {
        this.uniqueKeyMap = uniqueKeyMap;
    }

    public Map<String, DfForeignKeyMetaInfo> getForeignKeyMetaInfoMap() {
        return foreignKeyMetaInfoMap;
    }

    public void setForeignKeyMetaInfoMap(Map<String, DfForeignKeyMetaInfo> foreignKeyMetaInfoMap) {
        this.foreignKeyMetaInfoMap = foreignKeyMetaInfoMap;
    }

    public Map<String, Map<Integer, String>> getIndexMap() {
        return indexMap;
    }

    public void setIndexMap(Map<String, Map<Integer, String>> indexMap) {
        this.indexMap = indexMap;
    }
}
