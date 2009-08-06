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
package org.seasar.dbflute.logic.mapping;

import java.sql.Types;
import java.util.Map;

import org.apache.torque.engine.database.model.TypeMap;
import org.seasar.dbflute.exception.DfJDBCTypeNotFoundException;

/**
 * @author jflute
 */
public class DfJdbcTypeMapper {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Map<String, String> _nameToJdbcTypeMap;
    protected Resource _resource;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    public DfJdbcTypeMapper(Map<String, String> nameToJdbcTypeMap, Resource resource) {
        _nameToJdbcTypeMap = nameToJdbcTypeMap;
        _resource = resource;
    }

    public static interface Resource {
        boolean isTargetLanguageJava();

        boolean isDatabaseOracle();

        boolean isDatabasePostgreSQL();
    }

    // ===================================================================================
    //                                                                 Torque Type Getting
    //                                                                 ===================
    /**
     * Get the JDBC type of the column. <br /> 
     * The priority of mapping is as follows:
     * <pre>
     * 1. The specified type mapping by DB type name (typeMappingMap.dfprop)
     * 2. The fixed type mapping (PostgreSQL's OID and Oracle's Date and so on...)
     * 3. The standard type mapping by JDBC type if the type is not 'OTHER' (typeMappingMap.dfprop)
     * 4. The auto type mapping by DB type name
     * 5. String finally
     * </pre>
     * @param jdbcDefValue The JDBC definition value.
     * @param dbTypeName The name of DB data type. (Nullable: If null, the mapping using this is invalid)
     * @return The JDBC type of the column. (NotNull)
     */
    public String getColumnJdbcType(int jdbcDefValue, String dbTypeName) {
        // * * * * * *
        // Priority 1
        // * * * * * *
        if (dbTypeName != null) {
            if (_nameToJdbcTypeMap != null && !_nameToJdbcTypeMap.isEmpty()) {
                final String torqueType = _nameToJdbcTypeMap.get(dbTypeName);
                if (torqueType != null) {
                    return (String) torqueType;
                }
            }
        }

        // * * * * * *
        // Priority 2
        // * * * * * *
        if (isPostgreSQLBytesOid(dbTypeName)) {
            return getBlobJdbcType();
        }
        if (isOracleCompatibleDate(jdbcDefValue, dbTypeName)) {
            // For compatible to Oracle's JDBC driver.
            return getDateJdbcType();
        }

        // * * * * * *
        // Priority 3
        // * * * * * *
        if (!isOtherType(jdbcDefValue)) {
            try {
                return getJdbcType(jdbcDefValue);
            } catch (DfJDBCTypeNotFoundException ignored) {
            }
        }

        // * * * * * *
        // Priority 4
        // * * * * * *
        // /- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // Here is coming if the JDBC type is OTHER or is not found in TypeMap.
        // - - - - - - - - - -/
        if (dbTypeName == null) {
            return getVarcharJdbcType();
        } else if (dbTypeName.toLowerCase().contains("varchar")) {
            return getVarcharJdbcType();
        } else if (dbTypeName.toLowerCase().contains("char")) {
            return getCharJdbcType();
        } else if (dbTypeName.toLowerCase().contains("timestamp")) {
            return getTimestampJdbcType();
        } else if (dbTypeName.toLowerCase().contains("date")) {
            return getDateJdbcType();
        } else if (dbTypeName.toLowerCase().contains("clob")) {
            return getClobJdbcType();
        } else if (_resource.isTargetLanguageJava() && dbTypeName.toLowerCase().contains("uuid")) {
            // /- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
            // This is for Java only because the type has not been checked yet on C#.
            // - - - - - - - - - -/

            // [UUID Headache]: The reason why UUID type has not been supported yet on JDBC.
            return TypeMap.UUID;
        } else {
            // * * * * * *
            // Priority 5
            // * * * * * *
            return getVarcharJdbcType();
        }
    }

    // -----------------------------------------------------
    //                                    Type Determination
    //                                    ------------------
    public boolean isOracleCompatibleDate(final int jdbcType, final String dbTypeName) {
        return _resource.isDatabaseOracle() && java.sql.Types.TIMESTAMP == jdbcType
                && "date".equalsIgnoreCase(dbTypeName);
    }

    public boolean isOracleStringClob(final String dbTypeName) {
        return _resource.isDatabaseOracle() && "clob".equalsIgnoreCase(dbTypeName);
    }

    public boolean isPostgreSQLBytesOid(final String dbTypeName) {
        return _resource.isDatabasePostgreSQL() && "oid".equalsIgnoreCase(dbTypeName);
    }
    
    public boolean isUUID(final String dbTypeName) {
        return "uuid".equalsIgnoreCase(dbTypeName);
    }

    protected boolean isOtherType(final int jdbcDefValue) {
        return Types.OTHER == jdbcDefValue;
    }

    // -----------------------------------------------------
    //                                      JDBC Type Helper
    //                                      ----------------
    protected String getJdbcType(int jdbcDefValue) {
        return TypeMap.findJdbcTypeByJdbcDefValue(jdbcDefValue);
    }

    protected String getVarcharJdbcType() {
        return TypeMap.findJdbcTypeByJdbcDefValue(java.sql.Types.VARCHAR);
    }

    protected String getCharJdbcType() {
        return TypeMap.findJdbcTypeByJdbcDefValue(java.sql.Types.CHAR);
    }

    protected String getTimestampJdbcType() {
        return TypeMap.findJdbcTypeByJdbcDefValue(java.sql.Types.TIMESTAMP);
    }

    protected String getDateJdbcType() {
        return TypeMap.findJdbcTypeByJdbcDefValue(java.sql.Types.DATE);
    }

    protected String getClobJdbcType() {
        return TypeMap.findJdbcTypeByJdbcDefValue(java.sql.Types.CLOB);
    }

    protected String getBlobJdbcType() {
        return TypeMap.findJdbcTypeByJdbcDefValue(java.sql.Types.BLOB);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return _nameToJdbcTypeMap + ":" + _resource;
    }
}