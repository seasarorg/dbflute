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
 * Fetch-Narrowing-Bean context. (referring to s2pager)
 * @author jflute
 */
public class FetchNarrowingBeanContext {

    /** The thread-local for this. */
    private static ThreadLocal<FetchNarrowingBean> _threadLocal = new ThreadLocal<FetchNarrowingBean>();

    /**
     * Get fetch-narrowing-bean on thread.
     * @return Condition-bean context. (Nullable)
     */
    public static FetchNarrowingBean getFetchNarrowingBeanOnThread() {
        return (FetchNarrowingBean)_threadLocal.get();
    }

    /**
     * Set fetch-narrowing-bean on thread.
     * @param cb Condition-bean. (NotNull)
     */
    public static void setFetchNarrowingBeanOnThread(FetchNarrowingBean cb) {
        if (cb == null) {
            String msg = "The argument[cb] must not be null.";
            throw new IllegalArgumentException(msg);
        }
        _threadLocal.set(cb);
    }

    /**
     * Is existing fetch-narrowing-bean on thread?
     * @return Determination.
     */
    public static boolean isExistFetchNarrowingBeanOnThread() {
        return (_threadLocal.get() != null);
    }

    /**
     * Clear fetch-narrowing-bean on thread.
     */
    public static void clearFetchNarrowingBeanOnThread() {
        _threadLocal.set(null);
    }

    /**
     * Is the argument fetch-narrowing-bean?
     * @param dtoInstance Dto instance.
     * @return Determination.
     */
    public static boolean isTheArgumentFetchNarrowingBean(final Object dtoInstance) {
        if (dtoInstance instanceof FetchNarrowingBean) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Is the type fetch-narrowing-bean?
     * @param dtoClass DtoClass.
     * @return Determination.
     */
    public static boolean isTheTypeFetchNarrowingBean(final Class<?> dtoClass) {
        if (FetchNarrowingBean.class.isAssignableFrom(dtoClass)) {
            return true;
        } else {
            return false;
        }
    }
}
