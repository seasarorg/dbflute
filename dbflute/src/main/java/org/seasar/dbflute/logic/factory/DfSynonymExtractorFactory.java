package org.seasar.dbflute.logic.factory;

import javax.sql.DataSource;

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

    /**
     * @param basicProperties The basic properties. (NotNull)
     * @param dataSource The data source. (NotNull)
     */
    public DfSynonymExtractorFactory(DfBasicProperties basicProperties, DataSource dataSource) {
        _basicProperties = basicProperties;
        _dataSource = dataSource;
    }

    /**
     * @return The extractor of DB comments. (Nullable)
     */
    public DfSynonymExtractor createSynonymExtractor() {
        if (_basicProperties.isDatabaseOracle()) {
            final DfSynonymExtractorOracle extractor = new DfSynonymExtractorOracle();
            extractor.setDataSource(_dataSource);
            return extractor;
        }
        return null;
    }
}
