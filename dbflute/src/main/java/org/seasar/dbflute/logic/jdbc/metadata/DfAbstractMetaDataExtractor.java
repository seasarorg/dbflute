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

import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public abstract class DfAbstractMetaDataExtractor {

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected String filterNoNameSchema(String schemaName) { // basically for MySQL
        if (Srl.is_Null_or_TrimmedEmpty(schemaName)) {
            return schemaName;
        }
        final String suffix = "." + DfDatabaseProperties.NO_NAME_SCHEMA;
        if (!schemaName.endsWith(suffix)) {
            return schemaName;
        }
        return schemaName.substring(0, schemaName.lastIndexOf(suffix));
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
        return schemaName.substring(0, dotIndex);
    }

    protected String extractRealSchemaName(String schemaName) { // for DBMS that supports both schema and catalog
        if (Srl.is_Null_or_Empty(schemaName)) {
            return schemaName;
        }
        int dotIndex = schemaName.indexOf(".");
        if (dotIndex < 0) {
            return schemaName;
        }
        // basically additionalSchema with Database only
        final String realSchemaName = schemaName.substring(dotIndex + ".".length());
        if (DfDatabaseProperties.NO_NAME_SCHEMA.equals(realSchemaName)) {
            return null;
        }
        return realSchemaName;
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return "\n";
    }
}
