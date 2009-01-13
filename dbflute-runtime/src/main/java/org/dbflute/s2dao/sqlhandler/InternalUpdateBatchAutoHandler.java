package org.dbflute.s2dao.sqlhandler;

import javax.sql.DataSource;

import org.dbflute.s2dao.metadata.TnPropertyType;
import org.dbflute.jdbc.StatementFactory;
import org.dbflute.s2dao.metadata.TnBeanMetaData;

/**
 * @author DBFlute(AutoGenerator)
 */
public class InternalUpdateBatchAutoHandler extends InternalAbstractBatchAutoHandler {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public InternalUpdateBatchAutoHandler(DataSource dataSource,
            StatementFactory statementFactory, TnBeanMetaData beanMetaData,
            TnPropertyType[] propertyTypes) {

        super(dataSource, statementFactory, beanMetaData, propertyTypes);
    }

    // ===================================================================================
    //                                                                            Override
    //                                                                            ========
	@Override
    protected void setupBindVariables(Object bean) {
        setupUpdateBindVariables(bean);
    }
}