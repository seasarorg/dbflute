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
package org.seasar.dbflute.logic.sql2entity.cmentity;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.TypeMap;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.exception.DfJDBCException;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureNotParamResultMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMetaInfo.DfProcedureColumnType;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfOutsideSqlProperties;
import org.seasar.dbflute.s2dao.valuetype.TnValueTypes;
import org.seasar.dbflute.s2dao.valuetype.basic.StringType;
import org.seasar.dbflute.s2dao.valuetype.plugin.StringClobType;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 * @since 0.7.5 (2008/06/28 Saturday)
 */
public class DfProcedureExecutionMetaExtractor {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfProcedureExecutionMetaExtractor.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DfCustomizeEntityMetaExtractor _extractor = new DfCustomizeEntityMetaExtractor();
    protected final List<Object> _numberList = getProperties().getTypeMappingProperties().getJavaNativeNumberList();
    protected final List<Object> _dateList = getProperties().getTypeMappingProperties().getJavaNativeDateList();
    protected final List<Object> _booleanList = getProperties().getTypeMappingProperties().getJavaNativeBooleanList();
    protected final List<Object> _binaryList = getProperties().getTypeMappingProperties().getJavaNativeBinaryList();
    protected final StringType _stringType = new StringType();
    protected final StringClobType _stringClobType = new StringClobType();

    // ===================================================================================
    //                                                                             Process
    //                                                                             =======
    public void extractExecutionMetaData(DataSource dataSource, List<DfProcedureMetaInfo> procedureList)
            throws SQLException {
        final DfOutsideSqlProperties prop = getProperties().getOutsideSqlProperties();
        for (DfProcedureMetaInfo procedure : procedureList) {
            final String procedureFullQualifiedName = procedure.getProcedureFullQualifiedName();
            final String procedureSchemaQualifiedName = procedure.getProcedureSchemaQualifiedName();
            final String procedureName = procedure.getProcedureName();
            if (prop.isExecutionMetaProcedureName(procedureFullQualifiedName)
                    || prop.isExecutionMetaProcedureName(procedureSchemaQualifiedName)
                    || prop.isExecutionMetaProcedureName(procedureName)) {
                doExtractExecutionMetaData(dataSource, procedure);
            }
        }
    }

    protected void doExtractExecutionMetaData(DataSource dataSource, DfProcedureMetaInfo procedure) throws SQLException {
        final List<DfProcedureColumnMetaInfo> columnList = procedure.getProcedureColumnList();
        if (!needsToCall(columnList)) {
            final String name = procedure.buildProcedureLoggingName();
            _log.info("*not needed to call: " + name + " params=" + buildParameterTypeView(columnList));
            return;
        }
        final List<Object> testValueList = DfCollectionUtil.newArrayList();
        setupTestValueList(columnList, testValueList);
        final String procedureSqlName = procedure.buildProcedureSqlName();
        final boolean existsReturn = existsReturnValue(columnList);
        final String sql = createSql(procedureSqlName, columnList.size(), existsReturn, true);
        Connection conn = null;
        CallableStatement cs = null;
        try {
            _log.info("...Calling: " + sql);
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            cs = conn.prepareCall(sql);
            final List<DfProcedureColumnMetaInfo> boundColumnList = DfCollectionUtil.newArrayList();
            setupBindParameter(cs, columnList, testValueList, boundColumnList);
            ResultSet rs = null;

            boolean executed;
            try {
                executed = cs.execute();
            } catch (SQLException e) { // retry without escape because Oracle sometimes hates escape
                final String retrySql = createSql(procedureSqlName, columnList.size(), existsReturn, false);
                try {
                    try {
                        cs.close();
                    } catch (SQLException ignored) {
                    }
                    cs = conn.prepareCall(retrySql);
                    setupBindParameter(cs, columnList, testValueList, boundColumnList);
                    executed = cs.execute();
                    _log.info("  (o) retry: " + retrySql);
                } catch (SQLException ignored) {
                    _log.info("  (x) retry: " + retrySql);
                    throw e;
                }
            }
            if (executed) {
                int closetIndex = 0;
                do {
                    rs = cs.getResultSet();
                    if (rs == null) {
                        break;
                    }
                    final Map<String, DfColumnMetaInfo> columnMetaInfoMap = extractColumnMetaInfoMap(rs, sql);
                    final DfProcedureNotParamResultMetaInfo notParamResult = new DfProcedureNotParamResultMetaInfo();
                    notParamResult.setPropertyName("notParamResult" + (closetIndex + 1));
                    notParamResult.setColumnMetaInfoMap(columnMetaInfoMap);
                    procedure.addNotParamResult(notParamResult);
                    ++closetIndex;
                } while (cs.getMoreResults());
            }
            int index = 0;
            for (DfProcedureColumnMetaInfo column : boundColumnList) {
                final DfProcedureColumnType columnType = column.getProcedureColumnType();
                if (DfProcedureColumnType.procedureColumnIn.equals(columnType)) {
                    ++index;
                    continue;
                }
                final int paramIndex = (index + 1);
                final Object obj;
                if (column.isPostgreSQLCursor()) {
                    obj = TnValueTypes.POSTGRESQL_RESULT_SET.getValue(cs, paramIndex);
                } else if (column.isOracleCursor()) {
                    obj = TnValueTypes.ORACLE_RESULT_SET.getValue(cs, paramIndex);
                } else {
                    obj = cs.getObject(paramIndex); // as default
                }
                if (obj instanceof ResultSet) {
                    rs = (ResultSet) obj;
                    final Map<String, DfColumnMetaInfo> columnMetaInfoMap = extractColumnMetaInfoMap(rs, sql);
                    column.setColumnMetaInfoMap(columnMetaInfoMap);
                }
                ++index;
            }
        } catch (SQLException continued) {
            String msg = "*Failed to execute the procedure for getting meta data:" + ln();
            msg = msg + " " + sql + ln();
            for (DfProcedureColumnMetaInfo column : columnList) {
                msg = msg + "   " + column.getColumnDisplayName() + ln();
            }
            msg = msg + " test values = " + testValueList + ln();
            msg = msg + " " + DfJDBCException.extractMessage(continued);
            SQLException nextEx = continued.getNextException();
            if (nextEx == null) {
                msg = msg + ln() + " " + DfJDBCException.extractMessage(nextEx);
            }
            _log.info(msg);
        } finally {
            if (cs != null) {
                cs.close();
            }
            if (conn != null) {
                conn.rollback();
            }
        }
    }

