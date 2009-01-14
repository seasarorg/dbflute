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
package org.dbflute.s2dao.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.dbflute.s2dao.beans.exception.ConstructorNotFoundRuntimeException;
import org.dbflute.s2dao.beans.exception.FieldNotFoundRuntimeException;
import org.dbflute.s2dao.beans.exception.MethodNotFoundRuntimeException;
import org.dbflute.s2dao.beans.exception.PropertyNotFoundRuntimeException;

/**
 * {Refers to S2Container's utility and Extends it}
 * @author jflute
 */
public interface BeanDesc {
    
    // ===================================================================================
    //                                                                                Bean
    //                                                                                ====
    Class<?> getBeanClass();
    
    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    Constructor<?> getSuitableConstructor(Object[] args) throws ConstructorNotFoundRuntimeException;
    
    Constructor<?> getConstructor(Class<?>[] paramTypes);
    
    
    // ===================================================================================
    //                                                                            Property
    //                                                                            ========
    boolean hasPropertyDesc(String propertyName);

    PropertyDesc getPropertyDesc(String propertyName) throws PropertyNotFoundRuntimeException;

    int getPropertyDescSize();
    
    List<String> getProppertyNameList();
    
    // ===================================================================================
    //                                                                               Field
    //                                                                               =====
    boolean hasField(String fieldName);

    Field getField(String fieldName) throws FieldNotFoundRuntimeException;

    int getFieldSize();
    // ===================================================================================
    //                                                                              Method
    //                                                                              ======
    Method getMethod(String methodName) throws MethodNotFoundRuntimeException;

    Method getMethod(String methodName, Class<?>[] paramTypes) throws MethodNotFoundRuntimeException;

    Method getMethodNoException(String methodName);

    Method getMethodNoException(String methodName, Class<?>[] paramTypes);

    Method[] getMethods(String methodName) throws MethodNotFoundRuntimeException;

    boolean hasMethod(String methodName);

    String[] getMethodNames();
    
    // ===================================================================================
    //                                                                          Reflection
    //                                                                          ==========
    Object newInstance(Object[] args) throws ConstructorNotFoundRuntimeException;
    Object invoke(Object target, String methodName, Object[] args) throws MethodNotFoundRuntimeException;
    Object getFieldValue(String fieldName, Object target) throws FieldNotFoundRuntimeException;
}