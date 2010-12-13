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

import java.util.List;
import java.util.Map;

import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.logic.jdbc.metadata.DfAbstractMetaDataExtractor;
import org.seasar.dbflute.properties.assistant.DfAdditionalSchemaInfo;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfNameHintUtil;

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
    /** The list for except table on main schema. (Lazy) */
    private List<String> _tableExceptList;

    /** The list for target table on main schema. (Lazy) */
    private List<String> _tableTargetList;

    /** The map for except column on main schema. (Lazy) */
    private Map<String, List<String>> _columnExceptMap;

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

    // ===================================================================================
    //                                                                Except Determination
    //                                                                ====================
    /**
     * Is the table name out of sight?
     * @param unifiedSchema The unified schema that can contain catalog name and no-name schema. (Nullable)
     * @param tableName The name of table. (NotNull)
     * @return Determination.
     */
    public boolean isTableExcept(UnifiedSchema unifiedSchema, final String tableName) {
        if (tableName == null) {
            throw new IllegalArgumentException("The argument 'tableName' should not be null.");
        }
        final List<String> tableTargetList = getRealTableTargetList(unifiedSchema);
        final List<String> tableExceptList = getRealTableExceptList(unifiedSchema);
        return !isTargetByHint(tableName, tableTargetList, tableExceptList);
    }

    protected List<String> getRealTableExceptList(UnifiedSchema unifiedSchema) { // extension point
        if (unifiedSchema != null) {
            final DfAdditionalSchemaInfo schemaInfo = getAdditionalSchemaInfo(unifiedSchema);
            if (schemaInfo != null) {
                return schemaInfo.getTableExceptList();
            }
        }
        return getTableExceptList();
    }

    protected List<String> getRealTableTargetList(UnifiedSchema unifiedSchema) { // extension point
        if (unifiedSchema != null) {
            final DfAdditionalSchemaInfo schemaInfo = getAdditionalSchemaInfo(unifiedSchema);
            if (schemaInfo != null) {
                return schemaInfo.getTableTargetList();
            }
        }
        return getTableTargetList();
    }

    /**
     * Is the column of the table out of sight?
     * @param unifiedSchema The unified schema that can contain catalog name and no-name mark. (Nullable)
     * @param tableName The name of table. (NotNull)
     * @param columnName The name of column. (NotNull)
     * @return Determination.
     */
    public boolean isColumnExcept(UnifiedSchema unifiedSchema, String tableName, String columnName) {
        if (tableName == null) {
            throw new IllegalArgumentException("The argument 'tableName' should not be null.");
        }
        if (columnName == null) {
            throw new IllegalArgumentException("The argument 'columnName' should not be null.");
        }
        final Map<String, List<String>> columnExceptMap = getRealColumnExceptMap(unifiedSchema);
        final List<String> columnExceptList = columnExceptMap.get(tableName);
        if (columnExceptList == null) { // no definition about the table
            return false;
        }
        return !isTargetByHint(columnName, EMPTY_STRING_LIST, columnExceptList);
    }

    protected Map<String, List<String>> getRealColumnExceptMap(UnifiedSchema unifiedSchema) { // extension point
        final DfAdditionalSchemaInfo schemaInfo = getAdditionalSchemaInfo(unifiedSchema);
        if (schemaInfo != null) {
            return schemaInfo.getColumnExceptMap();
        }
        return getColumnExceptMap();
    }

    protected boolean isTargetByHint(final String name, final List<String> targetList, final List<String> exceptList) {
        return DfNameHintUtil.isTargetByHint(name, targetList, exceptList);
    }

    protected final DfAdditionalSchemaInfo getAdditionalSchemaInfo(UnifiedSchema unifiedSchema) {
        return getProperties().getDatabaseProperties().getAdditionalSchemaInfo(unifiedSchema);
    }

    // ===================================================================================
    //                                                                 Retry Determination
    //                                                                 ===================
    protected boolean canRetryCaseInsensitive() {
        if (isDatabaseMySQL()) {
            // MySQL causes a trouble by setting a name only differed in case as parameter
            // when Windows and lower_case_table_names = 0
            //  -> Can't create table '.\exampledb\Foo.frm' (errno: 121)
            // and other modes do not need to retry, so it returns false 
            return false;
        }
        return true;
    }
}