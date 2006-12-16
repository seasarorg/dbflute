package org.seasar.dbflute.helper.jdbc.generatedsql;

public interface DfGeneratedSqlExecutor {
    public void execute(String sql, String aliasName);
}
