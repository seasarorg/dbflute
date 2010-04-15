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
package org.seasar.dbflute.logic.jdbc.metadata;

import java.util.LinkedHashMap;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.jdbc.determiner.DfJdbcDeterminer;
import org.seasar.dbflute.logic.factory.DfJdbcDeterminerFactory;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public abstract class DfAbstractMetaDataExtractor {

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected String filterSchemaName(String schemaName) {
        // The driver throws the exception if the value is empty string.
        if (Srl.isTrimmedEmpty(schemaName) && !isSchemaNameEmptyAllowed()) {
            return null;
        }
        return schemaName;
    }

    protected String filterNoNameSchema(String schemaName) { // basically for MySQL
        if (Srl.is_Null_or_TrimmedEmpty(schemaName)) {
            return schemaName;
        }
        if (!schemaName.endsWith("." + DfDatabaseProperties.NO_NAME_SCHEMA)) {
            return schemaName;
        }
        return filterSchemaName(Srl.substringLastFront(schemaName, "."));
    }

    protected String extractCatalogName(String schemaName) { // for DBMS that supports both schema and catalog
        if (Srl.is_Null_or_Empty(schemaName)) {
            return null;
        }
        int dotIndex = schemaName.indexOf(".");
        if (dotIndex < 0) {
            return null;
        }
        // basically additionalSchema with Database only
        return Srl.substringFirstFront(schemaName, ".");
    }

    protected String extractPureSchemaName(String catalogSchema) { // for DBMS that supports both schema and catalog
        if (Srl.is_Null_or_Empty(catalogSchema)) {
            return filterSchemaName(catalogSchema);
        }
        int dotIndex = catalogSchema.indexOf(".");
        if (dotIndex < 0) {
            return filterSchemaName(catalogSchema);
        }
        // basically additionalSchema with Database only
        final String pureSchemaName = Srl.substringFirstRear(catalogSchema, ".");
        if (DfDatabaseProperties.NO_NAME_SCHEMA.equals(pureSchemaName)) {
            return null;
        }
        return filterSchemaName(pureSchemaName);
    }

    // ===================================================================================
    //                                                        Database Dependency Resolver
    //                                                        ============================
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
