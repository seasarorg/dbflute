package org.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.dbflute.jdbc.StatementFactory;
import org.dbflute.s2dao.metadata.TnBeanMetaData;
import org.dbflute.s2dao.sqlhandler.InternalAbstractAutoHandler;
import org.dbflute.s2dao.sqlhandler.InternalAbstractBatchAutoHandler;
import org.dbflute.s2dao.sqlhandler.InternalDeleteBatchAutoHandler;

/**
 * @author DBFlute(AutoGenerator)
 */
public class InternalDeleteBatchAutoStaticCommand extends TnAbstractBatchAutoStaticCommand {

	// ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public InternalDeleteBatchAutoStaticCommand(DataSource dataSource,
            StatementFactory statementFactory, TnBeanMetaData beanMetaData, String[] propertyNames, boolean optimisticLockHandling) {
        super(dataSource, statementFactory, beanMetaData, propertyNames, optimisticLockHandling, false);
    }

	// ===================================================================================
    //                                                                            Override
    //                                                                            ========
	@Override
    protected InternalAbstractAutoHandler createAutoHandler() {
        return createBatchAutoHandler();
    }

	@Override
    protected InternalAbstractBatchAutoHandler createBatchAutoHandler() {
        return newInternalBatchAutoHandler();
    }

    protected InternalDeleteBatchAutoHandler newInternalBatchAutoHandler() {
        return new InternalDeleteBatchAutoHandler(getDataSource(), getStatementFactory(), getBeanMetaData(), getPropertyTypes());
    }

	@Override
    protected void setupSql() {
        setupDeleteSql();
    }

	@Override
    protected void setupPropertyTypes(String[] propertyNames) {
        setupDeletePropertyTypes(propertyNames);
    }
}
