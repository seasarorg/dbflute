package org.seasar.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.sqlhandler.TnAbstractAutoHandler;
import org.seasar.dbflute.s2dao.sqlhandler.TnAbstractBatchAutoHandler;
import org.seasar.dbflute.s2dao.sqlhandler.TnInsertBatchAutoHandler;

/**
 * {Refers to S2Container's utility and Extends it}
 * @author jflute
 */
public class TnInsertBatchAutoStaticCommand extends TnAbstractBatchAutoStaticCommand {

	// ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnInsertBatchAutoStaticCommand(DataSource dataSource,
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
