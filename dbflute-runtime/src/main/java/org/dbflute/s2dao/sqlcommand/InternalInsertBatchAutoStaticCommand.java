package org.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.dbflute.jdbc.StatementFactory;
import org.dbflute.s2dao.metadata.TnBeanMetaData;
import org.dbflute.s2dao.sqlhandler.TnAbstractAutoHandler;
import org.dbflute.s2dao.sqlhandler.TnAbstractBatchAutoHandler;
import org.dbflute.s2dao.sqlhandler.TnInsertBatchAutoHandler;


/**
 * @author DBFlute(AutoGenerator)
 */
public class InternalInsertBatchAutoStaticCommand extends TnAbstractBatchAutoStaticCommand {

	// ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public InternalInsertBatchAutoStaticCommand(DataSource dataSource,
            StatementFactory statementFactory, TnBeanMetaData beanMetaData,
            String[] propertyNames) {
        super(dataSource, statementFactory, beanMetaData, propertyNames, false, false);
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
        return new TnInsertBatchAutoHandler(getDataSource(), getStatementFactory(), getBeanMetaData(), getPropertyTypes());
    }

	@Override
    protected void setupSql() {
        setupInsertSql();
    }

	@Override
    protected void setupPropertyTypes(String[] propertyNames) {
        setupInsertPropertyTypes(propertyNames);
    }
}
