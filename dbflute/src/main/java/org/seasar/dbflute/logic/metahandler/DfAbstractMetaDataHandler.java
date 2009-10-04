/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.logic.metahandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.jdbc.determiner.DfJdbcDeterminer;
import org.seasar.dbflute.logic.factory.DfJdbcDeterminerFactory;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.assistant.DfAdditionalSchemaInfo;
import org.seasar.dbflute.util.DfNameHintUtil;

/**
 * @author jflute
 */
public class DfAbstractMetaDataHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The list for except table. (Lazy) */
    private List<String> _tableExceptList;

    /** The list for target table. (Lazy) */
    private List<String> _tableTargetList;

    /** The simple list for except column. (Lazy) */
    private List<String> _simpleColumnExceptList;

    /** The map of additional schema. (Lazy) */
    private Map<String, DfAdditionalSchemaInfo> _additionalSchemaMap;

    protected List<String> getTableExceptList() { // for main schema
        if (_tableExceptList == null) {
            _tableExceptList = getProperties().getDatabaseProperties().getTableExceptList();
        }
        return _tableExceptList;
    }

    protected List<String> getTableTargetList() { // for main schema
        if (_tableTargetList == null) {
            _tableTargetList = getProperties().getDatabaseProperties().getTableTargetList();
        }
        return _tableTargetList;
    }

    protected List<String> getSimpleColumnExceptList() { // for main schema
        if (_simpleColumnExceptList == null) {
            _simpleColumnExceptList = getProperties().getDatabaseProperties().getSimpleColumnExceptList();
        }
        return _simpleColumnExceptList;
    }

    protected Map<String, DfAdditionalSchemaInfo> getAdditionalSchemaMap() { // for additional schema
        if (_additionalSchemaMap == null) {
            _additionalSchemaMap = getProperties().getDatabaseProperties().getAdditionalSchemaMap();
        }
        return _additionalSchemaMap;
    }

    // ===================================================================================
    //                                                                Except Determination
    //                                                                ====================
    /**
     * Is the table name out of sight?
     * @param schemaName The name of schema. (NotNull)
     * @param tableName The name of table. (NotNull)
     * @return Determination.
     */
    public boolean isTableExcept(String schemaName, final String tableName) {
        if (tableName == null) {
            throw new IllegalArgumentException("The argument 'tableName' is required.");
        }
        if (schemaName != null) {
            final Map<String, DfAdditionalSchemaInfo> additionalSchemaMap = getAdditionalSchemaMap();
            final DfAdditionalSchemaInfo schemaInfo = additionalSchemaMap.get(schemaName);
            if (schemaInfo != null) {
                final List<String> exceptList = schemaInfo.getTableExceptList();
                final List<String> targetList = schemaInfo.getTableTargetList();
                return !isTargetByHint(tableName, targetList, exceptList);
            }
        }
        {
            // for main schema
            final List<String> targetList = getTableTargetList();
            final List<String> exceptList = getTableExceptList();
            return !isTargetByHint(tableName, targetList, exceptList);
        }
    }

    /**
     * Is the column name out of sight?
     * @param columnName Column name. (NotNull)
     * @return Determination.
     */
    public boolean isColumnExcept(final String columnName) {
        if (columnName == null) {
            throw new NullPointerException("Argument[columnName] is required.");
        }

        final List<String> columnExceptSimpleList = getSimpleColumnExceptList();
        return !isTargetByHint(columnName, new ArrayList<String>(), columnExceptSimpleList);
    }

    protected boolean isTargetByHint(final String name, final List<String> targetList, final List<String> exceptList) {
        return DfNameHintUtil.isTargetByHint(name, targetList, exceptList);
    }

    // ===================================================================================
    //                                                        Database Dependency Resolver
    //                                                        ============================
    protected String filterSchemaName(String schemaName) {
        // The driver throws the exception if the value is empty string.
        if (schemaName != null && schemaName.trim().length() == 0 && !isSchemaNameEmptyAllowed()) {
            return null;
        }
        return schemaName;
    }

    protected boolean isSchemaNameEmptyAllowed() {
        return createJdbcDeterminer().isSchemaNameEmptyAllowed();
    }

    protected boolean isPrimaryKeyExtractingSupported() {
        return createJdbcDeterminer().isPrimaryKeyExtractingSupported();
    }

    protected boolean isForeignKeyExtractingSupported() {
        return createJdbcDeterminer().isForeignKeyExtractingSupported();
    }

    protected DfJdbcDeterminer createJdbcDeterminer() {
        return new DfJdbcDeterminerFactory(getBasicProperties()).createJdbcDeterminer();
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfBasicProperties getBasicProperties() {
        return DfBuildProperties.getInstance().getBasicProperties();
    }

    protected boolean isOracle() {
        return getBasicProperties().isDatabaseOracle();
    }

    protected boolean isPostgreSQL() {
        return getBasicProperties().isDatabasePostgreSQL();
    }

    protected boolean isMsAccess() {
        return getBasicProperties().isDatabaseMsAccess();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return "\n";
    }
}