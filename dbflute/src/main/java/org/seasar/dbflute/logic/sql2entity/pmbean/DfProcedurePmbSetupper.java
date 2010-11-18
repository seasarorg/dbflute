package org.seasar.dbflute.logic.sql2entity.pmbean;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.TypeMap;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.helper.language.grammar.DfGrammarInfo;
import org.seasar.dbflute.logic.jdbc.handler.DfColumnHandler;
import org.seasar.dbflute.logic.jdbc.handler.DfProcedureHandler;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureNotParamResultMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureColumnMetaInfo.DfProcedureColumnType;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfProcedureMetaInfo.DfProcedureType;
import org.seasar.dbflute.logic.sql2entity.cmentity.DfProcedureExecutionMetaExtractor;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfOutsideSqlProperties;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfProcedurePmbSetupper {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfProcedurePmbSetupper.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DataSource _dataSource;
    protected final Map<String, Map<String, DfColumnMetaInfo>> _entityInfoMap;
    protected final Map<String, DfPmbMetaData> _pmbMetaDataMap;
    protected final DfColumnHandler _columnHandler = new DfColumnHandler();
    protected final DfProcedureHandler _procedureHandler = new DfProcedureHandler();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfProcedurePmbSetupper(DataSource dataSource, Map<String, Map<String, DfColumnMetaInfo>> entityInfoMap,
            Map<String, DfPmbMetaData> pmbMetaDataMap) {
        _dataSource = dataSource;
        _entityInfoMap = entityInfoMap;
        _pmbMetaDataMap = pmbMetaDataMap;
    }

    // ===================================================================================
    //                                                                              Set up
    //                                                                              ======
    public void setupProcedure() throws SQLException {
        if (!getOutsideSqlProperties().isGenerateProcedureParameterBean()) {
            return;
        }
        _log.info(" ");
        _log.info("...Setting up procedures for generating parameter-beans");
        final List<DfProcedureMetaInfo> procedureList = getAvailableProcedureList();
        _log.info("/= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
        for (DfProcedureMetaInfo procedure : procedureList) {
            final Map<String, String> propertyNameTypeMap = DfCollectionUtil.newLinkedHashMap();
            final Map<String, String> propertyNameOptionMap = DfCollectionUtil.newLinkedHashMap();
            final Map<String, String> propertyNameColumnNameMap = DfCollectionUtil.newLinkedHashMap();
            final Map<String, DfProcedureColumnMetaInfo> propertyNameColumnInfoMap = DfCollectionUtil
                    .newLinkedHashMap();
            final List<DfProcedureColumnMetaInfo> procedureColumnList = procedure.getProcedureColumnList();
            final List<DfProcedureNotParamResultMetaInfo> notParamResultList = procedure.getNotParamResultList();

            final String pmbName = convertProcedureNameToPmbName(procedure.getProcedureName());
            {
                final String procDisp = procedure.buildProcedureLoggingName();
                final DfProcedureType procType = procedure.getProcedureType();
                _log.info("[" + pmbName + "]: " + procDisp + " // " + procType);
                if (procedureColumnList.isEmpty() && notParamResultList.isEmpty()) {
                    _log.info("    *No Parameter");
                }
            }

            boolean existsCustomizeEntity = false;
            int index = 0;
            for (DfProcedureColumnMetaInfo column : procedureColumnList) {
                final String columnName;
                {
                    final String plainColumnName = column.getColumnName();
                    if (Srl.is_NotNull_and_NotTrimmedEmpty(plainColumnName)) {
                        columnName = resolveVendorColumnNameHeadable(plainColumnName);
                    } else {
                        columnName = "arg" + (index + 1);
                    }
                }
                final String propertyName;
                {
                    propertyName = convertColumnNameToPropertyName(columnName);
                }
                propertyNameColumnInfoMap.put(propertyName, column);
                String propertyType = getProcedureColumnPropertyType(column);
                if (column.hasColumnMetaInfo()) {
                    final String entityName = convertProcedurePmbNameToEntityName(pmbName, propertyName);
                    _entityInfoMap.put(entityName, column.getColumnMetaInfoMap());
                    existsCustomizeEntity = true;
                    propertyType = convertProcedureListPropertyType(entityName);
                }
                propertyNameTypeMap.put(propertyName, propertyType);
                final DfProcedureColumnType procedureColumnType = column.getProcedureColumnType();
                propertyNameOptionMap.put(propertyName, procedureColumnType.toString());
                propertyNameColumnNameMap.put(propertyName, columnName);
                String msg = "    " + propertyType + " " + propertyName + ";";
                msg = msg + " // " + column.getProcedureColumnType();
                msg = msg + "(" + column.getJdbcDefType() + ", " + column.getDbTypeName() + ")";
                _log.info(msg);
                ++index;
            }
            for (DfProcedureNotParamResultMetaInfo result : notParamResultList) {
                final String propertyName = result.getPropertyName();
                String propertyType = getProcedureDefaultResultSetPropertyType();
                if (result.hasColumnMetaInfo()) {
                    final String entityName = convertProcedurePmbNameToEntityName(pmbName, propertyName);
                    _entityInfoMap.put(entityName, result.getColumnMetaInfoMap());
                    existsCustomizeEntity = true;
                    propertyType = convertProcedureListPropertyType(entityName);
                }
                propertyNameTypeMap.put(propertyName, propertyType);
                propertyNameOptionMap.put(propertyName, DfProcedureColumnType.procedureColumnResult.toString());
                propertyNameColumnNameMap.put(propertyName, propertyName);
                String msg = "    " + propertyType + " " + propertyName + ";";
                msg = msg + " // " + DfProcedureColumnType.procedureColumnResult;
                _log.info(msg);
            }
            final DfPmbMetaData parameterBeanMetaData = new DfPmbMetaData();
            parameterBeanMetaData.setClassName(pmbName);
            parameterBeanMetaData.setPropertyNameTypeMap(propertyNameTypeMap);
            parameterBeanMetaData.setPropertyNameOptionMap(propertyNameOptionMap);
            parameterBeanMetaData.setProcedureName(procedure.buildProcedureSqlName());
            parameterBeanMetaData.setPropertyNameColumnNameMap(propertyNameColumnNameMap);
            parameterBeanMetaData.setPropertyNameColumnInfoMap(propertyNameColumnInfoMap);
            parameterBeanMetaData.setRefCustomizeEntity(existsCustomizeEntity);
            _pmbMetaDataMap.put(pmbName, parameterBeanMetaData);
        }
        _log.info("= = = = = = = = = =/");
        _log.info(" ");
    }

    // -----------------------------------------------------
    //                                   Procedure Meta Info
    //                                   -------------------
    protected List<DfProcedureMetaInfo> getAvailableProcedureList() throws SQLException {
        _procedureHandler.includeProcedureSynonym(_dataSource);
        final List<DfProcedureMetaInfo> procedureList = _procedureHandler.getAvailableProcedureList(_dataSource);
        if (getOutsideSqlProperties().isGenerateProcedureCustomizeEntity()) {
            final DfProcedureExecutionMetaExtractor executionMetaHandler = new DfProcedureExecutionMetaExtractor();
            executionMetaHandler.extractExecutionMetaData(_dataSource, procedureList);
        }
        return procedureList;
    }

    // -----------------------------------------------------
    //                                      Procedure Column
    //                                      ----------------
    protected String getProcedureColumnPropertyType(DfProcedureColumnMetaInfo column) {
        if (isResultSetProperty(column)) {
            return getProcedureDefaultResultSetPropertyType();
        }
        final int jdbcDefType = column.getJdbcDefType();
        final String dbTypeName = column.getDbTypeName();
        final Integer columnSize = column.getColumnSize();
        final Integer decimalDigits = column.getDecimalDigits();
        final String propertyType;
        if (column.isOracleNumber()) {
            // because the length setting of procedure parameter is unsupported on Oracle
            propertyType = TypeMap.getDefaultDecimalJavaNativeType();
        } else if (column.isOracleTreatedAsArray()) {
            // here dbTypeName is "PL/SQL TABLE" or "TABLE" or "VARRAY" (it's not useful for type mapping)
            final String elementTypeName = column.getElementTypeName();
            final DfGrammarInfo grammarInfo = getBasicProperties().getLanguageDependencyInfo().getGrammarInfo();
            if (_columnHandler.hasMappingJdbcType(jdbcDefType, dbTypeName)) {
                final String torqueType = _columnHandler.getColumnJdbcType(jdbcDefType, elementTypeName);
                final String elementPropertyType = TypeMap.findJavaNativeByJdbcType(torqueType, columnSize,
                        decimalDigits);
                propertyType = grammarInfo.getGenericListClassName(elementPropertyType);
            } else {
                propertyType = grammarInfo.getGenericListClassName("Object");
            }
        } else {
            if (_columnHandler.hasMappingJdbcType(jdbcDefType, dbTypeName)) {
                final String torqueType = _columnHandler.getColumnJdbcType(jdbcDefType, dbTypeName);
                propertyType = TypeMap.findJavaNativeByJdbcType(torqueType, columnSize, decimalDigits);
            } else {
                // procedure has many-many types so it uses Object type (not String) 
                propertyType = "Object";
            }
        }
        return propertyType;
    }

    protected String getProcedureDefaultResultSetPropertyType() {
        final DfGrammarInfo grammarInfo = getBasicProperties().getLanguageDependencyInfo().getGrammarInfo();
        return grammarInfo.getGenericMapListClassName("String", "Object");
    }

    // -----------------------------------------------------
    //                                        Various Helper
    //                                        --------------
    protected boolean isResultSetProperty(DfProcedureColumnMetaInfo column) {
        if (column.hasColumnMetaInfo()) {
            return true;
        }
        if (column.isPostgreSQLCursor() || column.isOracleCursor()) {
            return true;
        } else {
            return false;
        }
    }

    protected String convertProcedureNameToPmbName(String procedureName) {
        procedureName = Srl.replace(procedureName, ".", "_");
        procedureName = resolveVendorProcedureNameHeadable(procedureName);
        return Srl.camelize(procedureName) + "Pmb";
    }

    protected String resolveVendorProcedureNameHeadable(String procedureName) {
        if (getBasicProperties().isDatabaseSQLServer()) {
            // SQLServer returns 'sp_foo;1'
            procedureName = Srl.substringLastFront(procedureName, ";");
        }
        return procedureName;
    }

    protected String convertProcedurePmbNameToEntityName(String pmbName, String propertyName) {
        final String baseName = Srl.substringLastFront(pmbName, "Pmb");
        final String entityName = baseName + Srl.initCap(propertyName);
        return entityName;
    }

    protected String convertProcedureListPropertyType(String entityName) {
        final DfGrammarInfo grammarInfo = getBasicProperties().getLanguageDependencyInfo().getGrammarInfo();
        return grammarInfo.getGenericListClassName(entityName);
    }

    protected String convertColumnNameToPropertyName(String columnName) {
        columnName = resolveVendorColumnNameHeadable(columnName);
        return Srl.initBeansProp(Srl.camelize(columnName));
    }

    protected String resolveVendorColumnNameHeadable(String columnName) {
        if (getBasicProperties().isDatabaseSQLServer()) {
            // SQLServer returns '@returnValue'
            columnName = Srl.substringFirstRear(columnName, "@");
        }
        return columnName;
    }

    protected DfBasicProperties getBasicProperties() {
        return DfBuildProperties.getInstance().getBasicProperties();
    }

    protected DfOutsideSqlProperties getOutsideSqlProperties() {
        return DfBuildProperties.getInstance().getOutsideSqlProperties();
    }
}
