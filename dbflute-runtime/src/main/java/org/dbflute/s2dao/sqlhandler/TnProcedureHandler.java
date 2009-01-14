package org.dbflute.s2dao.sqlhandler;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.dbflute.helper.StringKeyMap;
import org.dbflute.jdbc.StatementFactory;
import org.dbflute.s2dao.metadata.TnPropertyType;
import org.dbflute.s2dao.metadata.impl.TnPropertyTypeImpl;
import org.dbflute.s2dao.procedure.TnProcedureMetaData;
import org.dbflute.s2dao.procedure.TnProcedureParameterType;
import org.dbflute.s2dao.valuetype.TnValueType;
import org.dbflute.s2dao.valuetype.TnValueTypes;
import org.dbflute.s2dao.jdbc.ResultSetHandler;

/**
 * @author DBFlute(AutoGenerator)
 */
public class TnProcedureHandler extends TnBasicSelectHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private TnProcedureMetaData procedureMetaData;
		
    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnProcedureHandler(final DataSource dataSource, final String sql,
            final ResultSetHandler resultSetHandler, final StatementFactory statementFactory,
            final TnProcedureMetaData procedureMetaData) {
        super(dataSource, sql, resultSetHandler, statementFactory);
        this.procedureMetaData = procedureMetaData;
    }
	
    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @SuppressWarnings("unchecked")
    public Object execute(final Connection connection, final Object[] args, final Class[] argTypes) {
        final Object dto = getArgumentDto(args);
        logSql(args, argTypes);
        CallableStatement cs = null;
        try {
            cs = prepareCallableStatement(connection);
            bindArgs(cs, dto);
            Object returnValue = null; 
            if (cs.execute()) {
                final ResultSet resultSet = cs.getResultSet();
                if (resultSet != null) {
                    final ResultSetHandler handler = createReturnResultSetHandler(resultSet);
                    try {
                        returnValue = handler.handle(resultSet);
                    } finally {
                        if (resultSet != null) {
                            resultSet.close();
                        }
                    }
                }
            }
            return handleOutParameters(cs, dto, returnValue);
        } catch (SQLException e) {
            handleSQLException(e, cs);
            return null;// Unreachable!
        } finally {
            close(cs);
        }
    }

    protected ResultSetHandler createReturnResultSetHandler(ResultSet resultSet) {
        return new InternalMapListResultSetHandler();
    }

    @Override
    protected String getCompleteSql(final Object[] args) {// for Procedure Call
        String sql = getSql();
        Object dto = getArgumentDto(args);
        if (args == null || dto == null) {
            return sql;
        }
        StringBuilder sb = new StringBuilder(100);
        int pos = 0;
        int pos2 = 0;
        for (TnProcedureParameterType ppt : procedureMetaData.parameterTypes()) {
            if ((pos2 = sql.indexOf('?', pos)) < 0) {
                break;
            }
            sb.append(sql.substring(pos, pos2));
            pos = pos2 + 1;
            if (ppt.isInType()) {
                sb.append(getBindVariableText(ppt.getValue(dto)));
            } else {
                sb.append(sql.substring(pos2, pos));
            }
        }
        sb.append(sql.substring(pos));
        return sb.toString();
    }

    protected CallableStatement prepareCallableStatement(final Connection connection) {
        if (getSql() == null) { throw new IllegalStateException("The SQL should not be null!"); }
        return getStatementFactory().createCallableStatement(connection, getSql());
    }

    protected void bindArgs(final CallableStatement cs, final Object dto) throws SQLException {
        if (dto == null) { return; }
        int i = 0;
        for (TnProcedureParameterType ppt : procedureMetaData.parameterTypes()) {
            final TnValueType valueType = ppt.getValueType();
            if (ppt.isOutType()) {
                valueType.registerOutParameter(cs, i + 1);
            }
            if (ppt.isInType()) {
                final Object value = ppt.getValue(dto);
                valueType.bindValue(cs, i + 1, value);
            }
            ++i;
        }
    }

    protected Object handleResultSet(final CallableStatement cs) throws SQLException {
        ResultSet rs = null;
        try {
            rs = getResultSet(cs);
            return getResultSetHandler().handle(rs);
        } finally {
            close(rs);
        }
    }

    protected ResultSet getResultSet(Statement statement)  {
        try {
            return statement.getResultSet();
        } catch (SQLException e) {
            handleSQLException(e, statement);
            return null;// Unreachable!
        }
    }

    protected Object handleOutParameters(final CallableStatement cs, final Object dto, Object returnValue) throws SQLException {
        if (dto == null) {
            return null;
        }
        int i = 0;
        for (TnProcedureParameterType ppt : procedureMetaData.parameterTypes()) {
            final TnValueType valueType = ppt.getValueType();
            if (ppt.isOutType()) {
                Object value = valueType.getValue(cs, i + 1);
                if (value instanceof ResultSet) {
                    final ResultSet resultSet = (ResultSet) value;
                    final ResultSetHandler handler = createOutParameterResultSetHandler(ppt, resultSet);
                    try {
                        value = handler.handle(resultSet);
                    } finally {
                        if (resultSet != null) {
                            resultSet.close();
                        }
                    }
                }
                ppt.setValue(dto, value);
            } else if (ppt.isReturnType()) {
                ppt.setValue(dto, returnValue);
            }
            ++i;
        }
        return dto;
    }

    protected Object getArgumentDto(Object[] args) {
        if (args.length == 0) {
            return null;
        }
        if (args.length == 1) {
            if (args[0] == null) {
                throw new IllegalArgumentException("args[0] should not be null!");
            }
            return args[0];
        }
        throw new IllegalArgumentException("args");
    }

    protected ResultSetHandler createOutParameterResultSetHandler(TnProcedureParameterType ppt, ResultSet resultSet) {
        return new InternalMapListResultSetHandler();
    }

	// ===================================================================================
    //                                                              Map Result Set Handler
    //                                                              ======================
    protected static abstract class InternalAbstractMapResultSetHandler implements ResultSetHandler {

        protected Map<String, Object> createRow(ResultSet rs, TnPropertyType[] propertyTypes) throws SQLException {
            Map<String, Object> row = StringKeyMap.createAsFlexible();
            for (int i = 0; i < propertyTypes.length; ++i) {
                Object value = propertyTypes[i].getValueType().getValue(rs, i + 1);
                row.put(propertyTypes[i].getPropertyName(), value);
            }
            return row;
        }

        protected TnPropertyType[] createPropertyTypes(ResultSetMetaData rsmd) throws SQLException {
            int count = rsmd.getColumnCount();
            TnPropertyType[] propertyTypes = new TnPropertyType[count];
            for (int i = 0; i < count; ++i) {
                String propertyName = rsmd.getColumnLabel(i + 1);
                TnValueType valueType = TnValueTypes.getValueType(rsmd.getColumnType(i + 1));
                propertyTypes[i] = new TnPropertyTypeImpl(propertyName, valueType);
            }
            return propertyTypes;
        }
    }

    protected static class InternalMapListResultSetHandler extends InternalAbstractMapResultSetHandler {

        public Object handle(ResultSet resultSet) throws SQLException {
            TnPropertyType[] propertyTypes = createPropertyTypes(resultSet.getMetaData());
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            while (resultSet.next()) {
                list.add(createRow(resultSet, propertyTypes));
            }
            return list;
        }
    }
}
