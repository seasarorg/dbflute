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

/**
 * The context for select-bean.
 * @author jflute
 */
public class SelectBeanContext {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static ThreadLocal<SelectBean> _threadLocal = new ThreadLocal<SelectBean>();

    // ===================================================================================
    //                                                                         Select Bean
    //                                                                         ===========
    /**
     * Get select-bean on thread.
     * @return The instance of select-bean. (Nullable)
     */
    public static SelectBean getSelectBeanOnThread() {
        return _threadLocal.get();
    }

    /**
     * Set select-bean on thread.
     * @param selectBean The instance of select-bean. (NotNull)
     */
    public static void setSelectBeanOnThread(SelectBean selectBean) {
        if (selectBean == null) {
            String msg = "The argument[selectBean] must not be null.";
            throw new IllegalArgumentException(msg);
        }
        _threadLocal.set(selectBean);
    }

    /**
     * Is existing select-bean on thread?
     * @return Determination.
     */
    public static boolean isExistSelectBeanOnThread() {
        return (_threadLocal.get() != null);
    }

    /**
     * Clear select-bean on thread.
     */
    public static void clearSelectBeanOnThread() {
        _threadLocal.set(null);
    }

    // ===================================================================================
    //                                                                Fetch Narrowing Bean
    //                                                                ====================
    /**
     * Get fetch-narrowing-bean on thread.
     * @return The instance of fetch-narrowing-bean. (Nullable)
     */
    public static FetchNarrowingBean getFetchNarrowingBeanOnThread() {
        if (isExistSelectBeanOnThread()) {
            final SelectBean bean = getSelectBeanOnThread();
            if (bean instanceof FetchNarrowingBean) {
                return (FetchNarrowingBean) bean;
            }
        }
        return null;
    }

    /**
     * Is existing fetch-narrowing-bean on thread?
     * @return Determination.
     */
    public static boolean isExistFetchNarrowingBeanOnThread() {
        return (getFetchNarrowingBeanOnThread() != null);
    }
}
