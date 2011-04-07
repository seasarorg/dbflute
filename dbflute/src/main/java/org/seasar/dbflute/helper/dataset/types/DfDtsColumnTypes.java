package org.seasar.dbflute.helper.dataset.types;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Table. {Refer to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDtsColumnTypes {

    public static final DfDtsColumnType STRING = new DfDtsStringType();

    public static final DfDtsColumnType NOT_TRIM_STRING = new DfDtsStringType(false);

    public static final DfDtsColumnType BIGDECIMAL = new DfDtsBigDecimalType();

    public static final DfDtsColumnType TIMESTAMP = new DfDtsTimestampType();

    public static final DfDtsColumnType BINARY = new DfDtsBinaryType();

    public static final DfDtsColumnType OBJECT = new DfDtsObjectType();

    public static final DfDtsColumnType BOOLEAN = new DfDtsBooleanType();

    private static Map<Class<?>, DfDtsColumnType> typesByClass = new HashMap<Class<?>, DfDtsColumnType>();

    private static Map<Integer, DfDtsColumnType> typesBySqlType = new HashMap<Integer, DfDtsColumnType>();

    static {
        registerColumnType(String.class, STRING);
        registerColumnType(short.class, BIGDECIMAL);
        registerColumnType(Short.class, BIGDECIMAL);
        registerColumnType(int.class, BIGDECIMAL);
        registerColumnType(Integer.class, BIGDECIMAL);
        registerColumnType(long.class, BIGDECIMAL);
        registerColumnType(Long.class, BIGDECIMAL);
        registerColumnType(float.class, BIGDECIMAL);
        registerColumnType(Float.class, BIGDECIMAL);
        registerColumnType(double.class, BIGDECIMAL);
        registerColumnType(Double.class, BIGDECIMAL);
        registerColumnType(boolean.class, BOOLEAN);
        registerColumnType(Boolean.class, BOOLEAN);
        registerColumnType(BigDecimal.class, BIGDECIMAL);
        registerColumnType(Timestamp.class, TIMESTAMP);
        registerColumnType(java.sql.Date.class, TIMESTAMP);
        registerColumnType(java.util.Date.class, TIMESTAMP);
        registerColumnType(Calendar.class, TIMESTAMP);
        registerColumnType(new byte[0].getClass(), BINARY);

        registerColumnType(Types.TINYINT, BIGDECIMAL);
        registerColumnType(Types.SMALLINT, BIGDECIMAL);
        registerColumnType(Types.INTEGER, BIGDECIMAL);
        registerColumnType(Types.BIGINT, BIGDECIMAL);
        registerColumnType(Types.REAL, BIGDECIMAL);
        registerColumnType(Types.FLOAT, BIGDECIMAL);
        registerColumnType(Types.DOUBLE, BIGDECIMAL);
        registerColumnType(Types.DECIMAL, BIGDECIMAL);
        registerColumnType(Types.NUMERIC, BIGDECIMAL);
        registerColumnType(Types.BOOLEAN, BOOLEAN);
        registerColumnType(Types.DATE, TIMESTAMP);
        registerColumnType(Types.TIME, TIMESTAMP);
        registerColumnType(Types.TIMESTAMP, TIMESTAMP);
        registerColumnType(Types.BINARY, BINARY);
        registerColumnType(Types.VARBINARY, BINARY);
        registerColumnType(Types.LONGVARBINARY, BINARY);
        registerColumnType(Types.CHAR, STRING);
        registerColumnType(Types.LONGVARCHAR, STRING);
        registerColumnType(Types.VARCHAR, STRING);
    }

    protected DfDtsColumnTypes() {
    }

    // *unused on DBFlute
    //    public static ValueType getValueType(int type) {
    //        switch (type) {
    //        case Types.TINYINT:
    //        case Types.SMALLINT:
    //        case Types.INTEGER:
    //        case Types.BIGINT:
    //        case Types.REAL:
    //        case Types.FLOAT:
    //        case Types.DOUBLE:
    //        case Types.DECIMAL:
    //        case Types.NUMERIC:
    //            return ValueTypes.BIGDECIMAL;
    //        case Types.BOOLEAN:
    //            return ValueTypes.BOOLEAN;
    //        case Types.DATE:
    //        case Types.TIME:
    //        case Types.TIMESTAMP:
    //            return ValueTypes.TIMESTAMP;
    //        case Types.BINARY:
    //        case Types.VARBINARY:
    //        case Types.LONGVARBINARY:
    //            return ValueTypes.BINARY;
    //        case Types.CHAR:
    //        case Types.LONGVARCHAR:
    //        case Types.VARCHAR:
    //            return ValueTypes.STRING;
    //        default:
    //            return ValueTypes.OBJECT;
    //        }
    //    }

    public static DfDtsColumnType getColumnType(int type) {
        DfDtsColumnType columnType = (DfDtsColumnType) typesBySqlType.get(new Integer(type));
        if (columnType != null) {
            return columnType;
        }
        return OBJECT;
    }

    public static DfDtsColumnType getColumnType(Object value) {
        if (value == null) {
            return OBJECT;
        }
        return getColumnType(value.getClass());
    }

    public static DfDtsColumnType getColumnType(Class<?> clazz) {
        DfDtsColumnType columnType = (DfDtsColumnType) typesByClass.get(clazz);
        if (columnType != null) {
            return columnType;
        }
        return OBJECT;
    }

    public static DfDtsColumnType registerColumnType(int sqlType, DfDtsColumnType columnType) {
        return (DfDtsColumnType) typesBySqlType.put(new Integer(sqlType), columnType);
    }

    public static DfDtsColumnType registerColumnType(Class<?> clazz, DfDtsColumnType columnType) {
        return (DfDtsColumnType) typesByClass.put(clazz, columnType);
    }
}