package org.seasar.dbflute.logic.jdbc.metadata.identity.factory;

import javax.sql.DataSource;

import org.seasar.dbflute.logic.jdbc.metadata.identity.DfIdentityExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.identity.DfIdentityExtractorDB2;
import org.seasar.dbflute.properties.DfBasicProperties;

/**
 * @author jflute
 * @since 0.8.1 (2008/10/10 Friday)
 */
public class DfIdentityExtractorFactory {

    protected DfBasicProperties _basicProperties;
    protected DataSource _dataSource;

    /**
     * @param basicProperties The basic properties. (NotNull)
     * @param dataSource The data source. (NotNull)
     */
    public DfIdentityExtractorFactory(DfBasicProperties basicProperties, DataSource dataSource) {
        _basicProperties = basicProperties;
        _dataSource = dataSource;
    }

    /**
     * @return The extractor of DB comments. (NullAllowed)
     */
    public DfIdentityExtractor createIdentityExtractor() {
        if (_basicProperties.isDatabaseDB2()) {
            final DfIdentityExtractorDB2 extractor = new DfIdentityExtractorDB2();
            extractor.setDataSource(_dataSource);
            return extractor;
        }
        return null;
    }
}
