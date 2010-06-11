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
package org.seasar.dbflute.s2dao.rshandler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.jdbc.ValueType;
import org.seasar.dbflute.s2dao.jdbc.TnResultSetHandler;
import org.seasar.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.dbflute.s2dao.metadata.impl.TnPropertyTypeImpl;
import org.seasar.dbflute.s2dao.valuetype.TnValueTypes;

/**
 * @author jflute
 */
public abstract class TnAbstractMapResultSetHandler implements TnResultSetHandler {

    protected TnPropertyType[] createPropertyTypes(ResultSetMetaData rsmd) throws SQLException {
        final int count = rsmd.getColumnCount();
        final TnPropertyType[] propertyTypes = new TnPropertyType[count];
        for (int i = 0; i < count; ++i) {
            final String propertyName = rsmd.getColumnLabel(i + 1);

            // because it can only use by-JDBC-type value type here 
            final ValueType valueType = TnValueTypes.getValueType(rsmd.getColumnType(i + 1));

            propertyTypes[i] = new TnPropertyTypeImpl(propertyName, valueType);
        }
        return propertyTypes;
    }

    protected Map<String, Object> createRow(ResultSet rs, TnPropertyType[] propertyTypes) throws SQLException {
        final Map<String, Object> row = StringKeyMap.createAsFlexibleOrdered();
        for (int i = 0; i < propertyTypes.length; ++i) {
            final Object value = propertyTypes[i].getValueType().getValue(rs, i + 1);
            row.put(propertyTypes[i].getPropertyName(), value);
        }
        return row;
    }
}
