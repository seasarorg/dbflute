package org.seasar.dbflute.bhv.outsidesql;

import org.seasar.dbflute.cbean.ListResultBean;

/**
 * @author jflute
 * @since 0.9.7.2 (2010/06/18 Friday)
 */
public interface OutsideSqlSelectListCallback {

    /**
     * Call back to select list.
     * @param <ENTITY> The type of entity for element.
     * @param path The path of SQL file. (NotNull)
     * @param pmb The parameter-bean. Allowed types are Bean object and Map object. (Nullable)
     * @param entityType The element type of entity. (NotNull)
     * @return The result bean of selected list. (NotNull)
     * @exception org.seasar.dbflute.exception.OutsideSqlNotFoundException When the outside-SQL is not found.
     * @exception org.seasar.dbflute.exception.DangerousResultSizeException When the result size is over the specified safety size.
     */
    <ENTITY> ListResultBean<ENTITY> callbackSelectList(String path, Object pmb, Class<ENTITY> entityType);
}
