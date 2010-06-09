package org.seasar.dbflute.helper.jdbc.connection;

/**
 * @author jflute
 */
public class DfConnectionMetaInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _productName;
    protected String _productVersion;
    protected String _driverName;
    protected String _driverVersion;
    protected String _jdbcVersion;

    // ===================================================================================
    //                                                                             Display
    //                                                                             =======
    public String getProductDisp() {
        return _productName + " " + _productVersion;
    }

    public String getDriverDisp() {
        return _driverName + " " + _driverVersion + " for " + getJdbcDisp();
    }

    protected String getJdbcDisp() {
        return "JDBC " + _jdbcVersion;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getProductName() {
        return _productName;
    }

    public void setProductName(String productName) {
        this._productName = productName;
    }

    public String getProductVersion() {
        return _productVersion;
    }

    public void setProductVersion(String productVersion) {
        this._productVersion = productVersion;
    }

    public String getDriverName() {
        return _driverName;
    }

    public void setDriverName(String driverName) {
        this._driverName = driverName;
    }

    public String getDriverVersion() {
        return _driverVersion;
    }

    public void setDriverVersion(String driverVersion) {
        this._driverVersion = driverVersion;
    }

    public String getJdbcVersion() {
        return _jdbcVersion;
    }

    public void setJdbcVersion(String jdbcVersion) {
        this._jdbcVersion = jdbcVersion;
    }
}
