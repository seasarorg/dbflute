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
package org.dbflute.s2dao.beans.factory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import org.dbflute.s2dao.beans.ParameterizedClassDesc;

/**
 * {Refers to S2Container's utility and Extends it}
 * @author jflute
 */
public class ParameterizedClassDescFactory {

    protected static final String PROVIDER_CLASS_NAME = ParameterizedClassDescFactory.class.getName() + "Provider";
    protected static final Provider provider = createProvider();

    public static Map getTypeVariables(Class beanClass) {
        if (provider == null) {
            return Collections.EMPTY_MAP;
        }
        return provider.getTypeVariables(beanClass);
    }

    public static ParameterizedClassDesc createParameterizedClassDesc(final Field field, final Map map) {
        if (provider == null) {
            return null;
        }
        return provider.createParameterizedClassDesc(field, map);
    }

    public static ParameterizedClassDesc createParameterizedClassDesc(final Method method, final int index,
            final Map map) {
        if (provider == null) {
            return null;
        }
        return provider.createParameterizedClassDesc(method, index, map);
    }

    public static ParameterizedClassDesc createParameterizedClassDesc(final Method method, final Map map) {
        if (provider == null) {
            return null;
        }
        return provider.createParameterizedClassDesc(method, map);
    }

    protected static Provider createProvider() {
        try {
            final Class clazz = Class.forName(PROVIDER_CLASS_NAME);
            return (Provider) clazz.newInstance();
        } catch (final Exception e) {
            return null;
        }
    }

    public interface Provider {

        Map getTypeVariables(Class beanClass);

        ParameterizedClassDesc createParameterizedClassDesc(Field field, Map map);

        ParameterizedClassDesc createParameterizedClassDesc(Method method, int index, Map map);

        ParameterizedClassDesc createParameterizedClassDesc(Method method, Map map);
    }
}
