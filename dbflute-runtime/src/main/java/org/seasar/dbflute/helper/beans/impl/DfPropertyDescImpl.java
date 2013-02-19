/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Time;
import java.sql.Timestamp;

import org.seasar.dbflute.helper.beans.DfBeanDesc;
import org.seasar.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.dbflute.helper.beans.exception.DfBeanIllegalPropertyException;
import org.seasar.dbflute.util.DfReflectionUtil;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * @author modified by jflute (originated in S2Dao)
 */
public class DfPropertyDescImpl implements DfPropertyDesc {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Object[] EMPTY_ARGS = new Object[0];

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String _propertyName;
    private Class<?> _propertyType;
    private Method _readMethod;
    private Method _writeMethod;
    private Field _field;
    private DfBeanDesc _beanDesc;
    private Constructor<?> _stringConstructor;
    private Method _valueOfMethod;
    private boolean _readable = false;
    private boolean _writable = false;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfPropertyDescImpl(String propertyName, Class<?> propertyType, Method readMethod, Method writeMethod,
            DfBeanDesc beanDesc) {
        this(propertyName, propertyType, readMethod, writeMethod, null, beanDesc);
    }

    public DfPropertyDescImpl(String propertyName, Class<?> propertyType, Method readMethod, Method writeMethod,
            Field field, DfBeanDesc beanDesc) {
        if (propertyName == null) {
            String msg = "The argument 'propertyName' should not be null!";
            throw new IllegalArgumentException(msg);
        }
        if (propertyType == null) {
            String msg = "The argument 'propertyType' should not be null!";
            throw new IllegalArgumentException(msg);
        }
        _propertyName = propertyName;
        _propertyType = propertyType;
        setReadMethod(readMethod);
        setWriteMethod(writeMethod);
        setField(field);
        _beanDesc = beanDesc;
        setupStringConstructor();
        setupValueOfMethod();
    }

    private void setupStringConstructor() {
        final Constructor<?>[] cons = _propertyType.getConstructors();
        for (int i = 0; i < cons.length; ++i) {
            final Constructor<?> con = cons[i];
            if (con.getParameterTypes().length == 1 && con.getParameterTypes()[0].equals(String.class)) {
                _stringConstructor = con;
                break;
            }
        }
    }

