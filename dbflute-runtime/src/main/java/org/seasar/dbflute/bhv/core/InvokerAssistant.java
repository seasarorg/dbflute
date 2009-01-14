package org.seasar.dbflute.bhv.core;

import javax.sql.DataSource;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.cbean.sqlclause.SqlClauseCreator;
import org.seasar.dbflute.dbmeta.DBMetaProvider;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.resource.ResourceParameter;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaDataFactory;
import org.seasar.dbflute.s2dao.valuetype.TnValueTypeFactory;

/**
 * @author jflute
 */
public interface InvokerAssistant {

    /**
     * @return The current database definition. (NotNull)
     */
    DBDef assistCurrentDBDef();

    /**
     * @return The data source. (NotNull)
     */
    DataSource assistDataSource();
    
    /**
     * @return The provider of DB meta. (NotNull)
     */
    DBMetaProvider assistDBMetaProvider();

    /**
     * @return The create of SQL clause. (NotNull)
     */
    SqlClauseCreator assistSqlClauseCreator();
    
    /**
     * @return The factory of statement. (NotNull)
     */
    StatementFactory assistStatementFactory();
    
    /**
     * @return The factory of bean meta data. (NotNull)
     */
    TnBeanMetaDataFactory assistBeanMetaDataFactory();
    
    /**
     * @return The factory of value type. (NotNull)
     */
    TnValueTypeFactory assistValueTypeFactory();
    
    /**
     * @return The parameter of resource. (NotNull)
     */
    ResourceParameter assistResourceParameter();

    /**
     * @return The encoding of SQL files. (NotNull)
     */
    String assistSqlFileEncoding();

    /**
     * To be disposable.
     */
    void toBeDisposable();
}
