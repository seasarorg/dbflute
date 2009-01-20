package org.seasar.dbflute.bhv.core.command;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.dbflute.outsidesql.OutsideSqlOption;
import org.seasar.dbflute.resource.ResourceContext;

/**
 * @author jflute
 * @param <RESULT> The type of result.
 */
public abstract class AbstractOutsideSqlCommand<RESULT> extends AbstractBehaviorCommand<RESULT> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                     Basic Information
    //                                     -----------------
    /** The path of outside-SQL. (Required) */
    protected String _outsideSqlPath;

    /** The parameter-bean. (Required to set, but Nullable) */
    protected Object _parameterBean;

    /** The option of outside-SQL. (Required) */
    protected OutsideSqlOption _outsideSqlOption;

    /** The current database definition. (Required) */
    protected DBDef _currentDBDef;

    // ===================================================================================
    //                                                                  Detail Information
    //                                                                  ==================
    public boolean isConditionBean() {
        return false; // When the command is for outside-SQL, it always be false.
    }

    public boolean isOutsideSql() {
        return true;
    }

    public boolean isSelectCount() {
        return false; // When the command is for outside-SQL, it always be false.
    }

    // ===================================================================================
    //                                                                Argument Information
    //                                                                ====================
    public ConditionBean getConditionBean() {
        return null;
    }

    public String getOutsideSqlPath() {
        return _outsideSqlPath;
    }

    public OutsideSqlOption getOutsideSqlOption() {
        return _outsideSqlOption;
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected OutsideSqlContext createOutsideSqlContext() {
        return new OutsideSqlContext(ResourceContext.dbmetaProvider(), ResourceContext.getOutsideSqlPackage());
    }

    protected String buildDbmsSuffix() {
        assertOutsideSqlBasic("buildDbmsSuffix");
        final String productName = _currentDBDef.code();
        return (productName != null ? "_" + productName.toLowerCase() : "");
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertOutsideSqlBasic(String methodName) {
        if (_outsideSqlPath == null) {
            throw new IllegalStateException(buildAssertMessage("_outsideSqlPath", methodName));
        }
        if (_outsideSqlOption == null) {
            throw new IllegalStateException(buildAssertMessage("_outsideSqlOption", methodName));
        }
        if (_currentDBDef == null) {
            throw new IllegalStateException(buildAssertMessage("_currentDBDef", methodName));
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setOutsideSqlPath(String outsideSqlPath) {
        _outsideSqlPath = outsideSqlPath;
    }

    public void setParameterBean(Object parameterBean) {
        _parameterBean = parameterBean;
    }

    public void setOutsideSqlOption(OutsideSqlOption outsideSqlOption) {
        _outsideSqlOption = outsideSqlOption;
    }

    public void setCurrentDBDef(DBDef currentDBDef) {
        _currentDBDef = currentDBDef;
    }
}
