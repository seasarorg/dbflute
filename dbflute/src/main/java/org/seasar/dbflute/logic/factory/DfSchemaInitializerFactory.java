package org.seasar.dbflute.logic.factory;

import java.util.List;

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

public class DfSchemaInitializerFactory {

    protected DataSource _dataSource;
    protected DfBasicProperties _basicProperties;
    protected DfDatabaseProperties _databaseProperties;
    protected DfReplaceSchemaProperties _replaceSchemaProperties;
    protected InitializeType _initializeType;

    public enum InitializeType {
        FIRST, ONCE_MOCE, ONE_MORE_TIME
    }

    public DfSchemaInitializerFactory(DataSource dataSource, DfBasicProperties basicProperties,
            DfDatabaseProperties databaseProperties, DfReplaceSchemaProperties replaceSchemaProperties,
            InitializeType initializeType) {
        _dataSource = dataSource;
        _basicProperties = basicProperties;
        _databaseProperties = databaseProperties;
        _replaceSchemaProperties = replaceSchemaProperties;
        _initializeType = initializeType;
    }

    public DfSchemaInitializer createSchemaInitializer() {
        final DfSchemaInitializer initializer;
        if (_basicProperties.isDatabaseMySQL()) {
            initializer = createSchemaInitializerMySQL();
        } else if (_basicProperties.isDatabaseSqlServer()) {
            initializer = createSchemaInitializerSqlServer();
        } else if (_basicProperties.isDatabaseOracle()) {
            initializer = createSchemaInitializerOracle();
        } else if (_basicProperties.isDatabaseDB2()) {
            initializer = createSchemaInitializerDB2();
        } else {
            initializer = createSchemaInitializerJdbc();
        }
        return initializer;
    }

    protected DfSchemaInitializer createSchemaInitializerMySQL() {
        final DfSchemaInitializerMySQL initializer = new DfSchemaInitializerMySQL();
        initializer.setDataSource(_dataSource);
        return initializer;
    }

    protected DfSchemaInitializer createSchemaInitializerSqlServer() {
        final DfSchemaInitializerSqlServer initializer = new DfSchemaInitializerSqlServer();
        initializer.setDataSource(_dataSource);
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

    protected DfSchemaInitializer createSchemaInitializerJdbc() {
        final DfSchemaInitializerJdbc initializer = new DfSchemaInitializerJdbc();
        setupSchemaInitializerJdbcProperties(initializer);
        return initializer;
    }

    protected void setupSchemaInitializerJdbcProperties(DfSchemaInitializerJdbc initializer) {
        initializer.setDataSource(_dataSource);
        if (_initializeType.equals(InitializeType.FIRST)) { // Normal
            initializer.setSchema(_databaseProperties.getDatabaseSchema());
            return;
        }

        if (_initializeType.equals(InitializeType.ONCE_MOCE)) {
            // Here 'Once-More'!
            final String schema = getOnceMoreSchema();
            if (schema == null || schema.trim().length() == 0) {
                String msg = "Once More Schema should not be null or empty: schema=" + schema;
                throw new IllegalStateException(msg);
            }
            initializer.setSchema(schema);
            initializer.setTableNameWithSchema(true); // because it may be other schema!
            initializer.setOnceMoreDropObjectTypeList(getOnceMoreObjectTypeList());
            initializer.setOnceMoreDropTableTargetList(getOnceMoreDropTableTargetList());
            initializer.setOnceMoreDropTableExceptList(getOnceMoreDropTableExceptList());
            initializer.setOnceMoreDropDropAllTable(isOnceMoreDropAllTable());
        } else if (_initializeType.equals(InitializeType.ONE_MORE_TIME)) {
            // Here 'One-More-Time'!
            final String schema = getOneMoreTimeSchema();
            if (schema == null || schema.trim().length() == 0) {
                String msg = "One More Time Schema should not be null or empty: schema=" + schema;
                throw new IllegalStateException(msg);
            }
            initializer.setSchema(schema);
            initializer.setTableNameWithSchema(true); // because it may be other schema!
            initializer.setOnceMoreDropObjectTypeList(getOneMoreTimeObjectTypeList());
            initializer.setOnceMoreDropTableTargetList(getOneMoreTimeDropTableTargetList());
            initializer.setOnceMoreDropTableExceptList(getOneMoreTimeDropTableExceptList());
            initializer.setOnceMoreDropDropAllTable(isOneMoreTimeDropAllTable());
        } else {
            String msg = "Unknown initialize type: " + _initializeType;
            throw new IllegalStateException(msg);
        }
    }

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

    protected String getOneMoreTimeSchema() {
        return _replaceSchemaProperties.getOneMoreTimeDropDefinitionSchema();
    }

    protected List<String> getOneMoreTimeObjectTypeList() {
        return _replaceSchemaProperties.getOneMoreTimeDropObjectTypeList();
    }

    protected List<String> getOneMoreTimeDropTableTargetList() {
        return _replaceSchemaProperties.getOneMoreTimeDropTableTargetList();
    }

    protected List<String> getOneMoreTimeDropTableExceptList() {
        return _replaceSchemaProperties.getOneMoreTimeDropTableExceptList();
    }

    protected boolean isOneMoreTimeDropAllTable() {
        return _replaceSchemaProperties.isOneMoreTimeDropAllTable();
    }
}
