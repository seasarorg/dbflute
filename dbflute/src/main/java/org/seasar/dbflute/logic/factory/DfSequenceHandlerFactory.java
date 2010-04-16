package org.seasar.dbflute.logic.factory;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.seasar.dbflute.logic.jdbc.metadata.sequence.DfSequenceHandler;
import org.seasar.dbflute.logic.jdbc.metadata.sequence.DfSequenceHandlerDB2;
import org.seasar.dbflute.logic.jdbc.metadata.sequence.DfSequenceHandlerH2;
import org.seasar.dbflute.logic.jdbc.metadata.sequence.DfSequenceHandlerOracle;
import org.seasar.dbflute.logic.jdbc.metadata.sequence.DfSequenceHandlerPostgreSQL;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;

/**
 * @author jflute
 */
public class DfSequenceHandlerFactory {

    protected DataSource _dataSource;
    protected DfBasicProperties _basicProperties;
    protected DfDatabaseProperties _databaseProperties;

    public DfSequenceHandlerFactory(DataSource dataSource, DfBasicProperties basicProperties,
            DfDatabaseProperties databaseProperties) {
        _dataSource = dataSource;
        _basicProperties = basicProperties;
        _databaseProperties = databaseProperties;
    }

    public DfSequenceHandler createSequenceHandler() {
        final String mainSchema = _databaseProperties.getDatabaseSchema();
        final List<String> allSchemaList = createUniqueSchemaList();
        if (_basicProperties.isDatabasePostgreSQL()) {
            return new DfSequenceHandlerPostgreSQL(_dataSource, mainSchema, allSchemaList);
        } else if (_basicProperties.isDatabaseOracle()) {
            return new DfSequenceHandlerOracle(_dataSource, mainSchema, allSchemaList);
        } else if (_basicProperties.isDatabaseDB2()) {
            return new DfSequenceHandlerDB2(_dataSource, mainSchema, allSchemaList);
        } else if (_basicProperties.isDatabaseH2()) {
            return new DfSequenceHandlerH2(_dataSource, mainSchema, allSchemaList);
        }
        return null;
    }

    protected List<String> createUniqueSchemaList() { // not only main schema but also additional schemas
        final List<String> schemaList = new ArrayList<String>();
        final String mainSchema = _databaseProperties.getDatabaseSchema();
        if (mainSchema != null && mainSchema.trim().length() > 0) {
            schemaList.add(_databaseProperties.getDatabaseSchema());
        }
        schemaList.addAll(_databaseProperties.getAdditionalSchemaNameList());
        return schemaList;
    }
}
