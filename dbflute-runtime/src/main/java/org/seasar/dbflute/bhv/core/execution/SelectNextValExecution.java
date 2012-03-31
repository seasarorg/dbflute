package org.seasar.dbflute.bhv.core.execution;

import java.util.Map;

import javax.sql.DataSource;

import org.seasar.dbflute.bhv.core.supplement.SequenceCache;
import org.seasar.dbflute.bhv.core.supplement.SequenceCache.SequenceRealExecutor;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.s2dao.jdbc.TnResultSetHandler;

/**
 * @author jflute
 */
public class SelectNextValExecution extends SelectSimpleExecution {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final SequenceCache _sequenceCache;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public SelectNextValExecution(DataSource dataSource, StatementFactory statementFactory,
            Map<String, Class<?>> argNameTypeMap, String twoWaySql, TnResultSetHandler resultSetHandler,
            SequenceCache sequenceCache) {
        super(dataSource, statementFactory, argNameTypeMap, twoWaySql, resultSetHandler);
        _sequenceCache = sequenceCache;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    public Object execute(final Object[] args) {
        final Object nextVal;
        if (_sequenceCache != null) {
            nextVal = _sequenceCache.nextval(new SequenceRealExecutor() {
                public Object execute() {
                    return executeSuperExecute(args);
                }
            });
        } else {
            nextVal = executeSuperExecute(args);
        }
        return nextVal;
    }

    protected Object executeSuperExecute(Object[] args) {
        return super.execute(args);
    }
}
