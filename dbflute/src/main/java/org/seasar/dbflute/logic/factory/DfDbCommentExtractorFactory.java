package org.seasar.dbflute.logic.factory;

import javax.sql.DataSource;

import org.seasar.dbflute.helper.jdbc.metadata.comment.DfDbCommentExtractor;
import org.seasar.dbflute.helper.jdbc.metadata.comment.DfDbCommentExtractorOracle;
import org.seasar.dbflute.properties.DfBasicProperties;

/**
 * @author jflute
 * @since 0.8.1 (2008/10/10 Friday)
 */
public class DfDbCommentExtractorFactory {

    protected DfBasicProperties _basicProperties;
    protected DataSource _dataSource;

    /**
     * @param basicProperties The basic properties. (NotNull)
     * @param dataSource The data source. (NotNull)
     */
    public DfDbCommentExtractorFactory(DfBasicProperties basicProperties, DataSource dataSource) {
        _basicProperties = basicProperties;
        _dataSource = dataSource;
    }

    /**
     * @return The extractor of DB comments. (Nullable)
     */
    public DfDbCommentExtractor createDbCommentExtractor() {
        if (_basicProperties.isDatabaseOracle()) {
            final DfDbCommentExtractorOracle extractor = new DfDbCommentExtractorOracle();
            extractor.setDataSource(_dataSource);
            return extractor;
        }
        return null;
    }
}
