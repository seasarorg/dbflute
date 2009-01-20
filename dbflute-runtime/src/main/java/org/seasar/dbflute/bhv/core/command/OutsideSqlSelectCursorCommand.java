package org.seasar.dbflute.bhv.core.command;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.seasar.dbflute.jdbc.CursorHandler;
import org.seasar.dbflute.s2dao.jdbc.TnResultSetHandler;


/**
 * The behavior command for OutsideSql.selectList().
 * @author jflute
 */
public class OutsideSqlSelectCursorCommand extends AbstractOutsideSqlSelectCommand<Object> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The handler of cursor. (Required) */
    protected CursorHandler _cursorHandler;

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "selectCursor";
    }

    public Class<?> getCommandReturnType() {
        return Object.class;
    }

    // ===================================================================================
    //                                                                     Extension Point
    //                                                                     ===============
    @Override
    protected TnResultSetHandler createOutsideSqlSelectResultSetHandler() {
        return new TnResultSetHandler() {
            public Object handle(ResultSet rs) throws SQLException {
                return _cursorHandler.handle(rs);
            }
        };
    }

    @Override
    protected Object getResultTypeSpecification() {
        return _cursorHandler;
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    @Override
    protected void assertStatus(String methodName) {
        assertBasicProperty(methodName);
        assertComponentProperty(methodName);
        assertOutsideSqlBasic(methodName);
        if (_cursorHandler == null) {
            throw new IllegalStateException(buildAssertMessage("_cursorHandler", methodName));
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setCursorHandler(CursorHandler cursorHandler) {
        _cursorHandler = cursorHandler;
    }
}
