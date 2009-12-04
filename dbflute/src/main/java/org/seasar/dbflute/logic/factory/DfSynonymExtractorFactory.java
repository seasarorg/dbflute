package org.seasar.dbflute.logic.factory;

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

    protected DfBasicProperties _basicProperties;
    protected DfDatabaseProperties _databaseProperties;
    protected DataSource _dataSource;
    protected String _schema;
    protected StringSet _refTableCheckSet;

    /**
     * @param basicProperties The basic properties. (NotNull)
     * @param databaseProperties The database properties. (NotNull)
     * @param dataSource The data source. (NotNull)
     * @param schema The schema to extract. (NotNull)
     */
    public DfSynonymExtractorFactory(DfBasicProperties basicProperties, DfDatabaseProperties databaseProperties,
            DataSource dataSource, String schema, StringSet refTableCheckSet) {
        _basicProperties = basicProperties;
        _databaseProperties = databaseProperties;
        _schema = schema;
        _dataSource = dataSource;
        _refTableCheckSet = refTableCheckSet;
    }

    /**
     * @return The extractor of DB comments. (Nullable)
     */
    public DfSynonymExtractor createSynonymExtractor() {
        if (_basicProperties.isDatabaseOracle() && _databaseProperties.hasObjectTypeSynonym()) {
            final DfSynonymExtractorOracle extractor = new DfSynonymExtractorOracle();
            extractor.setDataSource(_dataSource);
            extractor.setSchema(_schema);
            extractor.setRefTableCheckSet(_refTableCheckSet);
            return extractor;
        }
        return null;
    }
}