    protected boolean existsReturnValue(List<DfProcedureColumnMetaInfo> columnList) {
        for (DfProcedureColumnMetaInfo column : columnList) {
            final DfProcedureColumnType columnType = column.getProcedureColumnType();
            if (DfProcedureColumnType.procedureColumnReturn.equals(columnType)) {
                return true;
            }
        }
        return false;
    }

    protected boolean needsToCall(List<DfProcedureColumnMetaInfo> columnList) {
        if (!isOracle() && !isPostgreSQL()) {
            return true; // because of for getting notParamResult
        }
        // Here Oracle or PostgreSQL (that don't support notParamResult)
        for (DfProcedureColumnMetaInfo column : columnList) {
            final DfProcedureColumnType columnType = column.getProcedureColumnType();
            if (DfProcedureColumnType.procedureColumnOut.equals(columnType)
                    || DfProcedureColumnType.procedureColumnInOut.equals(columnType)
                    || DfProcedureColumnType.procedureColumnReturn.equals(columnType)) {
                return true;
            }
        }
        return false;
    }

    protected String buildParameterTypeView(List<DfProcedureColumnMetaInfo> columnList) {
        final StringBuilder sb = new StringBuilder();
        final String prefix = "procedureColumn";
        for (DfProcedureColumnMetaInfo column : columnList) {
            String name = column.getProcedureColumnType().name();
            if (name.startsWith(prefix)) {
                name = name.substring(prefix.length());
            }
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(name);
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    protected void setupTestValueList(List<DfProcedureColumnMetaInfo> columnList, List<Object> testValueList) {
        for (DfProcedureColumnMetaInfo column : columnList) {
            doSetupTestValueList(column, testValueList);
        }
    }

    protected void doSetupTestValueList(DfProcedureColumnMetaInfo column, List<Object> testValueList) {
        final DfProcedureColumnType columnType = column.getProcedureColumnType();
        if (DfProcedureColumnType.procedureColumnReturn.equals(columnType)) {
            return;
        }
        if (DfProcedureColumnType.procedureColumnIn.equals(columnType)
                || DfProcedureColumnType.procedureColumnInOut.equals(columnType)) {

            // mapping by DB type name as pinpoint patch
            if (column.isPostgreSQLUuid() || column.isSQLServerUniqueIdentifier()) {
                testValueList.add("FD8C7155-3A0A-DB11-BAC4-0011F5099158");
                return;
            }

            // mapping by JDBC type
            final String stringValue = "0";
            final int jdbcDefType = column.getJdbcType();
            final String jdbcType = TypeMap.findJdbcTypeByJdbcDefValue(jdbcDefType);
            if (jdbcType == null) {
                testValueList.add(stringValue);
                return;
            }
            final Integer columnSize = column.getColumnSize();
            final Integer decimalDigits = column.getDecimalDigits();
            final String nativeType = TypeMap.findJavaNativeByJdbcType(jdbcType, columnSize, decimalDigits);
            Object testValue = null;
            if (containsAsEndsWith(nativeType, _numberList)) {
                testValue = 0;
            } else if (containsAsEndsWith(nativeType, _dateList)) {
                testValue = DfTypeUtil.toTimestamp("2010-03-31 12:34:56");
            } else if (containsAsEndsWith(nativeType, _booleanList)) {
                testValue = Boolean.FALSE;
            } else if (containsAsEndsWith(nativeType, _binaryList)) {
                testValue = stringValue; // binary type is unsupported here
            } else { // as String
                testValue = stringValue;
            }
            testValueList.add(testValue);
        }
    }

    protected boolean containsAsEndsWith(String str, List<Object> ls) {
        for (Object current : ls) {
            final String currentString = (String) current;
            if (str.endsWith(currentString)) {
                return true;
            }
        }
        return false;
    }

    public String createSql(String procedureName, int bindSize, boolean existsReturn, boolean escape) {
        final StringBuilder sb = new StringBuilder();
        if (escape) {
            sb.append("{");
        }
        final int argSize;
        {
            if (existsReturn) {
                sb.append("? = ");
                argSize = bindSize - 1;
            } else {
                argSize = bindSize;
            }
        }
        sb.append("call ").append(procedureName).append("(");
        for (int i = 0; i < argSize; i++) {
            sb.append("?, ");
        }
        if (argSize > 0) {
            sb.setLength(sb.length() - 2);
        }
        sb.append(")");
        if (escape) {
            sb.append("}");
        }
        return sb.toString();
    }

    protected void setupBindParameter(CallableStatement cs, List<DfProcedureColumnMetaInfo> columnList,
            List<Object> testValueList, List<DfProcedureColumnMetaInfo> boundColumnList) throws SQLException {
        boundColumnList.clear();
        int index = 0;
        int testValueIndex = 0;
        for (DfProcedureColumnMetaInfo column : columnList) {
            final int paramIndex = (index + 1);
            final DfProcedureColumnType columnType = column.getProcedureColumnType();
            final int jdbcType = column.getJdbcType();
            if (DfProcedureColumnType.procedureColumnReturn.equals(columnType)) {
                registerOutParameter(cs, paramIndex, jdbcType, column);
                boundColumnList.add(column);
            } else if (DfProcedureColumnType.procedureColumnIn.equals(columnType)) {
                bindObject(cs, paramIndex, jdbcType, testValueList.get(testValueIndex), column);
                ++testValueIndex;
                boundColumnList.add(column);
            } else if (DfProcedureColumnType.procedureColumnOut.equals(columnType)) {
                registerOutParameter(cs, paramIndex, jdbcType, column);
                boundColumnList.add(column);
            } else if (DfProcedureColumnType.procedureColumnInOut.equals(columnType)) {
                registerOutParameter(cs, paramIndex, jdbcType, column);
                bindObject(cs, paramIndex, jdbcType, testValueList.get(testValueIndex), column);
                ++testValueIndex;
                boundColumnList.add(column);
            }
            ++index;
        }
    }

    protected void registerOutParameter(CallableStatement cs, int paramIndex, int jdbcType,
            DfProcedureColumnMetaInfo column) throws SQLException {
        try {
            if (column.isOracleNCharOrNVarchar()) {
                _stringType.registerOutParameter(cs, paramIndex);
            } else if (column.isConceptTypeStringClob()) {
                _stringClobType.registerOutParameter(cs, paramIndex);
            } else if (column.isPostgreSQLUuid()) {
                TnValueTypes.UUID_AS_DIRECT.registerOutParameter(cs, paramIndex);
            } else if (column.isSQLServerUniqueIdentifier()) {
                TnValueTypes.UUID_AS_STRING.registerOutParameter(cs, paramIndex);
            } else if (column.isPostgreSQLCursor()) {
                TnValueTypes.POSTGRESQL_RESULT_SET.registerOutParameter(cs, paramIndex);
            } else if (column.isOracleCursor()) {
                TnValueTypes.ORACLE_RESULT_SET.registerOutParameter(cs, paramIndex);
            } else {
                cs.registerOutParameter(paramIndex, jdbcType);
            }
        } catch (SQLException e) {
            String msg = "Failed to register OUT parameter(" + paramIndex + "):";
            msg = msg + " " + column.getColumnNameDisp() + " - " + column.getColumnDefinitionLineDisp();
            throw new DfJDBCException(msg, e);
        }
    }

    protected void bindObject(CallableStatement cs, int paramIndex, int jdbcType, Object value,
            DfProcedureColumnMetaInfo column) throws SQLException {
        try {
            if (column.isOracleNCharOrNVarchar()) {
                _stringType.bindValue(cs, paramIndex, value != null ? value.toString() : value);
            } else if (column.isConceptTypeStringClob()) {
                _stringClobType.bindValue(cs, paramIndex, value != null ? value.toString() : value);
            } else if (column.isPostgreSQLUuid()) {
                TnValueTypes.UUID_AS_DIRECT.bindValue(cs, paramIndex, value);
            } else if (column.isSQLServerUniqueIdentifier()) {
                TnValueTypes.UUID_AS_STRING.bindValue(cs, paramIndex, value);
            } else {
                cs.setObject(paramIndex, value, jdbcType);
            }
        } catch (SQLException e) {
            String msg = "Failed to bind parameter(" + paramIndex + "):";
            msg = msg + " " + column.getColumnNameDisp() + " - " + column.getColumnDefinitionLineDisp();
            throw new DfJDBCException(msg, e);
        }
    }

    // ===================================================================================
    //                                                                    Column Meta Info
    //                                                                    ================
    protected Map<String, DfColumnMetaInfo> extractColumnMetaInfoMap(ResultSet rs, String sql) throws SQLException {
        return _extractor.extractColumnMetaInfoMap(rs, sql, null);
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
        return getBasicProperties().isDatabaseMSAccess();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return "\n";
    }
}