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
package org.seasar.dbflute.s2dao.beans.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.seasar.dbflute.s2dao.beans.TnBeanDesc;
import org.seasar.dbflute.s2dao.beans.impl.TnBeanDescImpl;

/**
 * {Refers to S2Container's utility and Extends it}
 * @author jflute
 */
public class TnBeanDescFactory {
    
    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static Map<Class<?>, TnBeanDesc> beanDescCache = new ConcurrentHashMap<Class<?>, TnBeanDesc>(1024);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected TnBeanDescFactory() {
    }

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    public static TnBeanDesc getBeanDesc(Class<?> clazz) {
        TnBeanDesc beanDesc = beanDescCache.get(clazz);
        if (beanDesc == null) {
            beanDesc = new TnBeanDescImpl(clazz);
            beanDescCache.put(clazz, beanDesc);
        }
        return beanDesc;
    }

    public static void clear() {
        beanDescCache.clear();
    }
}
