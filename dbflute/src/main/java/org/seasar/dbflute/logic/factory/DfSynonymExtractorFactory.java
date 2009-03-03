package org.seasar.dbflute.logic.factory;

import javax.sql.DataSource;

import org.seasar.dbflute.helper.collection.DfStringSet;
import org.seasar.dbflute.helper.jdbc.metadata.synonym.DfSynonymExtractor;
import org.seasar.dbflute.helper.jdbc.metadata.synonym.DfSynonymExtractorOracle;
import org.seasar.dbflute.properties.DfBasicProperties;

/**
 * @author jflute
 * @since 0.9.3 (2009/02/24 Tuesday)
 */
public class DfSynonymExtractorFactory {

    protected DfBasicProperties _basicProperties;
    protected DataSource _dataSource;
    protected String _schema;
    protected DfStringSet _refTableCheckSet;

    /**
     * @param basicProperties The basic properties. (NotNull)
     * @param dataSource The data source. (NotNull)
     * @param schema The schema to extract. (NotNull)
     */
    public DfSynonymExtractorFactory(DfBasicProperties basicProperties, DataSource dataSource, String schema,
            DfStringSet refTableCheckSet) {
        _basicProperties = basicProperties;
        _schema = schema;
        _dataSource = dataSource;
        _refTableCheckSet = refTableCheckSet;
    }

    /**
     * @return The extractor of DB comments. (Nullable)
     */
    public DfSynonymExtractor createSynonymExtractor() {
        if (_basicProperties.isDatabaseOracle()) {
            final DfSynonymExtractorOracle extractor = new DfSynonymExtractorOracle();
            extractor.setDataSource(_dataSource);
            extractor.setSchema(_schema);
            extractor.setRefTableCheckSet(_refTableCheckSet);
            return extractor;
        }
        return null;
    }
}
