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
package org.seasar.dbflute.helper.beans.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.helper.beans.DfBeanDesc;
import org.seasar.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.dbflute.helper.beans.exception.DfBeanFieldNotFoundException;
import org.seasar.dbflute.helper.beans.exception.DfBeanMethodNotFoundException;
import org.seasar.dbflute.helper.beans.exception.DfBeanPropertyNotFoundException;
import org.seasar.dbflute.util.DfReflectionUtil;
import org.seasar.dbflute.util.DfTypeUtil;
import org.seasar.dbflute.util.Srl;

/**
 * @author modified by jflute (originated in S2Dao)
 */
public class DfBeanDescImpl implements DfBeanDesc {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final Object[] EMPTY_ARGS = new Object[0];
    protected static final Class<?>[] EMPTY_PARAM_TYPES = new Class<?>[0];

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Class<?> _beanClass;

    // #retire needs to validate cost-benefit performance (balance with safety)
    // and lazy load is not enough to get performance because Java reflection is not flexible 
    //
    // e.g. if false -> true (property, method, field)
    // |       |  MemberCB   |  MemberCQ    |
    // | false | 33, 132, 24 | 104, 293, 88 |
    // | true  | 33,  77, 24 |  66, 176, 88 |
    //
    //protected final boolean _readOnly; // cannot block public field but OK, basically for saving memory
    //protected final DfBeanDescSetupFilter _setupFilter; // might be null

    protected final StringKeyMap<DfPropertyDesc> _propertyDescMap = StringKeyMap.createAsCaseInsensitive();
    protected final Map<String, Method[]> _methodsMap = new HashMap<String, Method[]>();
    protected final Map<String, Field> _fieldMap = new HashMap<String, Field>();

    protected final transient Set<String> _invalidPropertyNames = new HashSet<String>();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfBeanDescImpl(Class<?> beanClass) {
        if (beanClass == null) {
            String msg = "The argument 'beanClass' should not be null!";
            throw new IllegalArgumentException(msg);
        }
        _beanClass = beanClass;

        setupPropertyDesc();
        setupMethod();
        setupField();
    }

    // ===================================================================================
    //                                                                                Bean
    //                                                                                ====
    public Class<?> getBeanClass() {
        return _beanClass;
    }

    // ===================================================================================
    //                                                                   Property Handling
    //                                                                   =================
    public boolean hasPropertyDesc(String propertyName) {
        return getPropertyDescInternally(propertyName) != null;
    }

    public DfPropertyDesc getPropertyDesc(String propertyName) throws DfBeanPropertyNotFoundException {
        DfPropertyDesc pd = getPropertyDescInternally(propertyName);
        if (pd == null) {
            throw new DfBeanPropertyNotFoundException(_beanClass, propertyName);
        }
        return pd;
    }

    private DfPropertyDesc getPropertyDescInternally(String propertyName) {
        return _propertyDescMap.get(propertyName);
    }

    public int getPropertyDescSize() {
        return _propertyDescMap.size();
    }

    public List<String> getProppertyNameList() {
        return new ArrayList<String>(_propertyDescMap.keySet());
    }

    // ===================================================================================
    //                                                                      Field Handling
    //                                                                      ==============
    public boolean hasField(String fieldName) {
        return _fieldMap.get(fieldName) != null;
    }

    public Field getField(String fieldName) {
        Field field = (Field) _fieldMap.get(fieldName);
        if (field == null) {
            throw new DfBeanFieldNotFoundException(_beanClass, fieldName);
        }
        return field;
    }

    public int getFieldSize() {
        return _fieldMap.size();
    }

    // ===================================================================================
    //                                                                     Method Handling
    //                                                                     ===============
    public Method getMethod(String methodName) throws DfBeanMethodNotFoundException {
        return getMethod(methodName, EMPTY_PARAM_TYPES);
    }

    public Method getMethod(String methodName, Class<?>[] paramTypes) throws DfBeanMethodNotFoundException {
        final Method method = getMethodNoException(methodName, paramTypes);
        if (method == null) {
            throw new DfBeanMethodNotFoundException(_beanClass, methodName, paramTypes);
        }
        return method;
    }

    public Method getMethodNoException(String methodName) {
        return getMethodNoException(methodName, EMPTY_PARAM_TYPES);
    }

