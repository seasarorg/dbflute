package org.seasar.dbflute.logic.jdbc.metadata.identity.factory;

import javax.sql.DataSource;

import org.seasar.dbflute.logic.jdbc.metadata.identity.DfIdentityExtractor;
import org.seasar.dbflute.logic.jdbc.metadata.identity.DfIdentityExtractorDB2;
import org.seasar.dbflute.properties.facade.DfDatabaseTypeFacadeProp;

/**
 * @author jflute
 * @since 0.8.1 (2008/10/10 Friday)
 */
public class DfIdentityExtractorFactory {

    protected final DfDatabaseTypeFacadeProp _databaseTypeFacadeProp;
    protected final DataSource _dataSource;

    /**
     * @param databaseTypeFacadeProp The facade properties for database type. (NotNull)
     * @param dataSource The data source. (NotNull)
     */
    public DfIdentityExtractorFactory(DfDatabaseTypeFacadeProp databaseTypeFacadeProp, DataSource dataSource) {
        _databaseTypeFacadeProp = databaseTypeFacadeProp;
        _dataSource = dataSource;
    }

    /**
     * @return The extractor of DB comments. (NullAllowed)
     */
    public DfIdentityExtractor createIdentityExtractor() {
        if (_databaseTypeFacadeProp.isDatabaseDB2()) {
            final DfIdentityExtractorDB2 extractor = new DfIdentityExtractorDB2();
            extractor.setDataSource(_dataSource);
            return extractor;
        }
        return null;
    }
}
