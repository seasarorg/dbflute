package org.seasar.dbflute.logic.factory;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.seasar.dbflute.helper.StringSet;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.DfSynonymExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.DfSynonymExtractorOracle;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;

/**
 * @author jflute
 * @since 0.9.3 (2009/02/24 Tuesday)
 */
public class DfSynonymExtractorFactory {

    protected DataSource _dataSource;
    protected DfBasicProperties _basicProperties;
    protected DfDatabaseProperties _databaseProperties;
    protected StringSet _refTableCheckSet;

    /**
     * @param dataSource The data source. (NotNull)
     * @param basicProperties The basic properties. (NotNull)
     * @param databaseProperties The database properties. (NotNull)
     * @param refTableCheckSet The set for checking reference tables. (NotNull)
     */
    public DfSynonymExtractorFactory(DataSource dataSource, DfBasicProperties basicProperties,
            DfDatabaseProperties databaseProperties, StringSet refTableCheckSet) {
        _dataSource = dataSource;
        _basicProperties = basicProperties;
        _databaseProperties = databaseProperties;
        _refTableCheckSet = refTableCheckSet;
    }

    /**
     * @return The extractor of DB comments. (Nullable)
     */
    public DfSynonymExtractor createSynonymExtractor() {
        if (_basicProperties.isDatabaseOracle() && _databaseProperties.hasObjectTypeSynonym()) {
            final DfSynonymExtractorOracle extractor = new DfSynonymExtractorOracle();
            extractor.setDataSource(_dataSource);
            extractor.setSchemaList(createAllSchemaList());
            extractor.setRefTableCheckSet(_refTableCheckSet);
            return extractor;
        }
        return null;
    }

    protected List<String> createAllSchemaList() { // not only main schema but also additional schemas
        final String mainSchema = _databaseProperties.getDatabaseSchema();
        final List<String> schemaList = new ArrayList<String>();
        schemaList.add(mainSchema);
        schemaList.addAll(_databaseProperties.getAdditionalSchemaMap().keySet());
        return schemaList;
    }
}
