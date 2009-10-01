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

import org.seasar.dbflute.helper.beans.exception.DfConstructorNotFoundRuntimeException;
import org.seasar.dbflute.helper.beans.exception.DfFieldNotFoundRuntimeException;
import org.seasar.dbflute.helper.beans.exception.DfMethodNotFoundRuntimeException;
import org.seasar.dbflute.helper.beans.exception.DfPropertyNotFoundRuntimeException;

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
    Constructor<?> getSuitableConstructor(Object[] args) throws DfConstructorNotFoundRuntimeException;

    Constructor<?> getConstructor(Class<?>[] paramTypes);
    
    // ===================================================================================
    //                                                                            Property
    //                                                                            ========
    boolean hasPropertyDesc(String propertyName);

    DfPropertyDesc getPropertyDesc(String propertyName) throws DfPropertyNotFoundRuntimeException;

    int getPropertyDescSize();
    
    List<String> getProppertyNameList();
    
    // ===================================================================================
    //                                                                               Field
    //                                                                               =====
    boolean hasField(String fieldName);

    Field getField(String fieldName) throws DfFieldNotFoundRuntimeException;

    int getFieldSize();
    // ===================================================================================
    //                                                                              Method
    //                                                                              ======
    Method getMethod(String methodName) throws DfMethodNotFoundRuntimeException;

    Method getMethod(String methodName, Class<?>[] paramTypes) throws DfMethodNotFoundRuntimeException;

    Method getMethodNoException(String methodName);

    Method getMethodNoException(String methodName, Class<?>[] paramTypes);

    Method[] getMethods(String methodName) throws DfMethodNotFoundRuntimeException;

    boolean hasMethod(String methodName);

    String[] getMethodNames();
    
    // ===================================================================================
    //                                                                          Reflection
    //                                                                          ==========
    Object newInstance(Object[] args) throws DfConstructorNotFoundRuntimeException;
    Object invoke(Object target, String methodName, Object[] args) throws DfMethodNotFoundRuntimeException;
    Object getFieldValue(String fieldName, Object target) throws DfFieldNotFoundRuntimeException;
}