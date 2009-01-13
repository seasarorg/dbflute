package org.dbflute.s2dao.identity;

import javax.sql.DataSource;

import org.dbflute.s2dao.metadata.TnPropertyType;

/**
 * @author DBFlute(AutoGenerator)
 */
public class TnIdentifierAssignedGenerator extends TnIdentifierAbstractGenerator {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnIdentifierAssignedGenerator(TnPropertyType propertyType) {
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
