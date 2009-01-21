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
package org.seasar.dbflute.s2dao.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.seasar.dbflute.s2dao.beans.exception.TnConstructorNotFoundRuntimeException;
import org.seasar.dbflute.s2dao.beans.exception.TnFieldNotFoundRuntimeException;
import org.seasar.dbflute.s2dao.beans.exception.TnMethodNotFoundRuntimeException;
import org.seasar.dbflute.s2dao.beans.exception.TnPropertyNotFoundRuntimeException;

/**
 * {Refers to a S2Dao's class and Extends it}
 * @author jflute
 */
public interface TnBeanDesc {
    
    // ===================================================================================
    //                                                                                Bean
    //                                                                                ====
    Class<?> getBeanClass();
    
    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    Constructor<?> getSuitableConstructor(Object[] args) throws TnConstructorNotFoundRuntimeException;
    
    Constructor<?> getConstructor(Class<?>[] paramTypes);
    
    
    // ===================================================================================
    //                                                                            Property
    //                                                                            ========
    boolean hasPropertyDesc(String propertyName);

    TnPropertyDesc getPropertyDesc(String propertyName) throws TnPropertyNotFoundRuntimeException;

    int getPropertyDescSize();
    
    List<String> getProppertyNameList();
    
    // ===================================================================================
    //                                                                               Field
    //                                                                               =====
    boolean hasField(String fieldName);

    Field getField(String fieldName) throws TnFieldNotFoundRuntimeException;

    int getFieldSize();
    // ===================================================================================
    //                                                                              Method
    //                                                                              ======
    Method getMethod(String methodName) throws TnMethodNotFoundRuntimeException;

    Method getMethod(String methodName, Class<?>[] paramTypes) throws TnMethodNotFoundRuntimeException;

    Method getMethodNoException(String methodName);

    Method getMethodNoException(String methodName, Class<?>[] paramTypes);

    Method[] getMethods(String methodName) throws TnMethodNotFoundRuntimeException;

    boolean hasMethod(String methodName);

    String[] getMethodNames();
    
    // ===================================================================================
    //                                                                          Reflection
    //                                                                          ==========
    Object newInstance(Object[] args) throws TnConstructorNotFoundRuntimeException;
    Object invoke(Object target, String methodName, Object[] args) throws TnMethodNotFoundRuntimeException;
    Object getFieldValue(String fieldName, Object target) throws TnFieldNotFoundRuntimeException;
}