package org.seasar.dbflute.mock;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.seasar.dbflute.jdbc.ValueType;

/**
 * @author jflute
 * @since 0.9.6.4 (2010/01/22 Friday)
 */
public class MockValueType implements ValueType {

    public void bindValue(PreparedStatement ps, int index, Object value) throws SQLException {
    }

    public void bindValue(CallableStatement cs, String parameterName, Object value) throws SQLException {
    }

    public int getSqlType() {
        return 0;
    }

    public Object getValue(ResultSet resultSet, int index) throws SQLException {
        return null;
    }

    public Object getValue(ResultSet resultSet, String columnName) throws SQLException {
        return null;
    }

    public Object getValue(CallableStatement cs, int index) throws SQLException {
        return null;
    }

    public Object getValue(CallableStatement cs, String parameterName) throws SQLException {
        return null;
    }

    public void registerOutParameter(CallableStatement cs, int index) throws SQLException {
    }

    public void registerOutParameter(CallableStatement cs, String parameterName) throws SQLException {
    }

    public String toText(Object value) {
        return null;
    }
}
