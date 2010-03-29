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
package org.seasar.dbflute.s2dao.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.helper.beans.DfBeanDesc;
import org.seasar.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.dbflute.jdbc.ValueType;
import org.seasar.dbflute.resource.ResourceContext;
import org.seasar.dbflute.util.DfStringUtil;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnProcedureMetaDataFactory {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected TnFieldProcedureAnnotationReader _annotationReader = new TnFieldProcedureAnnotationReader();
    protected TnProcedureValueTypeProvider _valueTypeProvider = new TnProcedureValueTypeProvider();

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    public TnProcedureMetaData createProcedureMetaData(final String procedureName, final Class<?> pmbType) {
        final TnProcedureMetaData metaData = new TnProcedureMetaData(procedureName);
        if (pmbType == null) {
            return metaData;
        } else {
            if (!isDtoType(pmbType)) {
                throw new IllegalStateException("The pmb type was Not DTO type: " + pmbType.getName());
            }
        }
        final DfBeanDesc pmbDesc = DfBeanDescFactory.getBeanDesc(pmbType);

        // *Point
        final Stack<Class<?>> stack = new Stack<Class<?>>();
        for (Class<?> clazz = pmbType; clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
            stack.push(clazz);
        }
        for (; !stack.isEmpty();) {
            final Class<?> clazz = stack.pop();
            registerParameterType(metaData, pmbDesc, clazz.getDeclaredFields());
        }
        return metaData;
    }

    protected void registerParameterType(TnProcedureMetaData metaData, DfBeanDesc pmbDesc, Field[] fields) {
        for (Field field : fields) {
            if (!isInstanceField(field)) {
                continue;
            }
            final TnProcedureParameterType ppt = getProcedureParameterType(pmbDesc, field);
            if (ppt == null) {
                continue;
            }
            metaData.addParameterType(ppt);
        }
    }

    protected TnProcedureParameterType getProcedureParameterType(final DfBeanDesc dtoDesc, final Field field) {
        final String parameterInfo = _annotationReader.getParameterInfo(dtoDesc, field);
        if (parameterInfo == null) {
            return null;
        }
        final Map<String, String> parameterInfoMap = extractParameterInfoMap(parameterInfo, field);
        final String type = parameterInfoMap.get("type");
        field.setAccessible(true);
        final TnProcedureParameterType ppt = new TnProcedureParameterType(field);
        if (type.equalsIgnoreCase("in")) {
            ppt.setInType(true);
        } else if (type.equalsIgnoreCase("out")) {
            ppt.setOutType(true);
        } else if (type.equalsIgnoreCase("inout")) {
            ppt.setInType(true);
            ppt.setOutType(true);
        } else if (type.equalsIgnoreCase("return")) {
            ppt.setOutType(true);
            ppt.setReturnType(true);
        } else {
            String msg = "The parameter type should be 'in' or 'out' or 'inout' or 'return':";
            msg = msg + " class=" + field.getDeclaringClass().getSimpleName();
            msg = msg + " field=" + field.getName();
            msg = msg + " parameterType=" + type;
            throw new IllegalStateException(msg);
        }
        final Integer index = parseIndexAsInteger(parameterInfoMap.get("index"), parameterInfo, field);
        ppt.setParameterIndex(index);
        final ValueType valueType = findValueType(dtoDesc, field);
        ppt.setValueType(valueType);
        return ppt;
    }

    protected Map<String, String> extractParameterInfoMap(String parameterInfo, Field field) {
        final List<String> list = DfStringUtil.splitListTrimmed(parameterInfo, ",");
        if (list.size() != 2) {
            String msg = "The size of parameterInfo elements was illegal:";
            msg = msg + " elements=" + list + " info=" + parameterInfo + " name=" + field.getName();
            throw new IllegalStateException(msg);
        }
        final Map<String, String> map = new HashMap<String, String>();
        map.put("type", list.get(0));
        map.put("index", list.get(1));
        return map;
    }

    protected Integer parseIndexAsInteger(String index, String parameterInfo, Field field) {
        try {
            return DfTypeUtil.toInteger(index);
        } catch (NumberFormatException e) {
            String msg = "Failed to parse the parameter index as Integer:";
            msg = msg + " index=" + index + " info=" + parameterInfo + " parameter=" + field.getName();
            throw new IllegalStateException(msg);
        }
    }

    protected ValueType findValueType(DfBeanDesc dtoDesc, Field field) {
        final String name = _annotationReader.getValueType(dtoDesc, field);
        final Class<?> type = field.getType();
        final DBDef currentDBDef = ResourceContext.currentDBDef();
        return _valueTypeProvider.provideValueType(name, type, currentDBDef);
    }

    protected boolean isCurrentDBDef(DBDef currentDBDef) {
        return ResourceContext.isCurrentDBDef(currentDBDef);
    }

    protected boolean isInstanceField(Field field) {
        final int mod = field.getModifiers();
        return !Modifier.isStatic(mod) && !Modifier.isFinal(mod);
    }

    protected boolean isDtoType(Class<?> clazz) {
        return !isSimpleType(clazz) && !isContainerType(clazz);
    }

    protected boolean isSimpleType(Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz");
        }
        return clazz == String.class || clazz.isPrimitive() || clazz == Boolean.class || clazz == Character.class
                || Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz)
                || Calendar.class.isAssignableFrom(clazz) || clazz == byte[].class;
    }

    protected boolean isContainerType(Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz");
        }
        return Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz) || clazz.isArray();
    }

    protected static class TnFieldProcedureAnnotationReader {
        protected static final String PARAMETER_SUFFIX = "_PROCEDURE_PARAMETER";
        protected static final String VALUE_TYPE_SUFFIX = "_VALUE_TYPE";

        public String getParameterInfo(DfBeanDesc dtoDesc, Field field) {
            String fieldName = removeInstanceVariablePrefix(field.getName()); // don't forget
            String annotationName = fieldName + PARAMETER_SUFFIX;
            if (dtoDesc.hasField(annotationName)) {
                Field f = dtoDesc.getField(annotationName);
                return (String) getValue(f, null);
            } else {
                return null;
            }
        }

        public String getValueType(DfBeanDesc dtoDesc, Field field) {
            String fieldName = removeInstanceVariablePrefix(field.getName()); // don't forget
            String annotationName = fieldName + VALUE_TYPE_SUFFIX;
            if (dtoDesc.hasField(annotationName)) {
                Field f = dtoDesc.getField(annotationName);
                return (String) getValue(f, null);
            } else {
                return null;
            }
        }

        protected String removeInstanceVariablePrefix(String fieldName) {
            return fieldName.startsWith("_") ? fieldName.substring("_".length()) : fieldName;
        }

        protected Object getValue(Field field, Object target) {
            try {
                return field.get(target);
            } catch (IllegalAccessException e) {
                String msg = "The getting of the field threw the exception:";
                msg = msg + " class=" + field.getDeclaringClass().getSimpleName();
                msg = msg + " field=" + field.getName();
                throw new IllegalStateException(msg, e);
            }
        }
    }
}
