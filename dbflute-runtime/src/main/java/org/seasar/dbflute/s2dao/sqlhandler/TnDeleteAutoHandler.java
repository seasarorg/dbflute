package org.seasar.dbflute.s2dao.sqlhandler;

import javax.sql.DataSource;

import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.metadata.TnPropertyType;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnDeleteAutoHandler extends TnAbstractAutoHandler {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnDeleteAutoHandler(DataSource dataSource,
            StatementFactory statementFactory, TnBeanMetaData beanMetaData,
            TnPropertyType[] propertyTypes) {
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