package org.dbflute.resource;

/**
 * @author DBFlute(AutoGenerator)
 */
public class TnSqlLog {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String rawSql;
    private String completeSql;
    private Object[] bindArgs;
    private Class<?>[] bindArgTypes;
	
	// ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnSqlLog(String rawSql, String completeSql, Object[] bindArgs, Class<?>[] bindArgTypes) {
        this.rawSql = rawSql;
        this.completeSql = completeSql;
        this.bindArgs = bindArgs;
        this.bindArgTypes = bindArgTypes;
    }

	// ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Object[] getBindArgs() {
        return bindArgs;
    }

    public Class<?>[] getBindArgTypes() {
        return bindArgTypes;
    }

    public String getCompleteSql() {
        return completeSql;
    }

    public String getRawSql() {
        return rawSql;
    }

    public String toString() {
        return rawSql;
    }
}
