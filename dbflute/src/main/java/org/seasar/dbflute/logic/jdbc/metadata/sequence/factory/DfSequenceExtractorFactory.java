package org.seasar.dbflute.logic.jdbc.metadata.sequence.factory;

import java.util.List;

import javax.sql.DataSource;

import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.logic.jdbc.metadata.sequence.DfSequenceExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.sequence.DfSequenceExtractorDB2;
import org.seasar.dbflute.logic.jdbc.metadata.sequence.DfSequenceExtractorH2;
import org.seasar.dbflute.logic.jdbc.metadata.sequence.DfSequenceExtractorOracle;
import org.seasar.dbflute.logic.jdbc.metadata.sequence.DfSequenceExtractorPostgreSQL;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.facade.DfDatabaseTypeFacadeProp;
import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 */
public class DfSequenceExtractorFactory {

    protected final DataSource _dataSource;
    protected final DfDatabaseTypeFacadeProp _databaseTypeFacadeProp;
    protected final DfDatabaseProperties _databaseProperties;
    protected boolean _suppressAdditionalSchema;

    public DfSequenceExtractorFactory(DataSource dataSource, DfDatabaseTypeFacadeProp databaseTypeFacadeProp,
            DfDatabaseProperties databaseProperties) {
        _dataSource = dataSource;
        _databaseTypeFacadeProp = databaseTypeFacadeProp;
        _databaseProperties = databaseProperties;
    }

    public DfSequenceExtractor createSequenceExtractor() {
        final List<UnifiedSchema> targetSchemaList = createTargetSchemaList();
        if (_databaseTypeFacadeProp.isDatabasePostgreSQL()) {
            return new DfSequenceExtractorPostgreSQL(_dataSource, targetSchemaList);
        } else if (_databaseTypeFacadeProp.isDatabaseOracle()) {
            return new DfSequenceExtractorOracle(_dataSource, targetSchemaList);
        } else if (_databaseTypeFacadeProp.isDatabaseDB2()) {
            return new DfSequenceExtractorDB2(_dataSource, targetSchemaList);
        } else if (_databaseTypeFacadeProp.isDatabaseH2()) {
            return new DfSequenceExtractorH2(_dataSource, targetSchemaList);
        }
        return null;
    }

    protected List<UnifiedSchema> createTargetSchemaList() { // not only main schema but also additional schemas
        final List<UnifiedSchema> schemaList;
        if (_suppressAdditionalSchema) {
            schemaList = DfCollectionUtil.newArrayList(_databaseProperties.getDatabaseSchema());
        } else {
            schemaList = _databaseProperties.getTargetSchemaList();
        }
        return schemaList;
    }

    public void suppressAdditionalSchema() {
        _suppressAdditionalSchema = true;
    }
}
