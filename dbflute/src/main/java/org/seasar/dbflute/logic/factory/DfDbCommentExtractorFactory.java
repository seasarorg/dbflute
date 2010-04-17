package org.seasar.dbflute.logic.factory;

import javax.sql.DataSource;

import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.logic.jdbc.metadata.comment.DfDbCommentExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.comment.DfDbCommentExtractorMySql;
import org.seasar.dbflute.logic.jdbc.metadata.comment.DfDbCommentExtractorOracle;
import org.seasar.dbflute.logic.jdbc.metadata.comment.DfDbCommentExtractorSqlServer;
import org.seasar.dbflute.properties.DfBasicProperties;

/**
 * @author jflute
 * @since 0.8.1 (2008/10/10 Friday)
 */
public class DfDbCommentExtractorFactory {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DfBasicProperties _basicProperties;
    protected DataSource _dataSource;
    protected UnifiedSchema _unifiedSchema;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param basicProperties The basic properties. (NotNull)
     * @param dataSource The data source. (NotNull)
     * @param unifiedSchema The unified schema to extract. (Nullable)
     */
    public DfDbCommentExtractorFactory(DfBasicProperties basicProperties, DataSource dataSource,
            UnifiedSchema unifiedSchema) {
        _basicProperties = basicProperties;
        _dataSource = dataSource;
        _unifiedSchema = unifiedSchema;
    }

    // ===================================================================================
    //                                                                              Create
    //                                                                              ======
    /**
     * @return The extractor of DB comments. (Nullable)
     */
    public DfDbCommentExtractor createDbCommentExtractor() {
        if (_basicProperties.isDatabaseMySQL()) {
            final DfDbCommentExtractorMySql extractor = new DfDbCommentExtractorMySql();
            extractor.setDataSource(_dataSource);
            extractor.setUnifiedSchema(_unifiedSchema);
            return extractor;
        } else if (_basicProperties.isDatabaseOracle()) {
            final DfDbCommentExtractorOracle extractor = new DfDbCommentExtractorOracle();
            extractor.setDataSource(_dataSource);
            extractor.setUnifiedSchema(_unifiedSchema);
            return extractor;
        } else if (_basicProperties.isDatabaseSQLServer()) {
            final DfDbCommentExtractorSqlServer extractor = new DfDbCommentExtractorSqlServer();
            extractor.setDataSource(_dataSource);
            extractor.setUnifiedSchema(_unifiedSchema);
            return extractor;
        }
        return null;
    }
}
