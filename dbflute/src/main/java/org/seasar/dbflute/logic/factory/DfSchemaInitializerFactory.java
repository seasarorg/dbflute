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
    protected boolean _onceMore;

    public DfSchemaInitializerFactory(DataSource dataSource, DfBasicProperties basicProperties,
            DfDatabaseProperties databaseProperties, DfReplaceSchemaProperties replaceSchemaProperties,
            boolean onceMore) {
        _dataSource = dataSource;
        _basicProperties = basicProperties;
        _databaseProperties = databaseProperties;
        _replaceSchemaProperties = replaceSchemaProperties;
        _onceMore = onceMore;
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
        if (!_onceMore) { // Normal
            initializer.setSchema(_databaseProperties.getDatabaseSchema());
            return;
        }
        
        // Here 'Once-More'!
        final String schema = getOnceMoreSchema();
        if (schema == null || schema.trim().length() == 0) {
            String msg = "Once More Schema should not be null or empty: schema=" + schema;
            throw new IllegalStateException(msg);
        }
        final List<String> objectTypeList = getOnceMoreObjectTypeList();
        final List<String> tableTargetList = getOnceMoreDropTableTargetList();
        final List<String> tableExceptList = getOnceMoreDropTableExceptList();
        final boolean dropAllTable = isOnceMoreDropAllTable();
        initializer.setSchema(schema);
        initializer.setTableNameWithSchema(true);
        initializer.setObjectTypeList(objectTypeList);
        initializer.setTableTargetList(tableTargetList);
        initializer.setTableExceptList(tableExceptList);
        initializer.setDropAllTable(dropAllTable);
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
}
