package org.seasar.dbflute.logic.factory;

import javax.sql.DataSource;

import org.seasar.dbflute.logic.metadata.comment.DfDbCommentExtractor;
import org.seasar.dbflute.logic.metadata.comment.DfDbCommentExtractorOracle;
import org.seasar.dbflute.properties.DfBasicProperties;

/**
 * @author jflute
 * @since 0.8.1 (2008/10/10 Friday)
 */
public class DfDbCommentExtractorFactory {

    protected DfBasicProperties _basicProperties;
    protected DataSource _dataSource;
    protected String _schema;

    /**
     * @param basicProperties The basic properties. (NotNull)
     * @param dataSource The data source. (NotNull)
     * @param schema The schema to extract. (NotNull)
     */
    public DfDbCommentExtractorFactory(DfBasicProperties basicProperties, DataSource dataSource, String schema) {
        _basicProperties = basicProperties;
        _dataSource = dataSource;
        _schema = schema;
    }

    /**
     * @return The extractor of DB comments. (Nullable)
     */
    public DfDbCommentExtractor createDbCommentExtractor() {
        if (_basicProperties.isDatabaseOracle()) {
            final DfDbCommentExtractorOracle extractor = new DfDbCommentExtractorOracle();
            extractor.setDataSource(_dataSource);
            extractor.setSchema(_schema);
            return extractor;
        }
        return null;
    }
}
