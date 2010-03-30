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
package org.seasar.dbflute.logic.cmentity;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.TypeMap;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureNotParamResultMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMetaInfo.DfProcedureColumnType;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfOutsideSqlProperties;
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
    protected final DfCustomizeEntityMetaExtractor extractor = new DfCustomizeEntityMetaExtractor();
    protected final List<Object> numberList = getProperties().getTypeMappingProperties().getJavaNativeNumberList();
    protected final List<Object> dateList = getProperties().getTypeMappingProperties().getJavaNativeDateList();
    protected final List<Object> booleanList = getProperties().getTypeMappingProperties().getJavaNativeBooleanList();
    protected final List<Object> binaryList = getProperties().getTypeMappingProperties().getJavaNativeBinaryList();

    // ===================================================================================
    //                                                                             Process
    //                                                                             =======
    public void extractExecutionMetaData(DataSource dataSource, List<DfProcedureMetaInfo> procedureList)
            throws SQLException {
        final DfOutsideSqlProperties prop = getProperties().getOutsideSqlProperties();
        for (DfProcedureMetaInfo procedure : procedureList) {
            final String procedureName = procedure.getProcedureName();
            final String procedureFullName = procedure.getProcedureFullName();
            if (prop.isExecutionMetaProcedureName(procedureFullName)
                    || prop.isExecutionMetaProcedureName(procedureName)) {
                doExtractExecutionMetaData(dataSource, procedure);
            }
        }
    }

    protected void doExtractExecutionMetaData(DataSource dataSource, DfProcedureMetaInfo procedure) throws SQLException {
        // use unique list because Oracle package procedure may return duplicated information 
        final List<DfProcedureColumnMetaInfo> columnList = procedure.getProcedureColumnUniqueList();

        final List<Object> testValueList = new ArrayList<Object>();
        final boolean existsReturn = existsReturnValue(columnList);
        setupTestValueList(columnList, testValueList);
        final String sql = createSql(procedure.getProcedureSqlName(), columnList.size(), existsReturn);
        Connection conn = null;
        CallableStatement cs = null;
        try {
            _log.info("...Calling: " + sql);
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            cs = dataSource.getConnection().prepareCall(sql);
            final List<DfProcedureColumnMetaInfo> boundColumnList = new ArrayList<DfProcedureColumnMetaInfo>();
            setupBindParameter(cs, columnList, testValueList, boundColumnList);
            ResultSet rs = null;
            if (cs.execute()) {
                int closetIndex = 0;
                do {
                    rs = cs.getResultSet();
                    if (rs == null) {
                        break;
                    }
                    final Map<String, DfColumnMetaInfo> columnMetaInfoMap = extractColumnMetaInfoMap(rs, sql);
                    final DfProcedureNotParamResultMetaInfo notParamResult = new DfProcedureNotParamResultMetaInfo();
                    notParamResult.setPropertyName("notParamResultList" + (closetIndex + 1));
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
                final Object obj = cs.getObject(index + 1);
                if (obj instanceof ResultSet) {
                    rs = (ResultSet) obj;
                    final Map<String, DfColumnMetaInfo> columnMetaInfoMap = extractColumnMetaInfoMap(rs, sql);
                    column.setColumnMetaInfoMap(columnMetaInfoMap);
                }
                ++index;
            }
        } catch (SQLException e) {
            String msg = "*Failed to execute the procedure for getting meta data:" + ln();
            msg = msg + " " + sql + ln();
            msg = msg + " " + e.getMessage();
            _log.info(msg); // continued
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
            final int jdbcDefType = column.getJdbcType();
            final String jdbcType = TypeMap.findJdbcTypeByJdbcDefValue(jdbcDefType);
            if (jdbcType == null) {
                testValueList.add("0");
                return;
            }
            final Integer columnSize = column.getColumnSize();
            final Integer decimalDigits = column.getDecimalDigits();
            final String nativeType = TypeMap.findJavaNativeByJdbcType(jdbcType, columnSize, decimalDigits);
            Object testValue = null;
            if (containsAsEndsWith(nativeType, numberList)) {
                testValue = 0;
            } else if (containsAsEndsWith(nativeType, dateList)) {
                testValue = DfTypeUtil.toDate("2010-03-30");
            } else if (containsAsEndsWith(nativeType, booleanList)) {
                testValue = Boolean.FALSE;
            } else if (containsAsEndsWith(nativeType, binaryList)) {
                return; // binary type is unsupported here
            } else { // as String
                testValue = "0";
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

    public String createSql(String procedureName, int bindSize, boolean existsReturn) {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
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
        sb.append(")}");
        return sb.toString();
    }

    protected void setupBindParameter(CallableStatement cs, List<DfProcedureColumnMetaInfo> columnList,
            List<Object> testValueList, List<DfProcedureColumnMetaInfo> boundColumnList) throws SQLException {
        int index = 0;
        for (DfProcedureColumnMetaInfo column : columnList) {
            final DfProcedureColumnType columnType = column.getProcedureColumnType();
            if (DfProcedureColumnType.procedureColumnReturn.equals(columnType)) {
                cs.registerOutParameter(index + 1, column.getJdbcType());
                boundColumnList.add(column);
            } else if (DfProcedureColumnType.procedureColumnIn.equals(columnType)) {
                cs.setObject(index + 1, testValueList.remove(0));
                boundColumnList.add(column);
            } else if (DfProcedureColumnType.procedureColumnOut.equals(columnType)) {
                cs.registerOutParameter(index + 1, column.getJdbcType());
                boundColumnList.add(column);
            } else if (DfProcedureColumnType.procedureColumnInOut.equals(columnType)) {
                cs.registerOutParameter(index + 1, column.getJdbcType());
                cs.setObject(index + 1, testValueList.remove(0));
                boundColumnList.add(column);
            }
            ++index;
        }
    }

    protected Map<String, DfColumnMetaInfo> extractColumnMetaInfoMap(ResultSet rs, String sql) throws SQLException {
        return extractor.extractColumnMetaInfoMap(rs, sql, null);
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
        return getBasicProperties().isDatabaseMsAccess();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return "\n";
    }
}