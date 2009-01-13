package org.dbflute.twowaysql;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.seasar.extension.jdbc.ValueType;

import org.dbflute.resource.ResourceContext;

/**
 * @author DBFlute(AutoGenerator)
 */
public class TnCompleteSqlBuilder {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final String NULL = "null";

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    private TnCompleteSqlBuilder() {
    }

    // ===================================================================================
    //                                                                        Complete SQL
    //                                                                        ============
    public static String getCompleteSql(String sql, Object[] args
                                        , String logDateFormat
                                        , String logTimestampFormat) {
        if (args == null || args.length == 0) {
            return sql;
        }
        return getCompleteSql(sql, args, new ValueType[args.length], logDateFormat, logTimestampFormat);
    }

    public static String getCompleteSql(String sql, Object[] args
                                        , ValueType[] valueTypes
                                        , String logDateFormat
                                        , String logTimestampFormat) {
        if (args == null || args.length == 0) {
            return sql;
        }
        StringBuilder buf = new StringBuilder(sql.length() + args.length * 15);
        int pos = 0;
        int pos2 = 0;
        int pos3 = 0;
        int pos4 = 0;
        int pos5 = 0;
        int pos6 = 0;
        int index = 0;
        while (true) {
            pos = sql.indexOf('?', pos2);
            pos3 = sql.indexOf('\'', pos2);
            pos4 = sql.indexOf('\'', pos3 + 1);
            pos5 = sql.indexOf("/*", pos2);
            pos6 = sql.indexOf("*/", pos5 + 1);
            if (pos > 0) {
                if (pos3 >= 0 && pos3 < pos && pos < pos4) {
                    buf.append(sql.substring(pos2, pos4 + 1));
                    pos2 = pos4 + 1;
                } else if (pos5 >= 0 && pos5 < pos && pos < pos6) {
                    buf.append(sql.substring(pos2, pos6 + 1));
                    pos2 = pos6 + 1;
                } else {
                    if (args.length <= index) {
                        String msg = "The size of bind arguments is illegal:";
                        msg = msg + " size=" + args.length + " sql=" + sql;
                        throw new IllegalStateException(msg);
                    }
                    buf.append(sql.substring(pos2, pos));
                    buf.append(getBindVariableText(args[index], valueTypes[index], logDateFormat, logTimestampFormat));
                    pos2 = pos + 1;
                    index++;
                }
            } else {
                buf.append(sql.substring(pos2));
                break;
            }
        }
        return buf.toString();
    }

    protected static String getLogDateFormat() {
        String logDateFormat = ResourceContext.getLogDateFormat();
        return logDateFormat != null ? logDateFormat : "yyyy-MM-dd";
    }

    protected static String getLogTimestampFormat() {
        String logTimestampFormat = ResourceContext.getLogTimestampFormat();
        return logTimestampFormat != null ? logTimestampFormat : "yyyy-MM-dd HH:mm:ss";
    }
	
    // ===================================================================================
    //                                                                  Bind Variable Text
    //                                                                  ==================
	// For various seasar's version.
	protected static final Class<?>[] TOTEXT_ARGUMENT_TYPES = new Class<?>[]{Object.class};
	protected static final Method TOTEXT_METHOD;
	static {
	    Method method = null;
	    try {
	        method = ValueType.class.getMethod("toText", TOTEXT_ARGUMENT_TYPES);
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
        }
		TOTEXT_METHOD = method;
	}

