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
package org.seasar.dbflute.helper.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.seasar.dbflute.helper.beans.exception.DfConstructorNotFoundException;
import org.seasar.dbflute.helper.beans.exception.DfFieldNotFoundException;
import org.seasar.dbflute.helper.beans.exception.DfMethodNotFoundException;
import org.seasar.dbflute.helper.beans.exception.DfPropertyNotFoundException;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public interface DfBeanDesc {

    // ===================================================================================
    //                                                                                Bean
    //                                                                                ====
    Class<?> getBeanClass();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    Constructor<?> getSuitableConstructor(Object[] args) throws DfConstructorNotFoundException;

    Constructor<?> getConstructor(Class<?>[] paramTypes);
    
    // ===================================================================================
    //                                                                            Property
    //                                                                            ========
    boolean hasPropertyDesc(String propertyName);

    DfPropertyDesc getPropertyDesc(String propertyName) throws DfPropertyNotFoundException;

    int getPropertyDescSize();
    
    List<String> getProppertyNameList();
    
    // ===================================================================================
    //                                                                               Field
    //                                                                               =====
    boolean hasField(String fieldName);

    Field getField(String fieldName) throws DfFieldNotFoundException;

    int getFieldSize();
    // ===================================================================================
    //                                                                              Method
    //                                                                              ======
    Method getMethod(String methodName) throws DfMethodNotFoundException;

    Method getMethod(String methodName, Class<?>[] paramTypes) throws DfMethodNotFoundException;

    Method getMethodNoException(String methodName);

    Method getMethodNoException(String methodName, Class<?>[] paramTypes);

    Method[] getMethods(String methodName) throws DfMethodNotFoundException;

    boolean hasMethod(String methodName);

    String[] getMethodNames();
    
    // ===================================================================================
    //                                                                          Reflection
    //                                                                          ==========
    Object newInstance(Object[] args) throws DfConstructorNotFoundException;
    Object invoke(Object target, String methodName, Object[] args) throws DfMethodNotFoundException;
    Object getFieldValue(String fieldName, Object target) throws DfFieldNotFoundException;
}