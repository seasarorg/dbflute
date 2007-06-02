/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
package org.seasar.dbflute;

import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.S2ContainerFactory;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

/**
 * Component Provider.
 * <pre>
 * app.diconに定義したComoponentを提供する。
 * ContainerにはS2Containerを利用する。
 * </pre>
 * @author jflute
 */
public class DfComponentProvider {

    /** Container. */
    public static S2Container _container;

    /**
     * Get component.
     * 
     * @param type Class type. (NotNull)
     * @return Component. (NotNull)
     */
    public static Object getComponent(Class type) {
        initializeIfNeed(type);
        return _container.getComponent(type);
    }

    /**
     * Initialize if it needs.s
     * 
     * @param type Class type. (NotNull)
     */
    private static void initializeIfNeed(Class type) {
        if (_container == null) {
            final ClassLoader classLoader = type.getClassLoader();
            final String configPath = SingletonS2ContainerFactory.getConfigPath();
            _container = S2ContainerFactory.create(configPath, classLoader);
        }
    }
}