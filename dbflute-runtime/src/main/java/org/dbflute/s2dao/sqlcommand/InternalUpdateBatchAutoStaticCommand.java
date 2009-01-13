package org.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.dbflute.jdbc.StatementFactory;
import org.dbflute.s2dao.metadata.TnBeanMetaData;
import org.dbflute.s2dao.sqlhandler.InternalAbstractAutoHandler;
import org.dbflute.s2dao.sqlhandler.InternalAbstractBatchAutoHandler;
import org.dbflute.s2dao.sqlhandler.InternalUpdateBatchAutoHandler;


/**
 * @author DBFlute(AutoGenerator)
 */
public class InternalUpdateBatchAutoStaticCommand extends TnAbstractBatchAutoStaticCommand {

	// ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public InternalUpdateBatchAutoStaticCommand(DataSource dataSource,
            StatementFactory statementFactory, TnBeanMetaData beanMetaData,
            String[] propertyNames, boolean optimisticLockHandling, boolean versionNoAutoIncrementOnMemory) {
        super(dataSource, statementFactory, beanMetaData, propertyNames, optimisticLockHandling, versionNoAutoIncrementOnMemory);
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
        InternalUpdateBatchAutoHandler handler = newInternalBatchAutoHandler();
        handler.setVersionNoAutoIncrementOnMemory(versionNoAutoIncrementOnMemory);
        return handler;
    }

    protected InternalUpdateBatchAutoHandler newInternalBatchAutoHandler() {
        return new InternalUpdateBatchAutoHandler(getDataSource(), getStatementFactory(), getBeanMetaData(), getPropertyTypes());
    }

	@Override
    protected void setupSql() {
        setupUpdateSql();
    }

	@Override
    protected void setupPropertyTypes(String[] propertyNames) {
        setupUpdatePropertyTypes(propertyNames);
    }
}
