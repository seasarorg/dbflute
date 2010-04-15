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
package org.seasar.dbflute.logic.jdbc.handler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.jdbc.determiner.DfJdbcDeterminer;
import org.seasar.dbflute.logic.factory.DfJdbcDeterminerFactory;
import org.seasar.dbflute.logic.jdbc.metadata.DfAbstractMetaDataExtractor;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.assistant.DfAdditionalSchemaInfo;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfNameHintUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfAbstractMetaDataHandler extends DfAbstractMetaDataExtractor {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final List<String> EMPTY_STRING_LIST = DfCollectionUtil.emptyList();

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The list for except table. (Lazy) */
    private List<String> _tableExceptList;

    /** The list for target table. (Lazy) */
    private List<String> _tableTargetList;

    /** The map for except column. (Lazy) */
    private Map<String, List<String>> _columnExceptMap;

    /** The map of additional schema. (Lazy) */
    private Map<String, DfAdditionalSchemaInfo> _additionalSchemaMap;

    protected final List<String> getTableExceptList() { // for main schema
        if (_tableExceptList == null) {
            _tableExceptList = getProperties().getDatabaseProperties().getTableExceptList();
        }
        return _tableExceptList;
    }

    protected final List<String> getTableTargetList() { // for main schema
        if (_tableTargetList == null) {
            _tableTargetList = getProperties().getDatabaseProperties().getTableTargetList();
        }
        return _tableTargetList;
    }

    protected final Map<String, List<String>> getColumnExceptMap() { // for main schema
        if (_columnExceptMap == null) {
            _columnExceptMap = getProperties().getDatabaseProperties().getColumnExceptMap();
        }
        return _columnExceptMap;
    }

    protected final Map<String, DfAdditionalSchemaInfo> getAdditionalSchemaMap() { // for additional schema
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
     * @param schemaName The name of schema. (Nullable)
     * @param tableName The name of table. (NotNull)
     * @return Determination.
     */
    public boolean isTableExcept(String schemaName, final String tableName) {
        if (tableName == null) {
            throw new IllegalArgumentException("The argument 'tableName' should not be null.");
        }
        final List<String> tableTargetList = getRealTableTargetList(schemaName);
        final List<String> tableExceptList = getRealTableExceptList(schemaName);
        return !isTargetByHint(tableName, tableTargetList, tableExceptList);
    }

    protected List<String> getRealTableExceptList(String schemaName) { // extension point
        if (schemaName != null) {
            final Map<String, DfAdditionalSchemaInfo> additionalSchemaMap = getAdditionalSchemaMap();
            final DfAdditionalSchemaInfo schemaInfo = additionalSchemaMap.get(schemaName);
            if (schemaInfo != null) {
                return schemaInfo.getTableExceptList();
            }
        }
        return getTableExceptList();
    }

    protected List<String> getRealTableTargetList(String schemaName) { // extension point
        if (schemaName != null) {
            final Map<String, DfAdditionalSchemaInfo> additionalSchemaMap = getAdditionalSchemaMap();
            final DfAdditionalSchemaInfo schemaInfo = additionalSchemaMap.get(schemaName);
            if (schemaInfo != null) {
                return schemaInfo.getTableTargetList();
            }
        }
        return getTableTargetList();
    }

    /**
     * Is the column of the table out of sight?
     * @param schemaName The name of schema. (Nullable)
     * @param tableName The name of table. (NotNull)
     * @param columnName The name of column. (NotNull)
     * @return Determination.
     */
    public boolean isColumnExcept(String schemaName, String tableName, String columnName) {
        if (tableName == null) {
            throw new IllegalArgumentException("The argument 'tableName' should not be null.");
        }
        if (columnName == null) {
            throw new IllegalArgumentException("The argument 'columnName' should not be null.");
        }
        final Map<String, List<String>> columnExceptMap = getRealColumnExceptMap(schemaName);
        final List<String> columnExceptList = columnExceptMap.get(tableName);
        if (columnExceptList == null) { // no definition about the table
            return false;
        }
        return !isTargetByHint(columnName, EMPTY_STRING_LIST, columnExceptList);
    }

    protected Map<String, List<String>> getRealColumnExceptMap(String schemaName) { // extension point
        if (schemaName != null) {
            final Map<String, DfAdditionalSchemaInfo> additionalSchemaMap = getAdditionalSchemaMap();
            final DfAdditionalSchemaInfo schemaInfo = additionalSchemaMap.get(schemaName);
            if (schemaInfo != null) {
                return new HashMap<String, List<String>>(); // unsupported at additional schema
            }
        }
        return getColumnExceptMap();
    }

    protected boolean isTargetByHint(final String name, final List<String> targetList, final List<String> exceptList) {
        return DfNameHintUtil.isTargetByHint(name, targetList, exceptList);
    }

    // ===================================================================================
    //                                                        Database Dependency Resolver
    //                                                        ============================
    protected String filterSchemaName(String schemaName) {
        // The driver throws the exception if the value is empty string.
        if (Srl.is_NotNull_and_NotTrimmedEmpty(schemaName) && !isSchemaNameEmptyAllowed()) {
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

    protected DfDatabaseProperties getDatabaseProperties() {
        return DfBuildProperties.getInstance().getDatabaseProperties();
    }

    protected boolean isMySQL() {
        return getBasicProperties().isDatabaseMySQL();
    }

    protected boolean isPostgreSQL() {
        return getBasicProperties().isDatabasePostgreSQL();
    }

    protected boolean isOracle() {
        return getBasicProperties().isDatabaseOracle();
    }

    protected boolean isDB2() {
        return getBasicProperties().isDatabaseDB2();
    }

    protected boolean isSQLServer() {
        return getBasicProperties().isDatabaseSQLServer();
    }

    protected boolean isSQLite() {
        return getBasicProperties().isDatabaseSQLite();
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

    protected <KEY, VALUE> LinkedHashMap<KEY, VALUE> newLinkedHashMap() {
        return new LinkedHashMap<KEY, VALUE>();
    }
}