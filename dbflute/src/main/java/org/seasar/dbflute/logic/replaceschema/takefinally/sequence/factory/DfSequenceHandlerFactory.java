package org.seasar.dbflute.logic.replaceschema.takefinally.sequence.factory;

import java.util.List;

import javax.sql.DataSource;

import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.logic.replaceschema.takefinally.sequence.DfSequenceHandler;
import org.seasar.dbflute.logic.replaceschema.takefinally.sequence.DfSequenceHandlerDB2;
import org.seasar.dbflute.logic.replaceschema.takefinally.sequence.DfSequenceHandlerH2;
import org.seasar.dbflute.logic.replaceschema.takefinally.sequence.DfSequenceHandlerOracle;
import org.seasar.dbflute.logic.replaceschema.takefinally.sequence.DfSequenceHandlerPostgreSQL;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.facade.DfDatabaseTypeFacadeProp;

/**
 * @author jflute
 */
public class DfSequenceHandlerFactory {

    protected DataSource _dataSource;
    protected DfDatabaseTypeFacadeProp _databaseTypeFacadeProp;
    protected DfDatabaseProperties _databaseProperties;

    public DfSequenceHandlerFactory(DataSource dataSource, DfDatabaseTypeFacadeProp databaseTypeFacadeProp,
            DfDatabaseProperties databaseProperties) {
        _dataSource = dataSource;
        _databaseTypeFacadeProp = databaseTypeFacadeProp;
        _databaseProperties = databaseProperties;
    }

    public DfSequenceHandler createSequenceHandler() {
        final List<UnifiedSchema> targetSchemaList = createTargetSchemaList();
        if (_databaseTypeFacadeProp.isDatabasePostgreSQL()) {
            return new DfSequenceHandlerPostgreSQL(_dataSource, targetSchemaList);
        } else if (_databaseTypeFacadeProp.isDatabaseOracle()) {
            return new DfSequenceHandlerOracle(_dataSource, targetSchemaList);
        } else if (_databaseTypeFacadeProp.isDatabaseDB2()) {
            return new DfSequenceHandlerDB2(_dataSource, targetSchemaList);
        } else if (_databaseTypeFacadeProp.isDatabaseH2()) {
            return new DfSequenceHandlerH2(_dataSource, targetSchemaList);
        }
        return null;
    }

    protected List<UnifiedSchema> createTargetSchemaList() { // not only main schema but also additional schemas
        return _databaseProperties.getTargetSchemaList();
    }
}
