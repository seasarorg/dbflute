package org.seasar.dbflute.logic.jdbc.metadata.synonym.factory;

import java.util.List;

import javax.sql.DataSource;

import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.DfProcedureSynonymExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.DfProcedureSynonymExtractorOracle;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.facade.DfDatabaseTypeFacadeProp;

/**
 * @author jflute
 * @since 0.9.6.2 (2009/12/08 Tuesday)
 */
public class DfProcedureSynonymExtractorFactory {

    protected DataSource _dataSource;
    protected DfDatabaseTypeFacadeProp _databaseTypeFacadeProp;
    protected DfDatabaseProperties _databaseProperties;

    /**
     * @param dataSource The data source. (NotNull)
     * @param databaseTypeFacadeProp The facade properties for database type. (NotNull)
     * @param databaseProperties The database properties. (NotNull)
     */
    public DfProcedureSynonymExtractorFactory(DataSource dataSource, DfDatabaseTypeFacadeProp databaseTypeFacadeProp,
            DfDatabaseProperties databaseProperties) {
        _databaseTypeFacadeProp = databaseTypeFacadeProp;
        _dataSource = dataSource;
        _databaseProperties = databaseProperties;
    }

    /**
     * @return The extractor of DB comments. (NullAllowed)
     */
    public DfProcedureSynonymExtractor createSynonymExtractor() {
        if (_databaseTypeFacadeProp.isDatabaseOracle()) {
            final DfProcedureSynonymExtractorOracle extractor = new DfProcedureSynonymExtractorOracle();
            extractor.setDataSource(_dataSource);
            extractor.setTargetSchemaList(createTargetSchemaList());
            return extractor;
        }
        return null;
    }

    protected List<UnifiedSchema> createTargetSchemaList() { // not only main schema but also additional schemas
        return _databaseProperties.getTargetSchemaList();
    }
}
