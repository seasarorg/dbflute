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
package org.seasar.dbflute.helper.jdbc.metadata;

import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.jdbc.determiner.DfJdbcDeterminer;
import org.seasar.dbflute.logic.factory.DfJdbcDeterminerFactory;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.util.DfNameHintUtil;

/**
 * @author jflute
 */
public class DfAbstractMetaDataHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** List for except table. */
    protected List<String> _tableExceptList;

    /** List for target table. */
    protected List<String> _tableTargetList;

    /** Simple list for except column. */
    protected List<String> _simpleColumnExceptList;

    // ===================================================================================
    //                                                                            Property
    //                                                                            ========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfBasicProperties getBasicProperties() {
        return DfBuildProperties.getInstance().getBasicProperties();
    }

    protected List<String> getTableExceptList() {
        if (_tableExceptList == null) {
            _tableExceptList = getProperties().getDatabaseInfoProperties().getTableExceptList();
        }
        return _tableExceptList;
    }

    protected List<String> getTableTargetList() {
        if (_tableTargetList == null) {
            _tableTargetList = getProperties().getDatabaseInfoProperties().getTableTargetList();
        }
        return _tableTargetList;
    }

    protected List<String> getSimpleColumnExceptList() {
        if (_simpleColumnExceptList == null) {
            _simpleColumnExceptList = getProperties().getDatabaseInfoProperties().getSimpleColumnExceptList();
        }
        return _simpleColumnExceptList;
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
    //                                                                Except Determination
    //                                                                ====================
    /**
     * Is the table name out of sight?
     * @param tableName Table name. (NotNull)
     * @return Determination.
     */
    protected boolean isTableExcept(final String tableName) {
        if (tableName == null) {
            throw new NullPointerException("Argument[tableName] is required.");
        }

        final List<String> targetList = getTableTargetList();
        if (targetList == null) {
            throw new IllegalStateException("getTableTargetList() must not return null: + " + tableName);
        }

        final List<String> exceptList = getTableExceptList();
        return !isTargetByHint(tableName, targetList, exceptList);
    }

    /**
     * Is the column name out of sight?
     * @param columnName Column name. (NotNull)
     * @return Determination.
     */
    protected boolean isColumnExcept(final String columnName) {
        if (columnName == null) {
            throw new NullPointerException("Argument[columnName] is required.");
        }

        final List<String> columnExceptSimpleList = getSimpleColumnExceptList();
        return !isTargetByHint(columnName, new ArrayList<String>(), columnExceptSimpleList);
    }

    protected boolean isTargetByHint(final String name, final List<String> targetList, final List<String> exceptList) {
        return DfNameHintUtil.isTargetByHint(name, targetList, exceptList);
    }
}