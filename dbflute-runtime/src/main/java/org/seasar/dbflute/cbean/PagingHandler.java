package org.seasar.dbflute.cbean;

import java.util.List;

/**
 * The handler of paging.
 * @param <ENTITY> The type of entity.
 * @author DBFlute(AutoGenerator)
 */
public interface PagingHandler<ENTITY> {

    /**
     * Get the bean of paging.
     * @return The bean of paging. (NotNull)
     */
    public PagingBean getPagingBean();

    /**
     * Execute SQL for count.
     * @return The count of execution.
     */
    public int count();

    /**
     * Execute SQL for paging.
     * @return The list of entity. (NotNull)
     */
    public List<ENTITY> paging();
}
