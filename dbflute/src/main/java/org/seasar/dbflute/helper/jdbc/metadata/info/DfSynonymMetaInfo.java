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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.helper.jdbc.metadata.comment.DfDbCommentExtractor.UserColComments;
import org.seasar.dbflute.util.DfSystemUtil;

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
    protected List<DfColumnMetaInfo> columnMetaInfoList;
    protected List<String> primaryKeyNameList;
    protected boolean autoIncrement;
    protected Map<String, Map<Integer, String>> uniqueKeyMap;
    protected Map<String, DfForeignKeyMetaInfo> foreignKeyMetaInfoMap;
    protected Map<String, Map<Integer, String>> indexMap;
    protected String dbLinkName;
    protected boolean selectable;
    protected boolean procedureSynonym;
    protected boolean sequenceSynonym;
    protected String tableComment;
    protected Map<String, UserColComments> columnCommentMap;

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean isDBLink() {
        return dbLinkName != null;
    }

    public boolean hasTableComment() {
        return tableComment != null && tableComment.trim().length() > 0;
    }

    public boolean hasColumnCommentMap() {
        return columnCommentMap != null && !columnCommentMap.isEmpty();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        String comment = "";
        if (tableComment != null) {
            final String ln = DfSystemUtil.getLineSeparator();
            final int indexOf = tableComment.indexOf(ln);
            if (indexOf > 0) { // not contain 0 because ignore first line separator
                comment = tableComment.substring(0, indexOf) + "...";
            } else {
                comment = tableComment;
            }
        }
        return synonymName + ":{" + (dbLinkName != null ? dbLinkName : tableOwner) + "." + tableName
                + (columnMetaInfoList != null ? "(" + columnMetaInfoList.size() + " columns)" : "") + ", PK="
                + primaryKeyNameList + (autoIncrement ? ", ID" : "") + ", "
                + (uniqueKeyMap != null ? "UQ=" + uniqueKeyMap.size() : null) + ", "
                + (foreignKeyMetaInfoMap != null ? "FK=" + foreignKeyMetaInfoMap.size() : null) + ", selectable="
                + selectable + "} // " + comment;
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

    public List<DfColumnMetaInfo> getColumnMetaInfoList() {
        return columnMetaInfoList != null ? columnMetaInfoList : new ArrayList<DfColumnMetaInfo>();
    }

    public void setColumnMetaInfoList(List<DfColumnMetaInfo> columnMetaInfoList) {
        this.columnMetaInfoList = columnMetaInfoList;
    }

    public List<String> getPrimaryKeyNameList() {
        return primaryKeyNameList != null ? primaryKeyNameList : new ArrayList<String>();
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
        return uniqueKeyMap != null ? uniqueKeyMap : new HashMap<String, Map<Integer, String>>();
    }

    public void setUniqueKeyMap(Map<String, Map<Integer, String>> uniqueKeyMap) {
        this.uniqueKeyMap = uniqueKeyMap;
    }

    public Map<String, DfForeignKeyMetaInfo> getForeignKeyMetaInfoMap() {
        return foreignKeyMetaInfoMap != null ? foreignKeyMetaInfoMap : new HashMap<String, DfForeignKeyMetaInfo>();
    }

    public void setForeignKeyMetaInfoMap(Map<String, DfForeignKeyMetaInfo> foreignKeyMetaInfoMap) {
        this.foreignKeyMetaInfoMap = foreignKeyMetaInfoMap;
    }

    public Map<String, Map<Integer, String>> getIndexMap() {
        return indexMap != null ? indexMap : new HashMap<String, Map<Integer, String>>();
    }

    public void setIndexMap(Map<String, Map<Integer, String>> indexMap) {
        this.indexMap = indexMap;
    }

    public String getDbLinkName() {
        return dbLinkName;
    }

    public void setDbLinkName(String dbLinkName) {
        this.dbLinkName = dbLinkName;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public Map<String, UserColComments> getColumnCommentMap() {
        return columnCommentMap;
    }

    public void setColumnCommentMap(Map<String, UserColComments> columnCommentMap) {
        this.columnCommentMap = columnCommentMap;
    }
}
