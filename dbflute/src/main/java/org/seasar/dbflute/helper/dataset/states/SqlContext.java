package org.seasar.dbflute.helper.dataset.states;

/**
 * Row States. {Refer to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class SqlContext {

    private String sql;

    private Object[] args;

    private Class<?>[] argTypes;

    public SqlContext() {
    }

    public SqlContext(String sql, Object[] args, Class<?>[] argTypes) {
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