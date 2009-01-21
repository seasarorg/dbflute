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
    protected boolean _countLater;

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
            msg = msg + " When you select page, you should set up fetch-scope of paging bean(Should invoke fetchFirst() and fetchPage()!).";
            msg = msg + " The paging bean is: " + pagingBean;
            throw new IllegalStateException(msg);
        }
        final int allRecordCount;
        final List<ENTITY> selectedList;
        if (_countLater) {
            selectedList = handler.paging();
            allRecordCount = handler.count();
        } else {
            allRecordCount = handler.count();
            selectedList = handler.paging();
        }
        final PagingResultBean<ENTITY> rb = new ResultBeanBuilder<ENTITY>(_tableDbName).buildPagingResultBean(pagingBean, allRecordCount, selectedList);
        if (pagingBean.canPagingReSelect() && isNecessaryToReadPageAgain(rb)) {
            pagingBean.fetchPage(rb.getAllPageCount());
            final int reAllRecordCount = handler.count();
            final java.util.List<ENTITY> reSelectedList = handler.paging();
            return new ResultBeanBuilder<ENTITY>(_tableDbName).buildPagingResultBean(pagingBean, reAllRecordCount, reSelectedList);
        } else {
            return rb;
        }
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
    //                                                                              Option
    //                                                                              ======
    public PagingInvoker<ENTITY> countLater() {
        _countLater = true; return this;
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
            String msg = "The value should not be null: variableName=" + variableName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }
}
