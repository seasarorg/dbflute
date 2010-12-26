/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.s2dao.sqlhandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.jdbc.ValueType;
import org.seasar.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.dbflute.twowaysql.context.CommandContext;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnCommandContextHandler extends TnAbstractBasicSqlHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final CommandContext _commandContext;
    protected List<TnPropertyType> _boundPropTypeList; // not required

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnCommandContextHandler(DataSource dataSource, StatementFactory statementFactory, String sql,
            CommandContext commandContext) {
        super(dataSource, statementFactory, sql);
        assertObjectNotNull("commandContext", commandContext);
        _commandContext = commandContext;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public int execute(Object[] args) {
        final Connection conn = getConnection();
        try {
            return doExecute(conn, _commandContext);
        } finally {
            close(conn);
        }
    }

    protected int doExecute(Connection conn, CommandContext ctx) {
        logSql(ctx.getBindVariables(), getArgTypes(ctx.getBindVariables()));
        final PreparedStatement ps = prepareStatement(conn);
        int ret = -1;
        try {
            final Object[] bindVariables = ctx.getBindVariables();
            final Class<?>[] bindVariableTypes = ctx.getBindVariableTypes();
            if (hasPropertyTypeList()) {
                final int index = bindFirstScope(conn, ps, bindVariables, bindVariableTypes);
                bindSecondScope(conn, ps, bindVariables, bindVariableTypes, index);
            } else {
                bindArgs(conn, ps, bindVariables, bindVariableTypes);
            }
            ret = executeUpdate(ps);
        } finally {
            close(ps);
        }
        return ret;
    }

    protected boolean hasPropertyTypeList() {
        return _boundPropTypeList != null && !_boundPropTypeList.isEmpty();
    }

    // ===================================================================================
    //                                                                          Bind Scope
    //                                                                          ==========
    protected int bindFirstScope(Connection conn, PreparedStatement ps, Object[] bindVariables,
            Class<?>[] bindVariableTypes) {
        final List<Object> firstVariableList = new ArrayList<Object>();
        final List<ValueType> firstValueTypeList = new ArrayList<ValueType>();
        int index = 0;
        for (TnPropertyType propertyType : _boundPropTypeList) {
            firstVariableList.add(bindVariables[index]);
            firstValueTypeList.add(propertyType.getValueType());
            ++index;
        }
        bindArgs(conn, ps, firstVariableList.toArray(), firstValueTypeList.toArray(new ValueType[0]));
        return index;
    }

    protected void bindSecondScope(Connection conn, PreparedStatement ps, Object[] bindVariables,
            Class<?>[] bindVariableTypes, int index) {
        bindArgs(conn, ps, bindVariables, bindVariableTypes, index);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<TnPropertyType> getBoundPropTypeList() {
        return _boundPropTypeList;
    }

    public void setBoundPropTypeList(List<TnPropertyType> boundPropTypeList) {
        this._boundPropTypeList = boundPropTypeList;
    }
}
