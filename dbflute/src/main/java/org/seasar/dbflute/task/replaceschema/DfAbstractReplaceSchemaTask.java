package org.seasar.dbflute.task.replaceschema;

import org.seasar.dbflute.task.bs.DfAbstractTask;

/**
 * @author jflute
 * @since 0.7.9 (2008/08/22 Friday)
 */
public abstract class DfAbstractReplaceSchemaTask extends DfAbstractTask {
    
    // ===================================================================================
    //                                                                 DataSource Override
    //                                                                 ===================
    @Override
    protected boolean isUseDataSource() {
        return true;
    }
    
    // ===================================================================================
    //                                                                      Various Common
    //                                                                      ==============
    protected String resolveTerminater4Tool() {
        return getBasicProperties().isDatabaseOracle() ? "/" : null;
    }
}
