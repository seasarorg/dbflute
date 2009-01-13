package org.dbflute.s2dao.sqlhandler;

import javax.sql.DataSource;

import org.seasar.extension.jdbc.PropertyType;
import org.dbflute.jdbc.StatementFactory;
import org.dbflute.s2dao.metadata.TnBeanMetaData;

/**
 * @author DBFlute(AutoGenerator)
 */
public class InternalInsertBatchAutoHandler extends InternalAbstractBatchAutoHandler {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public InternalInsertBatchAutoHandler(DataSource dataSource,
            StatementFactory statementFactory, TnBeanMetaData beanMetaData,
            PropertyType[] propertyTypes) {
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