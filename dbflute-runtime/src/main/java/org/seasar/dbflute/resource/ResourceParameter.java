package org.seasar.dbflute.resource;

/**
 * The context of internal resource.
 * @author jflute
 */
public class ResourceParameter {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _outsideSqlPackage;
    protected String _logDateFormat;
    protected String _logTimestampFormat;

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getOutsideSqlPackage() {
        return _outsideSqlPackage;
    }

    public void setOutsideSqlPackage(String outsideSqlPackage) {
        _outsideSqlPackage = outsideSqlPackage;
    }

    public String getLogDateFormat() {
        return _logDateFormat;
    }

    public void setLogDateFormat(String logDateFormat) {
        _logDateFormat = logDateFormat;
    }

    public String getLogTimestampFormat() {
        return _logTimestampFormat;
    }

    public void setLogTimestampFormat(String logTimestampFormat) {
        _logTimestampFormat = logTimestampFormat;
    }
}
