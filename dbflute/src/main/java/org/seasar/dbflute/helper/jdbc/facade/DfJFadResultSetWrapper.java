package org.seasar.dbflute.helper.jdbc.facade;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.seasar.dbflute.jdbc.ValueType;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/04/25 Monday)
 */
public class DfJFadResultSetWrapper {

    protected final ResultSet _rs;
    protected final Map<String, ValueType> _columnValueTypeMap;
    protected final DfJFadStringConverter _stringConverter;

    public DfJFadResultSetWrapper(ResultSet rs, Map<String, ValueType> columnValueTypeMap,
            DfJFadStringConverter stringConverter) {
        _rs = rs;
        _columnValueTypeMap = columnValueTypeMap;
        _stringConverter = stringConverter;
    }

    public boolean next() throws SQLException {
        return _rs.next();
    }

    public Object getObject(String columnName) throws SQLException {
        final ValueType valueType = _columnValueTypeMap.get(columnName);
        if (valueType != null) {
            return valueType.getValue(_rs, columnName);
        } else {
            return _rs.getObject(columnName);
        }
    }

    public String getString(String columnName) throws SQLException {
        final Object value = getObject(columnName);
        if (value == null) {
            return null;
        }
        if (_stringConverter != null) {
            return _stringConverter.convert(value);
        } else {
            return value != null ? value.toString() : null;
        }
    }
}
