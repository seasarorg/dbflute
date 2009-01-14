package com.example.dbflute.basic.dbflute.allcommon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.bhv.core.CommonColumnAutoSetupper;


/**
 * The basic implementation of the auto set-upper of common column.
 * @author DBFlute(AutoGenerator)
 */
public class ImplementedCommonColumnAutoSetupper implements CommonColumnAutoSetupper {

    // =====================================================================================
    //                                                                            Definition
    //                                                                            ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(ImplementedCommonColumnAutoSetupper.class);

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========

    // =====================================================================================
    //                                                                                Set up
    //                                                                                ======
    /**
     * Handle common columns of insert if it needs.
     * @param targetEntity Target entity that the type is entity interface. (NotNull)
     */
    public void handleCommonColumnOfInsertIfNeeds(Entity targetEntity) {
        if (!(targetEntity instanceof EntityDefinedCommonColumn)) {
            return;
        }
        final EntityDefinedCommonColumn entity = (EntityDefinedCommonColumn)targetEntity;
        if (!entity.canCommonColumnAutoSetup()) {
            return;
        }
        if (_log.isDebugEnabled()) {
            String msg = "...Filtering entity of INSERT about the column columns of " + entity.getTableDbName();
            msg = msg + ": entity=" + entity.getDBMeta().extractPrimaryKeyMapString(entity, "{", "}", ", ", "=");
            _log.debug(msg);
        }

        final java.sql.Timestamp registerDatetime = org.seasar.dbflute.AccessContext.getAccessTimestampOnThread();
        entity.setRegisterDatetime(registerDatetime);

        final String registerUser = org.seasar.dbflute.AccessContext.getAccessUserOnThread();
        entity.setRegisterUser(registerUser);

        final String registerProcess = org.seasar.dbflute.AccessContext.getAccessProcessOnThread();
        entity.setRegisterProcess(registerProcess);

        final java.sql.Timestamp updateDatetime = entity.getRegisterDatetime();
        entity.setUpdateDatetime(updateDatetime);

        final String updateUser = entity.getRegisterUser();
        entity.setUpdateUser(updateUser);

        final String updateProcess = entity.getRegisterProcess();
        entity.setUpdateProcess(updateProcess);
    }

    /**
     * Handle common columns of update if it needs.
     * @param targetEntity Target entity that the type is entity interface. (NotNull)
     */
    public void handleCommonColumnOfUpdateIfNeeds(Entity targetEntity) {
        if (!(targetEntity instanceof EntityDefinedCommonColumn)) {
            return;
        }
        final EntityDefinedCommonColumn entity = (EntityDefinedCommonColumn)targetEntity;
        if (!entity.canCommonColumnAutoSetup()) {
            return;
        }
        if (_log.isDebugEnabled()) {
            String msg = "...Filtering entity of UPDATE about the column columns of " + entity.getTableDbName();
            msg = msg + ": entity=" + entity.getDBMeta().extractPrimaryKeyMapString(entity, "{", "}", ", ", "=");
            _log.debug(msg);
        }

        final java.sql.Timestamp updateDatetime = org.seasar.dbflute.AccessContext.getAccessTimestampOnThread();
        entity.setUpdateDatetime(updateDatetime);

        final String updateUser = org.seasar.dbflute.AccessContext.getAccessUserOnThread();
        entity.setUpdateUser(updateUser);

        final String updateProcess = org.seasar.dbflute.AccessContext.getAccessProcessOnThread();
        entity.setUpdateProcess(updateProcess);
    }
}
