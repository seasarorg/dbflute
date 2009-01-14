package org.seasar.dbflute.s2dao.sqlhandler;

import javax.sql.DataSource;

import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.metadata.TnPropertyType;

/**
 * @author DBFlute(AutoGenerator)
 */
public class TnUpdateAutoHandler extends TnAbstractAutoHandler {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnUpdateAutoHandler(DataSource dataSource,
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
        setLoggingMessageSqlArgs(bindVariables);
    }

	@Override
    protected void postUpdateBean(Object bean, int ret) {
        updateVersionNoIfNeed(bean);
        updateTimestampIfNeed(bean);
    }
}
