package org.seasar.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.sqlhandler.TnAbstractBatchAutoHandler;


/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public abstract class TnAbstractBatchAutoStaticCommand extends TnAbstractAutoStaticCommand {

	// ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnAbstractBatchAutoStaticCommand(DataSource dataSource,
            StatementFactory statementFactory, TnBeanMetaData beanMetaData,
            String[] propertyNames, boolean optimisticLockHandling, boolean versionNoAutoIncrementOnMemory) {
        super(dataSource, statementFactory, beanMetaData, propertyNames, optimisticLockHandling, versionNoAutoIncrementOnMemory);
    }

	// ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    public Object execute(Object[] args) {
        final TnAbstractBatchAutoHandler handler = createBatchAutoHandler();
        handler.setOptimisticLockHandling(optimisticLockHandling);
        handler.setVersionNoAutoIncrementOnMemory(versionNoAutoIncrementOnMemory);
		handler.setSql(getSql());
		// The logging message SQL of procedure is unnecessary.
        // handler.setLoggingMessageSqlArgs(args);
        return handler.executeBatch(args);
    }

    protected abstract TnAbstractBatchAutoHandler createBatchAutoHandler();
}