    public Method getMethodNoException(String methodName, Class<?>[] paramTypes) {
        final Method[] methods = findMethods(methodName);
        if (methods == null) {
            return null;
        }
        for (Method method : methods) {
            if (Arrays.equals(paramTypes, method.getParameterTypes())) {
                return method;
            }
        }
        return null;
    }

    public Method[] getMethods(String methodName) throws DfBeanMethodNotFoundException {
        final Method[] methods = findMethods(methodName);
        if (methods == null) {
            throw new DfBeanMethodNotFoundException(_beanClass, methodName, null);
        }
        return methods;
    }

    public boolean hasMethod(String methodName) {
        return _methodsMap.get(methodName) != null;
    }

    protected Method[] findMethods(String methodName) {
        return (Method[]) _methodsMap.get(methodName);
    }

    // ===================================================================================
    //                                                                     Set up Property
    //                                                                     ===============
    protected void setupPropertyDesc() {
        final Method[] methods = _beanClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            final Method method = methods[i];
            if (DfReflectionUtil.isBridgeMethod(method) || DfReflectionUtil.isSyntheticMethod(method)) {
                continue;
            }
            final String methodName = method.getName();
            if (methodName.startsWith("get")) {
                if (method.getParameterTypes().length != 0 || methodName.equals("getClass")
                        || method.getReturnType() == void.class) {
                    continue;
                }
                final String propertyName = initBeansProp(methodName.substring(3));
                setupReadMethod(method, propertyName);
            } else if (methodName.startsWith("is")) {
                if (method.getParameterTypes().length != 0 || !method.getReturnType().equals(Boolean.TYPE)
                        && !method.getReturnType().equals(Boolean.class)) {
                    continue;
                }
                final String propertyName = initBeansProp(methodName.substring(2));
                setupReadMethod(method, propertyName);
            } else if (methodName.startsWith("set")) {
                if (method.getParameterTypes().length != 1 || methodName.equals("setClass")
                        || method.getReturnType() != void.class) {
                    continue;
                }
                final String propertyName = initBeansProp(methodName.substring(3));
                setupWriteMethod(method, propertyName);
            }
        }
        for (Iterator<String> i = _invalidPropertyNames.iterator(); i.hasNext();) {
            _propertyDescMap.remove(i.next());
        }
        _invalidPropertyNames.clear();
    }

    protected static String initBeansProp(String name) {
        return Srl.initBeansProp(name);
    }

    protected void addPropertyDesc(DfPropertyDesc propertyDesc) {
        if (propertyDesc == null) {
            String msg = "The argument 'propertyDesc' should not be null!";
            throw new IllegalArgumentException(msg);
        }
        _propertyDescMap.put(propertyDesc.getPropertyName(), propertyDesc);
    }

    protected void setupReadMethod(Method readMethod, String propertyName) {
        final Class<?> propertyType = readMethod.getReturnType();
        DfPropertyDesc propDesc = getPropertyDescInternally(propertyName);
        if (propDesc != null) {
            if (!propDesc.getPropertyType().equals(propertyType)) {
                _invalidPropertyNames.add(propertyName);
            } else {
                propDesc.setReadMethod(readMethod);
            }
        } else {
            propDesc = createPropertyDesc(propertyName, propertyType, readMethod, null, null);
            addPropertyDesc(propDesc);
        }
    }

    protected void setupWriteMethod(Method writeMethod, String propertyName) {
        final Class<?> propertyType = writeMethod.getParameterTypes()[0];
        DfPropertyDesc propDesc = getPropertyDescInternally(propertyName);
        if (propDesc != null) {
            if (!propDesc.getPropertyType().equals(propertyType)) {
                _invalidPropertyNames.add(propertyName);
            } else {
                propDesc.setWriteMethod(writeMethod);
            }
        } else {
            propDesc = createPropertyDesc(propertyName, propertyType, null, writeMethod, null);
            addPropertyDesc(propDesc);
        }
    }

    // ===================================================================================
    //                                                                       Set up Method
    //                                                                       =============
    protected void setupMethod() {
        final Map<String, List<Method>> methodListMap = new LinkedHashMap<String, List<Method>>();
        final Method[] methods = _beanClass.getMethods();
        for (Method method : methods) {
            if (DfReflectionUtil.isBridgeMethod(method) || DfReflectionUtil.isSyntheticMethod(method)) {
                continue;
            }
            if (Object.class.equals(method.getDeclaringClass())) {
                continue;
            }
            final String methodName = method.getName();
            List<Method> list = (List<Method>) methodListMap.get(methodName);
            if (list == null) {
                list = new ArrayList<Method>();
                methodListMap.put(methodName, list);
            }
            list.add(method);
        }
        for (Entry<String, List<Method>> entry : methodListMap.entrySet()) {
            final String key = entry.getKey();
            final List<Method> methodList = entry.getValue();
            _methodsMap.put(key, methodList.toArray(new Method[methodList.size()]));
        }
    }

    // ===================================================================================
    //                                                                        Set up Field
    //                                                                        ============
    protected void setupField() {
        setupFields(_beanClass);
    }

    protected void setupFields(Class<?> targetClass) {
        if (targetClass.isInterface()) {
            setupFieldsByInterface(targetClass);
        } else {
            setupFieldsByClass(targetClass);
        }
    }

    protected void setupFieldsByInterface(Class<?> interfaceClass) {
        addFields(interfaceClass);
        final Class<?>[] interfaces = interfaceClass.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            setupFieldsByInterface(interfaces[i]);
        }
    }

    protected void addFields(Class<?> clazz) {
        final Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            final String fieldName = field.getName();
            if (_fieldMap.containsKey(fieldName)) { // target class's fields have priority  
                continue;
            }
            field.setAccessible(true);
            _fieldMap.put(fieldName, field);
            if (DfReflectionUtil.isInstanceVariableField(field)) {
                if (hasPropertyDesc(fieldName)) {
                    final DfPropertyDesc pd = getPropertyDesc(fieldName);
                    pd.setField(field);
                } else if (DfReflectionUtil.isPublicField(field)) {
                    final DfPropertyDesc pd = createPropertyDesc(fieldName, field.getType(), null, null, field);
                    _propertyDescMap.put(fieldName, pd);
                }
            }
        }
    }

    protected void setupFieldsByClass(Class<?> targetClass) {
        addFields(targetClass); // should be set up at first
        final Class<?>[] interfaces = targetClass.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            setupFieldsByInterface(interfaces[i]);
        }
        final Class<?> superClass = targetClass.getSuperclass();
        if (superClass != Object.class && superClass != null) {
            setupFieldsByClass(superClass);
        }
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected DfPropertyDesc createPropertyDesc(String propertyName, Class<?> propertyType, Method readMethod,
            Method writeMethod, Field field) {
        return new DfPropertyDescImpl(this, propertyName, propertyType, readMethod, writeMethod, field);
    }

    protected boolean adjustNumber(Class<?>[] paramTypes, Object[] args, int index) {
        if (paramTypes[index].isPrimitive()) {
            if (paramTypes[index] == int.class) {
                args[index] = DfTypeUtil.toInteger(args[index]);
                return true;
            } else if (paramTypes[index] == double.class) {
                args[index] = DfTypeUtil.toDouble(args[index]);
                return true;
            } else if (paramTypes[index] == long.class) {
                args[index] = DfTypeUtil.toLong(args[index]);
                return true;
            } else if (paramTypes[index] == short.class) {
                args[index] = DfTypeUtil.toShort(args[index]);
                return true;
            } else if (paramTypes[index] == float.class) {
                args[index] = DfTypeUtil.toFloat(args[index]);
                return true;
            }
        } else {
            if (paramTypes[index] == Integer.class) {
                args[index] = DfTypeUtil.toInteger(args[index]);
                return true;
            } else if (paramTypes[index] == Double.class) {
                args[index] = DfTypeUtil.toDouble(args[index]);
                return true;
            } else if (paramTypes[index] == Long.class) {
                args[index] = DfTypeUtil.toLong(args[index]);
                return true;
            } else if (paramTypes[index] == Short.class) {
                args[index] = DfTypeUtil.toShort(args[index]);
                return true;
            } else if (paramTypes[index] == Float.class) {
                args[index] = DfTypeUtil.toFloat(args[index]);
                return true;
            }
        }
        return false;
    }
}
