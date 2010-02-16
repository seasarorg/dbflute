package org.seasar.dbflute.helper.dataset.states;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDtsSqlContext {

    private String sql;

    private Object[] args;

    private Class<?>[] argTypes;

    public DfDtsSqlContext() {
    }

    public DfDtsSqlContext(String sql, Object[] args, Class<?>[] argTypes) {
        setSql(sql);
        setArgs(args);
        setArgTypes(argTypes);
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Class<?>[] getArgTypes() {
        return argTypes;
    }

    public void setArgTypes(Class<?>[] argTypes) {
        this.argTypes = argTypes;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}