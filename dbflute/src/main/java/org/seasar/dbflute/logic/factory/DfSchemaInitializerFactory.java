package org.seasar.dbflute.logic.factory;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializer;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializerDB2;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializerJdbc;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializerMySQL;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializerOracle;
import org.seasar.dbflute.helper.jdbc.schemainitializer.DfSchemaInitializerSqlServer;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;

/**
 * @author jflute
 */
public class DfSchemaInitializerFactory {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource _dataSource;
    protected DfBasicProperties _basicProperties;
    protected DfDatabaseProperties _databaseProperties;
    protected DfReplaceSchemaProperties _replaceSchemaProperties;
    protected InitializeType _initializeType;
    protected Map<String, Object> additionalDropMap;

    public enum InitializeType {
        FIRST, ONCE_MOCE, ADDTIONAL
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfSchemaInitializerFactory(DataSource dataSource, DfBasicProperties basicProperties,
            DfDatabaseProperties databaseProperties, DfReplaceSchemaProperties replaceSchemaProperties,
            InitializeType initializeType) {
        _dataSource = dataSource;
        _basicProperties = basicProperties;
        _databaseProperties = databaseProperties;
        _replaceSchemaProperties = replaceSchemaProperties;
        _initializeType = initializeType;
    }

    // ===================================================================================
    //                                                                              Create
    //                                                                              ======
    public DfSchemaInitializer createSchemaInitializer() {
        final DfSchemaInitializer initializer;
        if (_basicProperties.isDatabaseMySQL()) {
            initializer = createSchemaInitializerMySQL();
        } else if (_basicProperties.isDatabaseOracle()) {
            initializer = createSchemaInitializerOracle();
        } else if (_basicProperties.isDatabaseDB2()) {
            initializer = createSchemaInitializerDB2();
        } else if (_basicProperties.isDatabaseSqlServer()) {
            initializer = createSchemaInitializerSqlServer();
        } else {
            initializer = createSchemaInitializerJdbc();
        }
        return initializer;
    }

    protected DfSchemaInitializer createSchemaInitializerJdbc() {
        final DfSchemaInitializerJdbc initializer = new DfSchemaInitializerJdbc();
        setupSchemaInitializerJdbcProperties(initializer);
        return initializer;
    }

    protected DfSchemaInitializer createSchemaInitializerOracle() {
        final DfSchemaInitializerOracle initializer = new DfSchemaInitializerOracle();
        setupSchemaInitializerJdbcProperties(initializer);
        return initializer;
    }

    protected DfSchemaInitializer createSchemaInitializerDB2() {
        final DfSchemaInitializerDB2 initializer = new DfSchemaInitializerDB2();
        setupSchemaInitializerJdbcProperties(initializer);
        return initializer;
    }

    protected DfSchemaInitializer createSchemaInitializerMySQL() {
        final DfSchemaInitializerMySQL initializer = new DfSchemaInitializerMySQL();
        setupSchemaInitializerJdbcProperties(initializer);
        return initializer;
    }

    protected DfSchemaInitializer createSchemaInitializerSqlServer() {
        final DfSchemaInitializerSqlServer initializer = new DfSchemaInitializerSqlServer();
        setupSchemaInitializerJdbcProperties(initializer);
        return initializer;
    }