    public static String getBindVariableText(Object bindVariable, ValueType valueType
                                           , String logDateFormat
                                           , String logTimestampFormat) {
        if (valueType != null && TOTEXT_METHOD != null ) {
            try {
                return (String)TOTEXT_METHOD.invoke(valueType, new Object[]{bindVariable});
            } catch (IllegalArgumentException e) {
                String msg = "ValueType.toText() threw the IllegalArgumentException:";
                msg = msg + " valueType=" + valueType + " bindVariable=" + bindVariable;
                throw new IllegalStateException(msg, e);
            } catch (IllegalAccessException e) {
                String msg = "ValueType.toText() threw the IllegalAccessException:";
                msg = msg + " valueType=" + valueType + " bindVariable=" + bindVariable;
                throw new IllegalStateException(msg, e);
            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof RuntimeException) {
                    throw (RuntimeException)e.getTargetException();
                } else {
                    String msg = "ValueType.toText() threw the exception:";
                    msg = msg + " valueType=" + valueType + " bindVariable=" + bindVariable;
                    throw new IllegalStateException(msg, e.getTargetException());
                }
            }
        }
        return getBindVariableText(bindVariable, logDateFormat, logTimestampFormat);
    }

    public static String getBindVariableText(Object bindVariable
                                           , String logDateFormat
                                           , String logTimestampFormat) {
        if (bindVariable instanceof String) {
            return quote(bindVariable.toString());
        } else if (bindVariable instanceof Number) {
            return bindVariable.toString();
        } else if (bindVariable instanceof Time) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            return quote(sdf.format((java.util.Date) bindVariable));
        } else if (bindVariable instanceof Timestamp) {
            String format = logTimestampFormat != null ? logTimestampFormat : "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return quote(sdf.format((java.util.Date) bindVariable));
        } else if (bindVariable instanceof java.util.Date) {
            String format = logDateFormat != null ? logDateFormat : "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return quote(sdf.format((java.util.Date) bindVariable));
        } else if (bindVariable instanceof Boolean) {
            return bindVariable.toString();
        } else if (bindVariable == null) {
            return NULL;
        } else {
            return quote(bindVariable.toString());
        }
    }
	
    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    public static String nullText() {
        return NULL;
    }

    public static String toText(Number value) {
        if (value == null) {
            return NULL;
        }
        return value.toString();
    }

    public static String toText(Boolean value) {
        if (value == null) {
            return NULL;
        }
        return quote(value.toString());
    }

    public static String toText(String value) {
        if (value == null) {
            return NULL;
        }
        return quote(value);
    }

    public static String toText(Date value) {
        if (value == null) {
            return NULL;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);
        StringBuilder buf = new StringBuilder();
        addDate(buf, calendar);
        return quote(buf.toString());
    }

    public static String toText(Time value) {
        if (value == null) {
            return NULL;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);
        StringBuilder buf = new StringBuilder();
        addTime(buf, calendar);
        addTimeDecimalPart(buf, calendar.get(Calendar.MILLISECOND));
        return quote(buf.toString());
    }

    public static String toText(Timestamp value) {
        if (value == null) {
            return NULL;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);
        StringBuilder buf = new StringBuilder(30);
        addDate(buf, calendar);
        addTime(buf, calendar);
        addTimeDecimalPart(buf, value.getNanos());
        return quote(buf.toString());
    }

    public static String toText(byte[] value) {
        if (value == null) {
            return NULL;
        }
        return quote(value.toString() + "(byteLength=" + Integer.toString(value.length) + ")");
    }

    public static String toText(Object value) {
        if (value == null) {
            return NULL;
        }
        return quote(value.toString());
    }

	// yyyy-mm-dd
    protected static void addDate(StringBuilder buf, Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        buf.append(year);
        buf.append('-');
        int month = calendar.get(Calendar.MONTH) + 1;
        if (month < 10) {
            buf.append('0');
        }
        buf.append(month);
        buf.append('-');
        int date = calendar.get(Calendar.DATE);
        if (date < 10) {
            buf.append('0');
        }
        buf.append(date);
    }

	// hh:mm:ss
    protected static void addTime(StringBuilder buf, Calendar calendar) {
        if (buf.length() > 0) {
            buf.append(' ');
        }
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour < 10) {
            buf.append('0');
        }
        buf.append(hour);
        buf.append(':');
        int minute = calendar.get(Calendar.MINUTE);
        if (minute < 10) {
            buf.append('0');
        }
        buf.append(minute);
        buf.append(':');
        int second = calendar.get(Calendar.SECOND);
        if (second < 10) {
            buf.append('0');
        }
        buf.append(second);
    }

	// .000
    protected static void addTimeDecimalPart(StringBuilder buf, int decimalPart) {
        if (decimalPart == 0) {
            return;
        }
        if (buf.length() > 0) {
            buf.append('.');
        }
        buf.append(decimalPart);
    }

	// 'text'
    protected static String quote(String text) {
        return "'" + text + "'";
    }
}
