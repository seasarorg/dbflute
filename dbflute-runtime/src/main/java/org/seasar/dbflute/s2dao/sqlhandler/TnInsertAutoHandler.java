package org.seasar.dbflute.s2dao.sqlhandler;

import javax.sql.DataSource;

import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.s2dao.identity.TnIdentifierGenerator;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.metadata.TnPropertyType;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnInsertAutoHandler extends TnAbstractAutoHandler {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnInsertAutoHandler(DataSource dataSource,
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
        setLoggingMessageSqlArgs(bindVariables);
    }

	@Override
    protected void preUpdateBean(Object bean) {
	    TnBeanMetaData bmd = getBeanMetaData();
        for (int i = 0; i < bmd.getIdentifierGeneratorSize(); i++) {
            TnIdentifierGenerator generator = bmd.getIdentifierGenerator(i);
            if (generator.isSelfGenerate()) {
                generator.setIdentifier(bean, getDataSource());
            }
        }
    }
	
	@Override
    protected void postUpdateBean(Object bean, int ret) {
	    TnBeanMetaData bmd = getBeanMetaData();
        for (int i = 0; i < bmd.getIdentifierGeneratorSize(); i++) {
            TnIdentifierGenerator generator = bmd.getIdentifierGenerator(i);
            if (!generator.isSelfGenerate()) {
                generator.setIdentifier(bean, getDataSource());
            }
        }
        updateVersionNoIfNeed(bean);
        updateTimestampIfNeed(bean);
    }
}
