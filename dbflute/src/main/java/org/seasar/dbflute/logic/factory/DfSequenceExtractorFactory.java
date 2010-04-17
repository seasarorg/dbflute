package org.seasar.dbflute.logic.factory;

import java.util.List;

import javax.sql.DataSource;

import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.logic.jdbc.metadata.sequence.DfSequenceExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.sequence.DfSequenceExtractorDB2;
import org.seasar.dbflute.logic.jdbc.metadata.sequence.DfSequenceExtractorH2;
import org.seasar.dbflute.logic.jdbc.metadata.sequence.DfSequenceExtractorOracle;
import org.seasar.dbflute.logic.jdbc.metadata.sequence.DfSequenceExtractorPostgreSQL;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;

/**
 * @author jflute
 */
public class DfSequenceExtractorFactory {

    protected DataSource _dataSource;
    protected DfBasicProperties _basicProperties;
    protected DfDatabaseProperties _databaseProperties;

    public DfSequenceExtractorFactory(DataSource dataSource, DfBasicProperties basicProperties,
            DfDatabaseProperties databaseProperties) {
        _dataSource = dataSource;
        _basicProperties = basicProperties;
        _databaseProperties = databaseProperties;
    }

    public DfSequenceExtractor createSequenceExtractor() {
        final List<UnifiedSchema> targetSchemaList = createTargetSchemaList();
        if (_basicProperties.isDatabasePostgreSQL()) {
            return new DfSequenceExtractorPostgreSQL(_dataSource, targetSchemaList);
        } else if (_basicProperties.isDatabaseOracle()) {
            return new DfSequenceExtractorOracle(_dataSource, targetSchemaList);
        } else if (_basicProperties.isDatabaseDB2()) {
            return new DfSequenceExtractorDB2(_dataSource, targetSchemaList);
        } else if (_basicProperties.isDatabaseH2()) {
            return new DfSequenceExtractorH2(_dataSource, targetSchemaList);
        }
        return null;
    }

    protected List<UnifiedSchema> createTargetSchemaList() { // not only main schema but also additional schemas
        return _databaseProperties.getTargetSchemaList();
    }
}
