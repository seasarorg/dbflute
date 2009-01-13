package org.dbflute.s2dao.identity;

import javax.sql.DataSource;

import org.dbflute.resource.ResourceContext;
import org.dbflute.s2dao.metadata.PropertyType;

/**
 * @author DBFlute(AutoGenerator)
 */
public class TnIdentifierIdentityGenerator extends TnIdentifierAbstractGenerator {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnIdentifierIdentityGenerator(PropertyType propertyType) {
        super(propertyType);
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    public void setIdentifier(Object bean, DataSource ds) {
        String identitySelectSql = ResourceContext.currentDBDef().dbway().getIdentitySelectSql();
        if (identitySelectSql == null) {
            String msg = "Identity is unsupported at the DB: " + ResourceContext.currentDBDef();
            throw new IllegalStateException(msg);
        }
        Object value = executeSql(ds, identitySelectSql, null);
        reflectIdentifier(bean, value);
    }

    public boolean isSelfGenerate() {
        return false;
    }
}
