package org.dbflute.s2dao.sqlhandler;

import javax.sql.DataSource;

import org.seasar.extension.jdbc.PropertyType;
import org.dbflute.jdbc.StatementFactory;
import org.dbflute.s2dao.metadata.TnBeanMetaData;

/**
 * @author DBFlute(AutoGenerator)
 */
public class InternalDeleteAutoHandler extends InternalAbstractAutoHandler {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public InternalDeleteAutoHandler(DataSource dataSource,
            StatementFactory statementFactory, TnBeanMetaData beanMetaData,
            PropertyType[] propertyTypes) {
        super(dataSource, statementFactory, beanMetaData, propertyTypes);
    }

    // ===================================================================================
    //                                                                            Override
    //                                                                            ========
	@Override
    protected void setupBindVariables(Object bean) {
        setupDeleteBindVariables(bean);
        setLoggingMessageSqlArgs(bindVariables);
    }
}