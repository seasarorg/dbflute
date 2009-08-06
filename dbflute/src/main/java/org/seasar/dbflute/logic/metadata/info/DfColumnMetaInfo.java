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
package org.seasar.dbflute.logic.metadata.info;

import java.util.Map;

import org.seasar.dbflute.logic.metadata.comment.DfDbCommentExtractor.UserColComments;

/**
 * @author jflute
 */
public class DfColumnMetaInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String columnName;
    protected int jdbcDefValue;
    protected String dbTypeName;
    protected int columnSize;
    protected int decimalDigits;
    protected boolean required;
    protected String columnComment;
    protected String defaultValue;
    protected String sql2entityTableName;
    protected String sql2entityJavaNative;

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasColumnComment() {
        return columnComment != null && columnComment.trim().length() > 0;
    }

    // ===================================================================================
    //                                                                              Accept
    //                                                                              ======
    public void acceptColumnComment(Map<String, UserColComments> columnCommentMap) {
        if (columnCommentMap == null) {
            return;
        }
        final UserColComments userColComments = columnCommentMap.get(columnName);
        if (userColComments == null) {
            return;
        }
        final String comment = userColComments.getComments();
        if (comment != null && comment.trim().length() > 0) {
            columnComment = comment;
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "{" + columnName + ", " + dbTypeName + "(" + columnSize + "," + decimalDigits + "), " + jdbcDefValue + ", "
                + required + ", " + columnComment + ", " + defaultValue + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    public int getDecimalDigits() {
        return decimalDigits;
    }

    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public int getJdbcDefValue() {
        return jdbcDefValue;
    }

    public void setJdbcDefValue(int jdbcDefValue) {
        this.jdbcDefValue = jdbcDefValue;
    }

    public String getDbTypeName() {
        return dbTypeName;
    }

    public void setDbTypeName(String dbTypeName) {
        this.dbTypeName = dbTypeName;
    }

    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }

    public String getSql2EntityTableName() {
        return sql2entityTableName;
    }

    public void setSql2EntityTableName(String sql2entityTableName) {
        this.sql2entityTableName = sql2entityTableName;
    }

    public String getSql2EntityJavaNative() {
        return sql2entityJavaNative;
    }

    public void setSql2EntityJavaNative(String sql2entityJavaNative) {
        this.sql2entityJavaNative = sql2entityJavaNative;
    }
}
