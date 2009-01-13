package org.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.dbflute.jdbc.StatementFactory;
import org.dbflute.s2dao.metadata.TnBeanMetaData;
import org.dbflute.s2dao.sqlhandler.InternalAbstractAutoHandler;
import org.dbflute.s2dao.sqlhandler.InternalDeleteAutoHandler;


/**
 * @author DBFlute(AutoGenerator)
 */
public class InternalDeleteAutoStaticCommand extends TnAbstractAutoStaticCommand {

	// ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public InternalDeleteAutoStaticCommand(DataSource dataSource,
            StatementFactory statementFactory, TnBeanMetaData beanMetaData,
            String[] propertyNames, boolean optimisticLockHandling) {
        super(dataSource, statementFactory, beanMetaData, propertyNames, optimisticLockHandling, false);
    }

	// ===================================================================================
    //                                                                            Override
    //                                                                            ========
    @Override
    protected InternalAbstractAutoHandler createAutoHandler() {
        return new InternalDeleteAutoHandler(getDataSource(), getStatementFactory(), getBeanMetaData(), getPropertyTypes());
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
