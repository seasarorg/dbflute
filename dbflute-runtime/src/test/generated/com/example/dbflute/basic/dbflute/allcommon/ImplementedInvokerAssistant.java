package com.example.dbflute.basic.dbflute.allcommon;

import org.dbflute.DBDef;
import org.dbflute.bhv.core.BehaviorCommandInvoker;
import org.dbflute.bhv.core.InvokerAssistant;
import org.dbflute.cbean.sqlclause.SqlClauseCreator;
import org.dbflute.dbmeta.DBMetaProvider;
import org.dbflute.jdbc.StatementConfig;
import org.dbflute.resource.ResourceParameter;
import org.dbflute.s2dao.beans.factory.TnBeanDescFactory;
import org.seasar.framework.util.Disposable;
import org.seasar.framework.util.DisposableUtil;

/**
 * @author DBFlute(AutoGenerator)
 */
public class ImplementedInvokerAssistant implements InvokerAssistant {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DBMetaProvider _dbmetaProvider = createDBMetaProvider();
    protected final SqlClauseCreator _sqlClauseCreator = createSqlClauseCreator();
    protected BehaviorCommandInvoker _behaviorCommandInvoker;
    protected boolean _disposable;

    // ===================================================================================
    //                                                                            Creation
    //                                                                            ========
    protected SqlClauseCreator createSqlClauseCreator() {
        return new ImplementedSqlClauseCreator();
    }

    protected DBMetaProvider createDBMetaProvider() {
        return new DBMetaInstanceHandler();
    }

    // ===================================================================================
    //                                                                 Assistant Main Work
    //                                                                 ===================
    public DBDef assistCurrentDBDef() {
        return DBCurrent.getInstance().currentDBDef();
    }

    public DBMetaProvider assistDBMetaProvider() {
        return _dbmetaProvider;
    }

    public SqlClauseCreator assistSqlClauseCreator() {
        return _sqlClauseCreator;
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

    public StatementConfig assistDefaultStatementConfig() {
        return DBFluteConfig.getInstance().getDefaultStatementConfig();
    }
    
    public boolean assistInternalDebug() {
        return DBFluteConfig.getInstance().isInternalDebug();
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
    public void setBehaviorCommandInvoker(BehaviorCommandInvoker behaviorCommandInvoker) {
        _behaviorCommandInvoker = behaviorCommandInvoker;
    }
}
