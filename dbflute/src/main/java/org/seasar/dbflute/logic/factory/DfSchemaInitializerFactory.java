package org.seasar.dbflute.logic.factory;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.seasar.dbflute.logic.schemainitializer.DfSchemaInitializer;
import org.seasar.dbflute.logic.schemainitializer.DfSchemaInitializerDB2;
import org.seasar.dbflute.logic.schemainitializer.DfSchemaInitializerJdbc;
import org.seasar.dbflute.logic.schemainitializer.DfSchemaInitializerMySQL;
import org.seasar.dbflute.logic.schemainitializer.DfSchemaInitializerOracle;
import org.seasar.dbflute.logic.schemainitializer.DfSchemaInitializerSqlServer;
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
        FIRST, ADDTIONAL
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

        if (_initializeType.equals(InitializeType.ADDTIONAL)) {
            // Here 'Additional'!
            if (additionalDropMap == null) {
                String msg = "The additional drop map should exist if the initialize type is additional!";
                throw new IllegalStateException(msg);
            }
            final String schemaName = getAdditionalDropSchema(additionalDropMap);
            if (schemaName == null || schemaName.trim().length() == 0) {
                String msg = "Additional Drop Schema should not be null or empty: schema=" + schemaName;
                throw new IllegalStateException(msg);
            }
            initializer.setSchema(schemaName);
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
        initializer.setSuppressDropSequence(_replaceSchemaProperties.isSuppressDropSequence());
        initializer.setSuppressDropDBLink(_replaceSchemaProperties.isSuppressDropDBLink());
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
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
