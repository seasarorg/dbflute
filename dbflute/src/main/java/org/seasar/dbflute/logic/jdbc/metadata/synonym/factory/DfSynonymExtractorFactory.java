package org.seasar.dbflute.logic.jdbc.metadata.synonym.factory;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfTableMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.DfSynonymExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.synonym.DfSynonymExtractorOracle;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;

/**
 * @author jflute
 * @since 0.9.3 (2009/02/24 Tuesday)
 */
public class DfSynonymExtractorFactory {

    protected DataSource _dataSource;
    protected DfBasicProperties _basicProperties;
    protected DfDatabaseProperties _databaseProperties;
    protected Map<String, DfTableMetaInfo> _generatedTableMap;

    /**
     * @param dataSource The data source. (NotNull)
     * @param basicProperties The basic properties. (NotNull)
     * @param databaseProperties The database properties. (NotNull)
     * @param generatedTableMap The map of generated tables for checking reference tables. (NotNull)
     */
    public DfSynonymExtractorFactory(DataSource dataSource, DfBasicProperties basicProperties,
            DfDatabaseProperties databaseProperties, Map<String, DfTableMetaInfo> generatedTableMap) {
        _dataSource = dataSource;
        _basicProperties = basicProperties;
        _databaseProperties = databaseProperties;
        _generatedTableMap = generatedTableMap;
    }

    /**
     * @return The extractor of DB comments. (NullAllowed)
     */
    public DfSynonymExtractor createSynonymExtractor() {
        if (_basicProperties.isDatabaseOracle()) {
            final DfSynonymExtractorOracle extractor = new DfSynonymExtractorOracle();
            extractor.setDataSource(_dataSource);
            extractor.setTargetSchemaList(createTargetSchemaList());
            extractor.setGeneratedTableMap(_generatedTableMap);
            return extractor;
        }
        return null;
    }

    protected List<UnifiedSchema> createTargetSchemaList() { // not only main schema but also additional schemas
        return _databaseProperties.getTargetSchemaList();
    }
}
