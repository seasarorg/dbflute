package org.seasar.dbflute.logic.factory;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

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
        final List<String> allSchemaList = createAllSchemaList();
        if (_basicProperties.isDatabasePostgreSQL()) {
            return new DfSequenceExtractorPostgreSQL(_dataSource, allSchemaList);
        } else if (_basicProperties.isDatabaseOracle()) {
            return new DfSequenceExtractorOracle(_dataSource, allSchemaList);
        } else if (_basicProperties.isDatabaseDB2()) {
            return new DfSequenceExtractorDB2(_dataSource, allSchemaList);
        } else if (_basicProperties.isDatabaseH2()) {
            return new DfSequenceExtractorH2(_dataSource, allSchemaList);
        }
        return null;
    }

    protected List<String> createAllSchemaList() { // not only main schema but also additional schemas
        final List<String> schemaList = new ArrayList<String>();
        schemaList.add(_databaseProperties.getDatabaseSchema());
        schemaList.addAll(_databaseProperties.getAdditionalSchemaMap().keySet());
        return schemaList;
    }
}
