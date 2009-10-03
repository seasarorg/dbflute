/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.cbean;

import java.util.List;

/**
 * The invoker of paging.
 * @param <ENTITY> The type of entity.
 * @author jflute
 */
public class PagingInvoker<ENTITY> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _tableDbName;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public PagingInvoker(String tableDbName) {
        _tableDbName = tableDbName;
    }

    // ===================================================================================
    //                                                                              Invoke
    //                                                                              ======
    /**
     * Invoke select-page by handler.
     * @param handler The handler of paging. (NotNull)
     * @return The result bean of paging. (NotNull)
     */
    public PagingResultBean<ENTITY> invokePaging(PagingHandler<ENTITY> handler) {
        assertObjectNotNull("handler", handler);
        final PagingBean pagingBean = handler.getPagingBean();
        assertObjectNotNull("handler.getPagingBean()", pagingBean);
        if (!pagingBean.isFetchScopeEffective()) {
            String msg = "The paging bean is not effective about fetch-scope!";
            msg = msg + " When you select page, you should set up fetch-scope of";
            msg = msg + " paging bean(Should invoke fetchFirst() and fetchPage()!).";
            msg = msg + " The paging bean is: " + pagingBean;
            throw new IllegalStateException(msg);
        }
        final ResultBeanBuilder<ENTITY> builder = createResultBeanBuilder();
        final int allRecordCount;
        final List<ENTITY> selectedList;
        if (pagingBean.isCountLater()) {
            selectedList = handler.paging();
            if (isCurrentLastPage(selectedList, pagingBean)) {
                allRecordCount = deriveAllRecordCountFromLastPageValues(selectedList, pagingBean);
            } else {
                allRecordCount = handler.count();
            }
        } else {
            allRecordCount = handler.count();
            if (allRecordCount == 0) {
                pagingBean.xsetPaging(true); // restore its paging state
                selectedList = builder.buildEmptyListResultBean(pagingBean);
            } else {
                selectedList = handler.paging();
            }
        }
        final PagingResultBean<ENTITY> rb = builder.buildPagingResultBean(pagingBean, allRecordCount, selectedList);
        if (pagingBean.canPagingReSelect() && isNecessaryToReadPageAgain(rb)) {
            pagingBean.fetchPage(rb.getAllPageCount());
            final int reAllRecordCount = handler.count();
            final List<ENTITY> reSelectedList = handler.paging();
            return builder.buildPagingResultBean(pagingBean, reAllRecordCount, reSelectedList);
        } else {
            return rb;
        }
    }

    /**
     * Create the builder of result bean.
     * @return The builder of result bean. (NotNull)
     */
    protected ResultBeanBuilder<ENTITY> createResultBeanBuilder() {
        return new ResultBeanBuilder<ENTITY>(_tableDbName);
    }

    /**
     * Is the current page is last page?
     * @param selectedList The selected list. (NotNull)
     * @param pagingBean The bean of paging. (NotNull)
     * @return Determination.
     */
    protected boolean isCurrentLastPage(List<ENTITY> selectedList, PagingBean pagingBean) {
        // /- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  
        // It returns true if the size of list is NOT under fetch size(page size).
        // 
        // {For example}
        // If the fetch size is 20 and the size of selected list is 19 or less,
        // the current page must be last page(contains when only one page exists). 
        // it is NOT necessary to read count because the 19 is the hint to derive all record count.
        // 
        // If the fetch size is 20 and the size of selected list is 20,
        // it is necessary to read count because we cannot know whether the next pages exist or not.
        // - - - - - - - - - -/
        return selectedList.size() <= (pagingBean.getFetchSize() - 1);
    }

    /**
     * Derive all record count from last page values.
     * @param selectedList The selected list. (NotNull)
     * @param pagingBean The bean of paging. (NotNull)
     * @return Derived all record count.
     */
    protected int deriveAllRecordCountFromLastPageValues(List<ENTITY> selectedList, PagingBean pagingBean) {
        int baseSize = (pagingBean.getFetchPageNumber() - 1) * pagingBean.getFetchSize();
        return baseSize + selectedList.size();
    }

    /**
     * Is it necessary to read page again?
     * @param rb The result bean of paging. (NotNull)
     * @return Determination.
     */
    protected boolean isNecessaryToReadPageAgain(PagingResultBean<ENTITY> rb) {
        return rb.getAllRecordCount() > 0 && rb.getSelectedList().isEmpty();
    }

    // ===================================================================================
    //                                                                              Helper
    //                                                                              ======
    /**
     * Assert that the object is not null.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     * @exception IllegalArgumentException
     */
    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }
}
