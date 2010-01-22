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
package org.seasar.dbflute.s2dao.valuetype.basic;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.seasar.dbflute.s2dao.valuetype.TnAbstractValueType;
import org.seasar.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 */
public class UtilDateAsTimestampType extends TnAbstractValueType {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final TimestampType _timestampType = new TimestampType();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public UtilDateAsTimestampType() {
        super(Types.TIMESTAMP);
    }

    // ===================================================================================
    //                                                                           Get Value
    //                                                                           =========
    public Object getValue(ResultSet resultSet, int index) throws SQLException {
        return toUtilDate(_timestampType.getValue(resultSet, index));
    }

    public Object getValue(ResultSet resultSet, String columnName) throws SQLException {
        return toUtilDate(_timestampType.getValue(resultSet, columnName));
    }

    public Object getValue(CallableStatement cs, int index) throws SQLException {
        return toUtilDate(_timestampType.getValue(cs, index));
    }

    public Object getValue(CallableStatement cs, String parameterName) throws SQLException {
        return toUtilDate(_timestampType.getValue(cs, parameterName));
    }

    // ===================================================================================
    //                                                                          Bind Value
    //                                                                          ==========
    public void bindValue(PreparedStatement ps, int index, Object value) throws SQLException {
        _timestampType.bindValue(ps, index, toTimestamp(value));
    }

    public void bindValue(CallableStatement cs, String parameterName, Object value) throws SQLException {
        _timestampType.bindValue(cs, parameterName, toTimestamp(value));
    }

    // ===================================================================================
    //                                                                             To Text
    //                                                                             =======
    public String toText(Object value) {
        return _timestampType.toText(value);
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected java.util.Date toUtilDate(Object value) {
        try {
            return DfTypeUtil.toDate(value);
        } catch (RuntimeException e) {
            String msg = "Failed to convert the object to java.util.Date:";
            msg = msg + " type=" + (value != null ? value.getClass() : null) + " value=" + value;
            throw new IllegalStateException(msg);
        }
    }

    protected java.sql.Timestamp toTimestamp(Object value) {
        try {
            return DfTypeUtil.toTimestamp(value);
        } catch (RuntimeException e) {
            String msg = "Failed to convert the object to java.sql.Timestamp:";
            msg = msg + " type=" + (value != null ? value.getClass() : null) + " value=" + value;
            throw new IllegalStateException(msg);
        }
    }
}