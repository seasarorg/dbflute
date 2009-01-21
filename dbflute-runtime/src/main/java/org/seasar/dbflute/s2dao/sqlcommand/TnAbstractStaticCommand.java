package org.seasar.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaData;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public abstract class TnAbstractStaticCommand extends TnAbstractSqlCommand {

	// ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private TnBeanMetaData beanMetaData;

	// ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnAbstractStaticCommand(DataSource dataSource, StatementFactory statementFactory, TnBeanMetaData beanMetaData) {
        super(dataSource, statementFactory);
        this.beanMetaData = beanMetaData;
    }

	// ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public TnBeanMetaData getBeanMetaData() {
        return beanMetaData;
    }
}
