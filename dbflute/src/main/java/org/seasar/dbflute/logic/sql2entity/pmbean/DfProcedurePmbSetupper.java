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
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTypeArrayInfo;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTypeStructInfo;
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

            boolean refCustomizeEntity = false;

            // Procedure Parameter handling
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

                // procedure's overload is unsupported because of this (override property) 
                propertyNameColumnInfoMap.put(propertyName, column);

                final ProcedurePropertyInfo propertyInfo = processProcedureProperty(pmbName, column, propertyName);
                final String propertyType = propertyInfo.getPropertyType();
                if (propertyInfo.isRefCustomizeEntity()) {
                    refCustomizeEntity = true;
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

            // NotParamResult handling
            for (DfProcedureNotParamResultMetaInfo result : notParamResultList) {
                final String propertyName = result.getPropertyName();
                final String propertyType;
                if (result.hasResultSetColumnInfo()) {
                    final String entityName = convertProcedurePmbNameToEntityName(pmbName, propertyName);
                    _entityInfoMap.put(entityName, result.getResultSetColumnInfoMap());
                    propertyType = convertProcedureListPropertyType(entityName);
                    refCustomizeEntity = true;
                } else {
                    propertyType = getProcedureDefaultResultSetPropertyType();
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
            parameterBeanMetaData.setRefCustomizeEntity(refCustomizeEntity);
            _pmbMetaDataMap.put(pmbName, parameterBeanMetaData);
        }
        _log.info("= = = = = = = = = =/");
        _log.info(" ");
    }

    // ===================================================================================
    //                                                                      Procedure List
    //                                                                      ==============
    protected List<DfProcedureMetaInfo> getAvailableProcedureList() throws SQLException {
        _procedureHandler.includeProcedureSynonym(_dataSource);
        final List<DfProcedureMetaInfo> procedureList = _procedureHandler.getAvailableProcedureList(_dataSource);
        if (getOutsideSqlProperties().isGenerateProcedureCustomizeEntity()) {
            final DfProcedureExecutionMetaExtractor executionMetaHandler = new DfProcedureExecutionMetaExtractor();
            executionMetaHandler.extractExecutionMetaData(_dataSource, procedureList);
        }
        return procedureList;
    }

    // ===================================================================================
    //                                                                       Property Type
    //                                                                       =============
    protected ProcedurePropertyInfo processProcedureProperty(String pmbName, DfProcedureColumnMetaInfo column,
            String propertyName) {
        final ProcedurePropertyInfo processInfo = new ProcedurePropertyInfo();
        processInfo.setColumnInfo(column);
        if (isResultSetProperty(column)) {
            if (column.hasResultSetColumnInfo()) {
                final String entityName = convertProcedurePmbNameToEntityName(pmbName, propertyName);
                _entityInfoMap.put(entityName, column.getResultSetColumnInfoMap());
                processInfo.setPropertyType(convertProcedureListPropertyType(entityName));
                processInfo.setRefCustomizeEntity(true);
            } else {
                processInfo.setPropertyType(getProcedureDefaultResultSetPropertyType());
            }
            return processInfo;
        }
        final int jdbcDefType = column.getJdbcDefType();
        final Integer columnSize = column.getColumnSize();
        final Integer decimalDigits = column.getDecimalDigits();
        if (column.isOracleNumber()) {
            // because the length setting of procedure parameter is unsupported on Oracle
            processInfo.setPropertyType(TypeMap.getDefaultDecimalJavaNativeType());
            return processInfo;
        }
        if (column.isOracleTreatedAsArray() && column.hasTypeArrayElementType()) {
            // here dbTypeName is "PL/SQL TABLE" or "TABLE" or "VARRAY" (it's not useful for type mapping)
            final DfTypeArrayInfo arrayInfo = column.getTypeArrayInfo();
            if (arrayInfo.hasStructInfo()) {
                final DfTypeStructInfo structInfo = arrayInfo.getStructInfo();
                doProcessStructProperty(column, structInfo, processInfo);
            } else {
                final String elementType = arrayInfo.getElementType();
                final String propertyType = findPlainPropertyType(jdbcDefType, elementType, columnSize, decimalDigits);
                processInfo.setPropertyType(getGenericListClassName(propertyType));
            }
            return processInfo;
        }
        if (column.isOracleStruct() && column.hasTypeStructInfo()) {
            final DfTypeStructInfo structInfo = column.getTypeStructInfo();
            doProcessStructProperty(column, structInfo, processInfo);
        }
        final String dbTypeName = column.getDbTypeName();
        processInfo.setPropertyType(findPlainPropertyType(jdbcDefType, dbTypeName, columnSize, decimalDigits));
        return processInfo;
    }

    protected boolean isResultSetProperty(DfProcedureColumnMetaInfo column) {
        if (column.hasResultSetColumnInfo()) {
            return true;
        }
        return column.isPostgreSQLCursor() || column.isOracleCursor();
    }

    protected void doProcessStructProperty(DfProcedureColumnMetaInfo column, DfTypeStructInfo structInfo,
            ProcedurePropertyInfo processInfo) {
        final String entityName = convertStructNameToEntityName(structInfo);
        if (!_entityInfoMap.containsKey(entityName)) { // because STRUCTs are independent objects
            _entityInfoMap.put(entityName, structInfo.getAttributeInfoMap());
        }
        processInfo.setPropertyType(getGenericListClassName(entityName));
        processInfo.setRefCustomizeEntity(true);
    }

    protected String getProcedureDefaultResultSetPropertyType() {
        final DfGrammarInfo grammarInfo = getBasicProperties().getLanguageDependencyInfo().getGrammarInfo();
        return grammarInfo.getGenericMapListClassName("String", "Object"); // Map<String, Object>
    }

    protected String findPlainPropertyType(int jdbcDefType, String dbTypeName, Integer columnSize, Integer decimalDigits) {
        if (_columnHandler.hasMappingJdbcType(jdbcDefType, dbTypeName)) {
            final String torqueType = _columnHandler.getColumnJdbcType(jdbcDefType, dbTypeName);
            return TypeMap.findJavaNativeByJdbcType(torqueType, columnSize, decimalDigits);
        } else {
            return "Object"; // procedure has many-many types so it uses Object type (not String)
        }
    }

    protected String getGenericListClassName(String element) {
        final DfGrammarInfo grammarInfo = getBasicProperties().getLanguageDependencyInfo().getGrammarInfo();
        return grammarInfo.getGenericListClassName(element); // List<ELEMENT>
    }

    protected static class ProcedurePropertyInfo {
        protected DfProcedureColumnMetaInfo _columnInfo;
        protected String _propertyType;
        protected boolean _refCustomizeEntity;

        public DfProcedureColumnMetaInfo getColumnInfo() {
            return _columnInfo;
        }

        public void setColumnInfo(DfProcedureColumnMetaInfo columnInfo) {
            _columnInfo = columnInfo;
        }

        public String getPropertyType() {
            return _propertyType;
        }

        public void setPropertyType(String propertyType) {
            _propertyType = propertyType;
        }

        public boolean isRefCustomizeEntity() {
            return _refCustomizeEntity;
        }

        public void setRefCustomizeEntity(boolean refCustomizeEntity) {
            _refCustomizeEntity = refCustomizeEntity;
        }
    }

    // ===================================================================================
    //                                                                        Convert Name
    //                                                                        ============
    protected String convertProcedureNameToPmbName(String procedureName) {
        final String projectPrefix = getBasicProperties().getProjectPrefix();
        procedureName = Srl.replace(procedureName, ".", "_");
        procedureName = resolveVendorProcedureNameHeadache(procedureName);
        return projectPrefix + Srl.camelize(procedureName) + "Pmb";
    }

    protected String resolveVendorProcedureNameHeadache(String procedureName) {
        if (getBasicProperties().isDatabaseSQLServer()) { // SQLServer returns 'sp_foo;1'
            procedureName = Srl.substringLastFront(procedureName, ";");
        }
        return procedureName;
    }

    protected String convertProcedurePmbNameToEntityName(String pmbName, String propertyName) {
        final String baseName = Srl.substringLastFront(pmbName, "Pmb");
        return baseName + Srl.initCap(propertyName);
    }

    protected String convertStructNameToEntityName(DfTypeStructInfo structInfo) {
        final String projectPrefix = getBasicProperties().getProjectPrefix();
        final String typeName = structInfo.getTypeName();
        return projectPrefix + "Struct" + Srl.camelize(typeName);
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

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBasicProperties getBasicProperties() {
        return DfBuildProperties.getInstance().getBasicProperties();
    }

    protected DfOutsideSqlProperties getOutsideSqlProperties() {
        return DfBuildProperties.getInstance().getOutsideSqlProperties();
    }
}
