package org.seasar.dbflute.helper.jdbc.connection;


public interface DfDataSourceCreator {

    // =========================================================================================
    //                                                                                  Accessor
    //                                                                                  ========
    /**
     * Set the JDBC driver to be used.
     *
     * @param driver driver class name
     */
    public void setDriver(String driver);

    /**
     * Set the DB connection url.
     *
     * @param url connection url
     */
    public void setUrl(String url);

    /**
     * Set the user name for the DB connection.
     *
     * @param userId database user
     */
    public void setUserId(String userId);

    /**
     * Set the password for the DB connection.
     *
     * @param password database password
     */
    public void setPassword(String password);

    /**
     * Set the autoCommit for the DB connection.
     *
     * @param autoCommit Is auto commit?
     */
    public void setAutoCommit(boolean autoCommit);

    public void create();

    public void commit();
    
    public void destroy();
}
