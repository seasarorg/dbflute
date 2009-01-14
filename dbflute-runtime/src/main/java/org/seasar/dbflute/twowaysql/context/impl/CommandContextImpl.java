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
package org.seasar.dbflute.twowaysql.context.impl;

import java.util.ArrayList;
import java.util.List;

import ognl.OgnlRuntime;

import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.twowaysql.context.CommandContext;
import org.seasar.dbflute.twowaysql.context.CommandContextPropertyAccessor;

/**
 * @author jflute
 */
public class CommandContextImpl implements CommandContext {

    private StringKeyMap<Object> args = StringKeyMap.createAsCaseInsensitiveConcurrent();
    private StringKeyMap<Class<?>> argTypes = StringKeyMap.createAsCaseInsensitiveConcurrent();
    private StringBuffer sqlBuf = new StringBuffer(100);
    private List<Object> bindVariables = new ArrayList<Object>();
    private List<Class<?>> bindVariableTypes = new ArrayList<Class<?>>();
    private boolean enabled = true;
    private CommandContext parent;

    static {
        OgnlRuntime.setPropertyAccessor(CommandContext.class, new CommandContextPropertyAccessor());
    }

    public CommandContextImpl() {
    }

    public CommandContextImpl(CommandContext parent) {
        this.parent = parent;
        enabled = false;
    }

    public Object getArg(String name) {
        if (args.containsKey(name)) {
            return args.get(name);
        } else if (parent != null) {
            return parent.getArg(name);
        } else {
            if (args.size() == 1) {
                String firstKey = args.keySet().iterator().next();
                return args.get(firstKey);
            }
            return null;
        }
    }

    public Class<?> getArgType(String name) {
        if (argTypes.containsKey(name)) {
            return (Class<?>) argTypes.get(name);
        } else if (parent != null) {
            return parent.getArgType(name);
        } else {
            if (argTypes.size() == 1) {
                String firstKey = argTypes.keySet().iterator().next();
                return argTypes.get(firstKey);
            }
            return null;
        }
    }

    public void addArg(String name, Object arg, Class<?> argType) {
        args.put(name, arg);
        argTypes.put(name, argType);
    }

    public String getSql() {
        return sqlBuf.toString();
    }

    public Object[] getBindVariables() {
        return bindVariables.toArray(new Object[bindVariables.size()]);
    }

    public Class<?>[] getBindVariableTypes() {
        return (Class<?>[]) bindVariableTypes.toArray(new Class[bindVariableTypes.size()]);
    }

    public CommandContext addSql(String sql) {
        sqlBuf.append(sql);
        return this;
    }

    public CommandContext addSql(String sql, Object bindVariable, Class<?> bindVariableType) {
        sqlBuf.append(sql);
        bindVariables.add(bindVariable);
        bindVariableTypes.add(bindVariableType);
        return this;
    }

    public CommandContext addSql(String sql, Object[] bindVariables, Class<?>[] bindVariableTypes) {
        sqlBuf.append(sql);
        for (int i = 0; i < bindVariables.length; ++i) {
            this.bindVariables.add(bindVariables[i]);
            this.bindVariableTypes.add(bindVariableTypes[i]);
        }
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}