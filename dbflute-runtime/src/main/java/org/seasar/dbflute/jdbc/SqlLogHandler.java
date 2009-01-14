package org.seasar.dbflute.jdbc;

/**
 * The handler of SQL log.
 * @author DBFlute(AutoGenerator)
 */
public interface SqlLogHandler {

    /**
     * @param executedSql The executed SQL. (NotNull)
     * @param displaySql The SQL for display. (NotNull)
     * @param args The arguments of the SQL. (Nullable)
     * @param argTypes The argument types of the SQL. (Nullable)
     */
    void handle(String executedSql, String displaySql, Object[] args, Class<?>[] argTypes);
}
