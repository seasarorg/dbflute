package org.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.dbflute.jdbc.StatementFactory;
import org.dbflute.s2dao.metadata.TnBeanMetaData;
import org.dbflute.s2dao.sqlhandler.InternalAbstractAutoHandler;
import org.dbflute.s2dao.sqlhandler.InternalAbstractBatchAutoHandler;
import org.dbflute.s2dao.sqlhandler.InternalInsertBatchAutoHandler;


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
    protected InternalAbstractAutoHandler createAutoHandler() {
        return createBatchAutoHandler();
    }

	@Override
    protected InternalAbstractBatchAutoHandler createBatchAutoHandler() {
        return new InternalInsertBatchAutoHandler(getDataSource(), getStatementFactory(), getBeanMetaData(), getPropertyTypes());
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
