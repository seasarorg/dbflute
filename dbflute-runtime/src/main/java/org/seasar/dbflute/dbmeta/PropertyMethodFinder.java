/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.dbmeta;

import java.lang.reflect.Method;

/**
 * The interface of finder for property method.
 * @author jflute
 * @since 1.0.5G (2014/05/20 Tuesday)
 */
public interface PropertyMethodFinder {

    /**
     * Find the read method for the property.
     * @param beanType The type of bean that declares the method. (NotNull)
     * @param propertyName The name of property to find the method. (NotNull)
     * @param propertyType The type of property. (NotNull)
     * @return The found method. (NullAllowed: when not found)
     */
    Method findReadMethod(Class<?> beanType, String propertyName, Class<?> propertyType);

    /**
     * Find the write method for the property.
     * @param beanType The type of bean that declares the method. (NotNull)
     * @param propertyName The name of property to find the method. (NotNull)
     * @param propertyType The type of property. (NotNull)
     * @return The found method. (NullAllowed: when not found)
     */
    Method findWriteMethod(Class<?> beanType, String propertyName, Class<?> propertyType);
}
