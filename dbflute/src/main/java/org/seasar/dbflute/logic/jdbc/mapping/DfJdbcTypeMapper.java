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
package org.seasar.dbflute.logic.jdbc.mapping;

import java.sql.Types;
import java.util.Map;

import org.apache.torque.engine.database.model.TypeMap;
import org.seasar.dbflute.util.Srl;

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
        boolean isLangJava();

        boolean isDbmsPostgreSQL();

        boolean isDbmsOracle();
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
        final String adjustment = processForcedAdjustment(jdbcDefValue, dbTypeName);
        if (adjustment != null) {
            return adjustment;
        }

        // * * * * * *
        // Priority 3
        // * * * * * *
        if (!isOtherType(jdbcDefValue)) {
            final String jdbcType = getJdbcType(jdbcDefValue);
            if (Srl.is_NotNull_and_NotEmpty(jdbcType)) {
                return jdbcType;
            }
        }
        // here means that it cannot determine by jdbcDefValue

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
        } else if (_resource.isLangJava() && dbTypeName.toLowerCase().contains("uuid")) {
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

    protected String processForcedAdjustment(int jdbcDefValue, String dbTypeName) {
        if (isDbTypeBytesOid(dbTypeName)) {
            return getBlobJdbcType();
        }
        if (isPostgreSQL_Interval(dbTypeName)) {
            return getTimeJdbcType();
        }
        if (isOracle_CompatibleDate(jdbcDefValue, dbTypeName)) {
            // for compatible to Oracle's JDBC driver
            return getDateJdbcType();
        }
        return null;
    }

    // -----------------------------------------------------
    //                                          Concept Type
    //                                          ------------
    public boolean isDbTypeStringClob(final String dbTypeName) {
        return _resource.isDbmsOracle() && "clob".equalsIgnoreCase(dbTypeName);
    }

    public boolean isDbTypeBytesOid(final String dbTypeName) {
        return _resource.isDbmsPostgreSQL() && "oid".equalsIgnoreCase(dbTypeName);
    }

    // -----------------------------------------------------
    //                                         Pinpoint Type
    //                                         -------------
    public boolean isPostgreSQL_BpChar(final String dbTypeName) {
        return _resource.isDbmsPostgreSQL() && "bpchar".equalsIgnoreCase(dbTypeName);
    }

    public boolean isPostgreSQL_Numeric(final String dbTypeName) {
        return _resource.isDbmsPostgreSQL() && "numeric".equalsIgnoreCase(dbTypeName);
    }

    public boolean isPostgreSQL_Interval(final String dbTypeName) {
        return _resource.isDbmsPostgreSQL() && "interval".equalsIgnoreCase(dbTypeName);
    }

    public boolean isOracle_CompatibleDate(final int jdbcType, final String dbTypeName) {
        return _resource.isDbmsOracle() && java.sql.Types.TIMESTAMP == jdbcType && "date".equalsIgnoreCase(dbTypeName);
    }

    public boolean isOracle_BinaryFloatDouble(final int jdbcType, final String dbTypeName) {
        return _resource.isDbmsOracle()
                && ("binary_float".equalsIgnoreCase(dbTypeName) || "binary_double".equalsIgnoreCase(dbTypeName));
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

    protected String getTimeJdbcType() {
        return TypeMap.findJdbcTypeByJdbcDefValue(java.sql.Types.TIME);
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