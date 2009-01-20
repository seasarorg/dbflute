package org.seasar.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.dbflute.s2dao.sqlhandler.TnAbstractAutoHandler;
import org.seasar.dbflute.s2dao.sqlhandler.TnDeleteAutoHandler;


/**
 * @author jflute
 */
public class TnDeleteAutoStaticCommand extends TnAbstractAutoStaticCommand {

	// ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnDeleteAutoStaticCommand(DataSource dataSource,
            StatementFactory statementFactory, TnBeanMetaData beanMetaData,
            String[] propertyNames, boolean optimisticLockHandling) {
        super(dataSource, statementFactory, beanMetaData, propertyNames, optimisticLockHandling, false);
    }

	// ===================================================================================
    //                                                                            Override
    //                                                                            ========
    @Override
    protected TnAbstractAutoHandler createAutoHandler() {
        return new TnDeleteAutoHandler(getDataSource(), getStatementFactory(), getBeanMetaData(), getPropertyTypes());
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
