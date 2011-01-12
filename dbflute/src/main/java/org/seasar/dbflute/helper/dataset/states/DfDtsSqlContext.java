package org.seasar.dbflute.helper.dataset.states;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDtsSqlContext {

    private String _sql;
    private Object[] _args;
    private Class<?>[] _argTypes;

    public DfDtsSqlContext(String sql, Object[] args, Class<?>[] argTypes) {
        _sql = sql;
        _args = args;
        _argTypes = argTypes;
    }

    public Object[] getArgs() {
        return _args;
    }

    public Class<?>[] getArgTypes() {
        return _argTypes;
    }

    public String getSql() {
        return _sql;
    }
}