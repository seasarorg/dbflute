package org.seasar.dbflute.logic.jdbc.metadata.synonym.factory;

import java.util.List;
import java.util.Map;

import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.helper.jdbc.context.DfSchemaSource;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMeta;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.DfSynonymExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.DfSynonymExtractorOracle;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.facade.DfDatabaseTypeFacadeProp;
import org.seasar.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 0.9.3 (2009/02/24 Tuesday)
 */
public class DfSynonymExtractorFactory {

    protected DfSchemaSource _dataSource;
    protected DfDatabaseTypeFacadeProp _databaseTypeFacadeProp;
    protected DfDatabaseProperties _databaseProperties;
    protected Map<String, DfTableMeta> _generatedTableMap;

    /**
     * @param dataSource The data source. (NotNull)
     * @param databaseTypeFacadeProp The facade properties for database type. (NotNull)
     * @param databaseProperties The database properties. (NotNull)
     * @param generatedTableMap The map of generated tables for checking reference tables. (NotNull)
     */
    public DfSynonymExtractorFactory(DfSchemaSource dataSource, DfDatabaseTypeFacadeProp databaseTypeFacadeProp,
            DfDatabaseProperties databaseProperties, Map<String, DfTableMeta> generatedTableMap) {
        _dataSource = dataSource;
        _databaseTypeFacadeProp = databaseTypeFacadeProp;
        _databaseProperties = databaseProperties;
        _generatedTableMap = generatedTableMap;
    }

    /**
     * @return The extractor of DB comments. (NullAllowed)
     */
    public DfSynonymExtractor createSynonymExtractor() {
        if (_databaseTypeFacadeProp.isDatabaseOracle()) {
            final DfSynonymExtractorOracle extractor = new DfSynonymExtractorOracle();
            extractor.setDataSource(_dataSource);
            extractor.setTargetSchemaList(createTargetSchemaList());
            extractor.setGeneratedTableMap(_generatedTableMap);
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
