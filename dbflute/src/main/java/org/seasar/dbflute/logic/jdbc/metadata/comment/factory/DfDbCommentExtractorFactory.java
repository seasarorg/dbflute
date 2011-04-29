package org.seasar.dbflute.logic.jdbc.metadata.comment.factory;

import javax.sql.DataSource;

import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.logic.jdbc.metadata.comment.DfDbCommentExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.comment.DfDbCommentExtractorMySQL;
import org.seasar.dbflute.logic.jdbc.metadata.comment.DfDbCommentExtractorOracle;
import org.seasar.dbflute.logic.jdbc.metadata.comment.DfDbCommentExtractorSQLServer;
import org.seasar.dbflute.properties.facade.DfDatabaseTypeFacadeProp;

/**
 * @author jflute
 * @since 0.8.1 (2008/10/10 Friday)
 */
public class DfDbCommentExtractorFactory {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DataSource _dataSource;
    protected final UnifiedSchema _unifiedSchema;
    protected final DfDatabaseTypeFacadeProp _databaseTypeFacadeProp;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param dataSource The data source. (NotNull)
     * @param unifiedSchema The unified schema to extract. (NullAllowed)
     * @param databaseTypeFacadeProp The facade properties for database type. (NotNull)
     */
    public DfDbCommentExtractorFactory(DataSource dataSource, UnifiedSchema unifiedSchema,
            DfDatabaseTypeFacadeProp databaseTypeFacadeProp) {
        _dataSource = dataSource;
        _unifiedSchema = unifiedSchema;
        _databaseTypeFacadeProp = databaseTypeFacadeProp;
    }

    // ===================================================================================
    //                                                                              Create
    //                                                                              ======
    /**
     * @return The extractor of DB comments. (NullAllowed)
     */
    public DfDbCommentExtractor createDbCommentExtractor() {
        if (_databaseTypeFacadeProp.isDatabaseMySQL()) {
            final DfDbCommentExtractorMySQL extractor = new DfDbCommentExtractorMySQL();
            extractor.setDataSource(_dataSource);
            extractor.setUnifiedSchema(_unifiedSchema);
            return extractor;
        } else if (_databaseTypeFacadeProp.isDatabaseOracle()) {
            final DfDbCommentExtractorOracle extractor = new DfDbCommentExtractorOracle();
            extractor.setDataSource(_dataSource);
            extractor.setUnifiedSchema(_unifiedSchema);
            return extractor;
        } else if (_databaseTypeFacadeProp.isDatabaseSQLServer()) {
            final DfDbCommentExtractorSQLServer extractor = new DfDbCommentExtractorSQLServer();
            extractor.setDataSource(_dataSource);
            extractor.setUnifiedSchema(_unifiedSchema);
            return extractor;
        }
        return null;
    }
}
