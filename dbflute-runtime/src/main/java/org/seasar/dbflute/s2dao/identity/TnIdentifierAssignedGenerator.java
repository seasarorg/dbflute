package org.seasar.dbflute.s2dao.identity;

import javax.sql.DataSource;

import org.seasar.dbflute.s2dao.metadata.TnPropertyType;

/**
 * {Refers to S2Container's utility and Extends it}
 * @author jflute
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