    protected void setupSchemaInitializerJdbcProperties(DfSchemaInitializerJdbc initializer) {
        initializer.setDataSource(_dataSource);
        setupDetailExecutionHandling(initializer);
        if (_initializeType.equals(InitializeType.FIRST)) { // Normal
            initializer.setSchema(_databaseProperties.getDatabaseSchema());
            initializer.setDropGenerateTableOnly(_replaceSchemaProperties.isDropGenerateTableOnly());
            return;
        }

        if (_initializeType.equals(InitializeType.ONCE_MOCE)) {
            // Here 'Once-More'!
            final String schema = getOnceMoreSchema();
            if (schema == null || schema.trim().length() == 0) {
                String msg = "Once More Drop Schema should not be null or empty: schema=" + schema;
                throw new IllegalStateException(msg);
            }
            initializer.setSchema(schema);
            initializer.setTableNameWithSchema(true); // because it may be other schema!
            initializer.setDropObjectTypeList(getOnceMoreObjectTypeList());
            initializer.setDropTableTargetList(getOnceMoreDropTableTargetList());
            initializer.setDropTableExceptList(getOnceMoreDropTableExceptList());
            initializer.setDropGenerateTableOnly(false);
        } else if (_initializeType.equals(InitializeType.ADDTIONAL)) {
            // Here 'Additional'!
            if (additionalDropMap == null) {
                String msg = "The additional drop map should exist if the initialize type is additional!";
                throw new IllegalStateException(msg);
            }
            final String schema = getAdditionalDropSchema(additionalDropMap);
            if (schema == null || schema.trim().length() == 0) {
                String msg = "Additional Drop Schema should not be null or empty: schema=" + schema;
                throw new IllegalStateException(msg);
            }
            initializer.setSchema(schema);
            initializer.setTableNameWithSchema(true); // because it may be other schema!
            initializer.setDropObjectTypeList(getAdditionalDropObjectTypeList(additionalDropMap));
            initializer.setDropTableTargetList(getAdditionalDropTableTargetList(additionalDropMap));
            initializer.setDropTableExceptList(getAdditionalDropTableExceptList(additionalDropMap));
            initializer.setDropGenerateTableOnly(false);
        } else {
            String msg = "Unknown initialize type: " + _initializeType;
            throw new IllegalStateException(msg);
        }
    }

    protected void setupDetailExecutionHandling(DfSchemaInitializerJdbc initializer) {
        initializer.setSuppressTruncateTable(_replaceSchemaProperties.isSuppressTruncateTable());
        initializer.setSuppressDropForeignKey(_replaceSchemaProperties.isSuppressDropForeignKey());
        initializer.setSuppressDropTable(_replaceSchemaProperties.isSuppressDropTable());
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    protected String getOnceMoreSchema() {
        return _replaceSchemaProperties.getOnceMoreDropDefinitionSchema();
    }

    protected List<String> getOnceMoreObjectTypeList() {
        return _replaceSchemaProperties.getOnceMoreDropObjectTypeList();
    }

    protected List<String> getOnceMoreDropTableTargetList() {
        return _replaceSchemaProperties.getOnceMoreDropTableTargetList();
    }

    protected List<String> getOnceMoreDropTableExceptList() {
        return _replaceSchemaProperties.getOnceMoreDropTableExceptList();
    }

    protected boolean isOnceMoreDropAllTable() {
        return _replaceSchemaProperties.isOnceMoreDropAllTable();
    }

    protected String getAdditionalDropSchema(Map<String, Object> map) {
        return _replaceSchemaProperties.getAdditionalDropSchema(map);
    }

    protected List<String> getAdditionalDropObjectTypeList(Map<String, Object> map) {
        return _replaceSchemaProperties.getAdditionalDropObjectTypeList(map);
    }

    protected List<String> getAdditionalDropTableTargetList(Map<String, Object> map) {
        return _replaceSchemaProperties.getAdditionalDropTableTargetList(map);
    }

    protected List<String> getAdditionalDropTableExceptList(Map<String, Object> map) {
        return _replaceSchemaProperties.getAdditionalDropTableExceptList(map);
    }

    protected boolean isAdditionalDropAllTable(Map<String, Object> map) {
        return _replaceSchemaProperties.isAdditionalDropAllTable(map);
    }

    public Map<String, Object> getAdditionalDropMap() {
        return additionalDropMap;
    }

    public void setAdditionalDropMap(Map<String, Object> additionalDropMap) {
        this.additionalDropMap = additionalDropMap;
    }
}
