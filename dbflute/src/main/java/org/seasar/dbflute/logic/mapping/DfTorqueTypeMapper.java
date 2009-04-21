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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.TypeMap;

/**
 * @author jflute
 */
public class DfTorqueTypeMapper {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfTorqueTypeMapper.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Map<String, String> _nameToTorqueTypeMap;
    protected Resource _resource;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    public DfTorqueTypeMapper(Map<String, String> nameToTorqueTypeMap, Resource resource) {
        _nameToTorqueTypeMap = nameToTorqueTypeMap;
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
     * Get the Torque type of the column. <br /> 
     * The priority of mapping is as follows:
     * <pre>
     * 1. The specified type mapping by DB type name (typeMappingMap.dfprop)
     * 2. The fixed type mapping (PostgreSQL's OID and Oracle's Date and so on...)
     * 3. The standard type mapping by JDBC type if the type is not 'OTHER' (typeMappingMap.dfprop)
     * 4. The auto type mapping by DB type name
     * 5. String finally
     * </pre>
     * @param jdbcType The data type of JDBC.
     * @param dbTypeName The name of DB data type. (Nullable: If null, the mapping using this is invalid)
     * @return The Torque type of the column. (NotNull)
     */
    public String getColumnTorqueType(int jdbcType, String dbTypeName) {
        // * * * * * *
        // Priority 1
        // * * * * * *
        if (dbTypeName != null) {
            if (_nameToTorqueTypeMap != null && !_nameToTorqueTypeMap.isEmpty()) {
                final String torqueType = _nameToTorqueTypeMap.get(dbTypeName);
                if (torqueType != null) {
                    return (String) torqueType;
                }
            }
        }

        // * * * * * *
        // Priority 2
        // * * * * * *
        if (isPostgreSQLBytesOid(dbTypeName)) {
            return getBlobTorqueType();
        }
        if (isOracleCompatibleDate(jdbcType, dbTypeName)) {
            // For compatible to Oracle's JDBC driver.
            return getDateTorqueType();
        }

        // * * * * * *
        // Priority 3
        // * * * * * *
        if (!isOtherType(jdbcType)) {
            try {
                return getTorqueType(jdbcType);
            } catch (RuntimeException e) {
                String msg = "Not found the sqlTypeCode in TypeMap: jdbcType=";
                msg = msg + jdbcType + " message=" + e.getMessage();
                _log.warn(msg);
            }
        }

        // * * * * * *
        // Priority 4
        // * * * * * *
        if (dbTypeName == null) {
            return getVarcharTorqueType();
        } else if (dbTypeName.toLowerCase().contains("varchar")) {
            return getVarcharTorqueType();
        } else if (dbTypeName.toLowerCase().contains("char")) {
            return getCharTorqueType();
        } else if (dbTypeName.toLowerCase().contains("timestamp")) {
            return getTimestampTorqueType();
        } else if (dbTypeName.toLowerCase().contains("date")) {
            return getDateTorqueType();
        } else if (dbTypeName.toLowerCase().contains("clob")) {
            return getClobTorqueType();
        } else if (_resource.isTargetLanguageJava() && dbTypeName.toLowerCase().contains("uuid")) {
            // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
            // This is for Java only because the type has not been checked yet on C#.
            // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

            // [UUID Headache]: The reason why UUID type has not been supported yet on JDBC.
            return TypeMap.UUID;
        } else {
            // * * * * * *
            // Priority 5
            // * * * * * *
            return getVarcharTorqueType();
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

    protected boolean isOtherType(final int jdbcType) {
        return Types.OTHER == jdbcType;
    }

    // -----------------------------------------------------
    //                                    Torque Type Helper
    //                                    ------------------
    protected String getTorqueType(int jdbcType) {
        return TypeMap.getTorqueType(jdbcType);
    }

    protected String getVarcharTorqueType() {
        return TypeMap.getTorqueType(java.sql.Types.VARCHAR);
    }

    protected String getCharTorqueType() {
        return TypeMap.getTorqueType(java.sql.Types.CHAR);
    }

    protected String getTimestampTorqueType() {
        return TypeMap.getTorqueType(java.sql.Types.TIMESTAMP);
    }

    protected String getDateTorqueType() {
        return TypeMap.getTorqueType(java.sql.Types.DATE);
    }

    protected String getClobTorqueType() {
        return TypeMap.getTorqueType(java.sql.Types.CLOB);
    }

    protected String getBlobTorqueType() {
        return TypeMap.getTorqueType(java.sql.Types.BLOB);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return _nameToTorqueTypeMap + ":" + _resource;
    }
}