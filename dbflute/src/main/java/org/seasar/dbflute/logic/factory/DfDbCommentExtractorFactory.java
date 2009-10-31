package org.seasar.dbflute.logic.factory;

import javax.sql.DataSource;

import org.seasar.dbflute.helper.jdbc.metadata.comment.DfDbCommentExtractor;
import org.seasar.dbflute.helper.jdbc.metadata.comment.DfDbCommentExtractorMySql;
import org.seasar.dbflute.helper.jdbc.metadata.comment.DfDbCommentExtractorOracle;
import org.seasar.dbflute.helper.jdbc.metadata.comment.DfDbCommentExtractorSqlServer;
import org.seasar.dbflute.helper.jdbc.urlanalyzer.DfUrlAnalyzerMySql;
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
    protected String _url;
    protected String _schema;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param basicProperties The basic properties. (NotNull)
     * @param dataSource The data source. (NotNull)
     * @param url The url to extract. (Nullable)
     * @param schema The schema to extract. (Nullable)
     */
    public DfDbCommentExtractorFactory(DfBasicProperties basicProperties, DataSource dataSource, String url,
            String schema) {
        _basicProperties = basicProperties;
        _dataSource = dataSource;
        _url = url;
        _schema = schema;
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
            final String schema = extractSchemaFromMySqlUrl();
            if (schema == null || schema.trim().length() == 0) {
                return null;
            }
            extractor.setSchema(schema);
            return extractor;
        } else if (_basicProperties.isDatabaseOracle()) {
            final DfDbCommentExtractorOracle extractor = new DfDbCommentExtractorOracle();
            extractor.setDataSource(_dataSource);
            extractor.setSchema(_schema);
            return extractor;
        } else if (_basicProperties.isDatabaseSqlServer()) {
            final DfDbCommentExtractorSqlServer extractor = new DfDbCommentExtractorSqlServer();
            extractor.setDataSource(_dataSource);
            extractor.setSchema(_schema);
            return extractor;
        }
        return null;
    }

    protected String extractSchemaFromMySqlUrl() {
        final DfUrlAnalyzerMySql analyzer = new DfUrlAnalyzerMySql();
        analyzer.setUrl(_url);
        return analyzer.extractSchema();
    }
}
