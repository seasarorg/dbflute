package org.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.dbflute.jdbc.StatementFactory;
import org.dbflute.s2dao.metadata.TnBeanMetaData;
import org.dbflute.s2dao.sqlhandler.TnAbstractAutoHandler;
import org.dbflute.s2dao.sqlhandler.TnAbstractBatchAutoHandler;
import org.dbflute.s2dao.sqlhandler.TnUpdateBatchAutoHandler;


/**
 * @author DBFlute(AutoGenerator)
 */
public class TnUpdateBatchAutoStaticCommand extends TnAbstractBatchAutoStaticCommand {

	// ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnUpdateBatchAutoStaticCommand(DataSource dataSource,
            StatementFactory statementFactory, TnBeanMetaData beanMetaData,
            String[] propertyNames, boolean optimisticLockHandling, boolean versionNoAutoIncrementOnMemory) {
        super(dataSource, statementFactory, beanMetaData, propertyNames, optimisticLockHandling, versionNoAutoIncrementOnMemory);
    }

	// ===================================================================================
    //                                                                            Override
    //                                                                            ========
	@Override
    protected TnAbstractAutoHandler createAutoHandler() {
        return createBatchAutoHandler();
    }

	@Override
    protected TnAbstractBatchAutoHandler createBatchAutoHandler() {
        TnUpdateBatchAutoHandler handler = newInternalBatchAutoHandler();
        handler.setVersionNoAutoIncrementOnMemory(versionNoAutoIncrementOnMemory);
        return handler;
    }

    protected TnUpdateBatchAutoHandler newInternalBatchAutoHandler() {
        return new TnUpdateBatchAutoHandler(getDataSource(), getStatementFactory(), getBeanMetaData(), getPropertyTypes());
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