    private void setupValueOfMethod() {
        final Method[] methods = _propertyType.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            final Method method = methods[i];
            if (DfReflectionUtil.isBridgeMethod(method) || DfReflectionUtil.isSyntheticMethod(method)) {
                continue;
            }
            if (DfReflectionUtil.isStatic(method.getModifiers()) && method.getName().equals("valueOf")
                    && method.getParameterTypes().length == 1 && method.getParameterTypes()[0].equals(String.class)) {
                _valueOfMethod = method;
                break;
            }
        }
    }

    // ===================================================================================
    //                                                                                Bean
    //                                                                                ====
    public DfBeanDesc getBeanDesc() {
        return _beanDesc;
    }

    // ===================================================================================
    //                                                                            Property
    //                                                                            ========
    public final String getPropertyName() {
        return _propertyName;
    }

    public final Class<?> getPropertyType() {
        return _propertyType;
    }

    // ===================================================================================
    //                                                                              Method
    //                                                                              ======
    public final Method getReadMethod() {
        return _readMethod;
    }

    public final void setReadMethod(Method readMethod) {
        this._readMethod = readMethod;
        if (readMethod != null) {
            _readable = true;
        }
    }

    public final boolean hasReadMethod() {
        return _readMethod != null;
    }

    public final Method getWriteMethod() {
        return _writeMethod;
    }

    public final void setWriteMethod(Method writeMethod) {
        this._writeMethod = writeMethod;
        if (writeMethod != null) {
            _writable = true;
        }
    }

    public final boolean hasWriteMethod() {
        return _writeMethod != null;
    }

    // ===================================================================================
    //                                                                               Field
    //                                                                               =====
    public Field getField() {
        return _field;
    }

    public void setField(Field field) {
        this._field = field;
        if (field != null && DfReflectionUtil.isPublic(field.getModifiers())) {
            _readable = true;
            _writable = true;
        }
    }

    // ===================================================================================
    //                                                                               Value
    //                                                                               =====
    /**
     * {@inheritDoc}
     */
    public final Object getValue(Object target) {
        try {
            if (!_readable) {
                final Class<?> beanClass = _beanDesc.getBeanClass();
                String msg = DfTypeUtil.toClassTitle(beanClass) + "." + _propertyName;
                msg = msg + " is not readable.";
                throw new IllegalStateException(msg);
            } else if (hasReadMethod()) {
                return DfReflectionUtil.invoke(_readMethod, target, EMPTY_ARGS);
            } else {
                return DfReflectionUtil.getValue(_field, target);
            }
        } catch (Throwable t) {
            throw new DfBeanIllegalPropertyException(_beanDesc.getBeanClass(), _propertyName, t);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final void setValue(Object target, Object value) {
        try {
            value = convertIfNeed(value);
            if (!_writable) {
                final Class<?> beanClass = _beanDesc.getBeanClass();
                String msg = DfTypeUtil.toClassTitle(beanClass) + "." + _propertyName;
                msg = msg + " is not writable.";
                throw new IllegalStateException(msg);
            } else if (hasWriteMethod()) {
                DfReflectionUtil.invoke(_writeMethod, target, new Object[] { value });
            } else {
                DfReflectionUtil.setValue(_field, target, value);
            }
        } catch (Throwable t) {
            throw new DfBeanIllegalPropertyException(_beanDesc.getBeanClass(), _propertyName, t);
        }
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean isReadable() {
        return _readable;
    }

    public boolean isWritable() {
        return _writable;
    }

    // ===================================================================================
    //                                                                             Convert
    //                                                                             =======
    public Object convertIfNeed(Object arg) {
        if (_propertyType.isPrimitive()) {
            return convertPrimitiveWrapper(arg);
        } else if (Number.class.isAssignableFrom(_propertyType)) {
            return convertNumber(arg);
        } else if (java.util.Date.class.isAssignableFrom(_propertyType)) {
            return convertDate(arg);
        } else if (Boolean.class.isAssignableFrom(_propertyType)) {
            return DfTypeUtil.toBoolean(arg);
        } else if (arg != null && arg.getClass() != String.class && String.class == _propertyType) {
            return arg.toString();
        } else if (arg instanceof String && !String.class.equals(_propertyType)) {
            return convertWithString(arg);
        } else if (java.util.Calendar.class.isAssignableFrom(_propertyType)) {
            return DfTypeUtil.toCalendar(arg);
        }
        return arg;
    }

    private Object convertPrimitiveWrapper(Object arg) {
        return DfTypeUtil.toWrapper(arg, _propertyType);
    }

    private Object convertNumber(Object arg) {
        return DfTypeUtil.toNumber(arg, _propertyType);
    }

    private Object convertDate(Object arg) {
        if (_propertyType == java.util.Date.class) {
            return DfTypeUtil.toDate(arg);
        } else if (_propertyType == Timestamp.class) {
            return DfTypeUtil.toTimestamp(arg);
        } else if (_propertyType == java.sql.Date.class) {
            return DfTypeUtil.toDate(arg);
        } else if (_propertyType == Time.class) {
            return DfTypeUtil.toTime(arg);
        }
        return arg;
    }

    private Object convertWithString(Object arg) {
        if (_stringConstructor != null) {
            return DfReflectionUtil.newInstance(_stringConstructor, new Object[] { arg });
        }
        if (_valueOfMethod != null) {
            return DfReflectionUtil.invoke(_valueOfMethod, null, new Object[] { arg });
        }
        return arg;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public final String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("propertyName=");
        buf.append(_propertyName);
        buf.append(",propertyType=");
        buf.append(_propertyType.getName());
        buf.append(",readMethod=");
        buf.append(_readMethod != null ? _readMethod.getName() : "null");
        buf.append(",writeMethod=");
        buf.append(_writeMethod != null ? _writeMethod.getName() : "null");
        return buf.toString();
    }
}
