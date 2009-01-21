package org.seasar.dbflute.s2dao.sqlhandler;

import javax.sql.DataSource;

import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.metadata.TnPropertyType;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnInsertBatchAutoHandler extends TnAbstractBatchAutoHandler {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnInsertBatchAutoHandler(DataSource dataSource,
            StatementFactory statementFactory, TnBeanMetaData beanMetaData,
            TnPropertyType[] propertyTypes) {
        super(dataSource, statementFactory, beanMetaData, propertyTypes);
        setOptimisticLockHandling(false);
    }

    // ===================================================================================
    //                                                                            Override
    //                                                                            ========
	@Override
    protected void setupBindVariables(Object bean) {
        setupInsertBindVariables(bean);
    }
}