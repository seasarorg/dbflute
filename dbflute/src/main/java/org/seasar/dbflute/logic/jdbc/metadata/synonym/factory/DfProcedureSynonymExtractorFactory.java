package org.seasar.dbflute.logic.jdbc.metadata.synonym.factory;

import java.util.List;

import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.helper.jdbc.context.DfSchemaSource;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.DfProcedureSynonymExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.DfProcedureSynonymExtractorOracle;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.facade.DfDatabaseTypeFacadeProp;
import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 0.9.6.2 (2009/12/08 Tuesday)
 */
public class DfProcedureSynonymExtractorFactory {

    protected final DfSchemaSource _dataSource;
    protected final DfDatabaseTypeFacadeProp _databaseTypeFacadeProp;
    protected final DfDatabaseProperties _databaseProperties;

    /**
     * @param dataSource The data source. (NotNull)
     * @param databaseTypeFacadeProp The facade properties for database type. (NotNull)
     * @param databaseProperties The database properties. (NotNull)
     */
    public DfProcedureSynonymExtractorFactory(DfSchemaSource dataSource,
            DfDatabaseTypeFacadeProp databaseTypeFacadeProp, DfDatabaseProperties databaseProperties) {
        _dataSource = dataSource;
        _databaseTypeFacadeProp = databaseTypeFacadeProp;
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
        final List<UnifiedSchema> schemaList = DfCollectionUtil.newArrayList(_dataSource.getSchema());
        schemaList.addAll(_databaseProperties.getAdditionalSchemaList());
        return schemaList;
    }
}
