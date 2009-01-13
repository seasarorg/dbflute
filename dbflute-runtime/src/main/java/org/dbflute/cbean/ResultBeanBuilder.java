package org.dbflute.cbean;

import java.util.List;

/**
 * The builder of result bean.
 * @param <ENTITY> The type of entity.
 * @author DBFlute(AutoGenerator)
 */
public class ResultBeanBuilder<ENTITY> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _tableDbName;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ResultBeanBuilder(String tableDbName) {
        _tableDbName = tableDbName;
    }

    // ===================================================================================
    //                                                                             Builder
    //                                                                             =======
    /**
     * Build the result bean of list without order-by clause. {for Various}
     * @param selectedList Selected list. (NotNull)
     * @return The result bean of list. (NotNull)
     */
    public ListResultBean<ENTITY> buildListResultBean(List<ENTITY> selectedList) {
        ListResultBean<ENTITY> rb = new ListResultBean<ENTITY>();
        rb.setTableDbName(_tableDbName);
        rb.setAllRecordCount(selectedList.size());
        rb.setSelectedList(selectedList);
        return rb;
    }

    /**
     * Build the result bean of list. {for CB}
     * @param cb The condition-bean. (NotNull)
     * @param selectedList Selected list. (NotNull)
     * @return The result bean of list. (NotNull)
     */
    public ListResultBean<ENTITY> buildListResultBean(ConditionBean cb, List<ENTITY> selectedList) {
        ListResultBean<ENTITY> rb = new ListResultBean<ENTITY>();
        rb.setTableDbName(_tableDbName);
        rb.setAllRecordCount(selectedList.size());
        rb.setSelectedList(selectedList);
        rb.setOrderByClause(cb.getSqlComponentOfOrderByClause());
        return rb;
    }

    /**
     * Build the result bean of paging. {for Paging}
     * @param pb The bean of paging. (NotNull)
     * @param allRecordCount All record count.
     * @param selectedList The list of selected entity. (NotNull)
     * @return The result bean of paging. (NotNull)
     */
    public PagingResultBean<ENTITY> buildPagingResultBean(PagingBean pb, int allRecordCount, List<ENTITY> selectedList) {
        PagingResultBean<ENTITY> rb = new PagingResultBean<ENTITY>();
        rb.setTableDbName(_tableDbName);
        rb.setAllRecordCount(allRecordCount);
        rb.setSelectedList(selectedList);
        rb.setOrderByClause(pb.getSqlComponentOfOrderByClause());
        rb.setPageSize(pb.getFetchSize());
        rb.setCurrentPageNumber(pb.getFetchPageNumber());
        return rb;
    }
}
