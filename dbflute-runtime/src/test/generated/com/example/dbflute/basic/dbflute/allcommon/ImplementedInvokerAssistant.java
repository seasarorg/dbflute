package com.example.dbflute.basic.dbflute.allcommon;

import javax.sql.DataSource;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.bhv.core.BehaviorCommandInvoker;
import org.seasar.dbflute.bhv.core.InvokerAssistant;
import org.seasar.dbflute.cbean.sqlclause.SqlClauseCreator;
import org.seasar.dbflute.dbmeta.DBMetaProvider;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.resource.ResourceParameter;
import org.seasar.dbflute.s2dao.beans.factory.TnBeanDescFactory;
import org.seasar.dbflute.s2dao.extension.TnBeanMetaDataFactoryExtension;
import org.seasar.dbflute.s2dao.jdbc.TnStatementFactoryImpl;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaDataFactory;
import org.seasar.dbflute.s2dao.valuetype.TnValueTypeFactory;
import org.seasar.dbflute.s2dao.valuetype.TnValueTypes;
import org.seasar.dbflute.s2dao.valuetype.impl.TnValueTypeFactoryImpl;
import org.seasar.framework.util.Disposable;
import org.seasar.framework.util.DisposableUtil;

/**
 * @author DBFlute(AutoGenerator)
 */
public class ImplementedInvokerAssistant implements InvokerAssistant {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BehaviorCommandInvoker _behaviorCommandInvoker;
    protected DataSource _dataSource;
    protected final DBMetaProvider _dbmetaProvider = createDBMetaProvider();
    protected final SqlClauseCreator _sqlClauseCreator = createSqlClauseCreator();
    protected final StatementFactory _statementFactory = createStatementFactory();
    protected final TnValueTypeFactory _valueTypeFactory = createValueTypeFactory();

    // This should be initialized after initializing the factory of value type.
    // Because createBeanMetaDataFactory() uses the variable '_valueTypeFactory'.
    protected final TnBeanMetaDataFactory _beanMetaDataFactory = createBeanMetaDataFactory();

    protected boolean _disposable;

    // ===================================================================================
    //                                                                            Creation
    //                                                                            ========
    protected DBMetaProvider createDBMetaProvider() {
        return new DBMetaInstanceHandler();
    }
    
    protected SqlClauseCreator createSqlClauseCreator() {
        return new ImplementedSqlClauseCreator();
    }

    protected StatementFactory createStatementFactory() {
        final TnStatementFactoryImpl factory = new TnStatementFactoryImpl();
        factory.setDefaultStatementConfig(DBFluteConfig.getInstance().getDefaultStatementConfig());
        factory.setInternalDebug(DBFluteConfig.getInstance().isInternalDebug());
        return factory;
    }

    protected TnValueTypeFactory createValueTypeFactory() {
        return new TnValueTypeFactoryImpl();
    }

    protected TnBeanMetaDataFactory createBeanMetaDataFactory() {
        final TnBeanMetaDataFactoryExtension factory = new TnBeanMetaDataFactoryExtension();
        factory.setDataSource(_dataSource);
        factory.setValueTypeFactory(_valueTypeFactory);
        return factory;
    }

    // ===================================================================================
    //                                                                 Assistant Main Work
    //                                                                 ===================
    public DBDef assistCurrentDBDef() {
        return DBCurrent.getInstance().currentDBDef();
    }

    public DataSource assistDataSource() {
        return _dataSource;
    }

    public DBMetaProvider assistDBMetaProvider() {
        return _dbmetaProvider;
    }

    public SqlClauseCreator assistSqlClauseCreator() {
        return _sqlClauseCreator;
    }

    public StatementFactory assistStatementFactory() {
        return _statementFactory;
    }

    public TnBeanMetaDataFactory assistBeanMetaDataFactory() {
        return _beanMetaDataFactory;
    }

    public TnValueTypeFactory assistValueTypeFactory() {
        return _valueTypeFactory;
    }

    public ResourceParameter assistResourceParameter() {
        ResourceParameter resourceParameter = new ResourceParameter();
        resourceParameter.setOutsideSqlPackage(DBFluteConfig.getInstance().getOutsideSqlPackage());
        resourceParameter.setLogDateFormat(DBFluteConfig.getInstance().getLogDateFormat());
        resourceParameter.setLogTimestampFormat(DBFluteConfig.getInstance().getLogTimestampFormat());
        return resourceParameter;
    }

    public String assistSqlFileEncoding() {
        return "UTF-8";
    }

    // ===================================================================================
    //                                                                             Dispose
    //                                                                             =======
    public void toBeDisposable() { // for HotDeploy
        if (!_disposable) {
            synchronized (this) {
                if (!_disposable) {
                    // Register for BehaviorCommandInvoker
                    DisposableUtil.add(new Disposable() {
                        public void dispose() {
                            if (_behaviorCommandInvoker != null) {
                                _behaviorCommandInvoker.clearExecutionCache();
                            }
                            _disposable = false;
                        }
                    });
                    // Register for BeanDescFactory
                    DisposableUtil.add(new Disposable() {
                        public void dispose() {
                            TnBeanDescFactory.clear();
                        }
                    });
                    // Register for ValueTypes
                    DisposableUtil.add(new Disposable() {
                        public void dispose() {
                            TnValueTypes.clear();
                        }
                    });
                    _disposable = true;
                }
            }
        }
    }

    public boolean isDisposable() {
        return _disposable;
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertBehaviorCommandInvoker() {
        if (_behaviorCommandInvoker == null) {
            String msg = "The attribute 'behaviorCommandInvoker' should not be null!";
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setDataSource(DataSource dataSource) {
        _dataSource = dataSource;
    }

    public void setBehaviorCommandInvoker(BehaviorCommandInvoker behaviorCommandInvoker) {
        _behaviorCommandInvoker = behaviorCommandInvoker;
    }
}
