package org.dbflute.s2dao.identity;

import javax.sql.DataSource;

import org.seasar.extension.jdbc.PropertyType;

/**
 * @author DBFlute(AutoGenerator)
 */
public class TnIdentifierAssignedGenerator extends TnIdentifierAbstractGenerator {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnIdentifierAssignedGenerator(PropertyType propertyType) {
        super(propertyType);
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    public void setIdentifier(Object bean, DataSource ds) {
    }

    public boolean isSelfGenerate() {
        return true;
    }
}
