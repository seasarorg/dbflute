package org.seasar.dbflute.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {Refers to S2Container's utility and Extends it}
 * @author jflute
 */
public class DfTypeUtil {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final String NULL = "null";

    // ===================================================================================
    //                                                                          Convert To
    //                                                                          ==========
    // -----------------------------------------------------
    //                                               Boolean
    //                                               -------
    public static Boolean toBoolean(Object o) {
        if (o == null) {
            return null;
        } else if (o instanceof Boolean) {
            return (Boolean) o;
        } else if (o instanceof Number) {
            int num = ((Number) o).intValue();
            return Boolean.valueOf(num != 0);
        } else if (o instanceof String) {
            String s = (String) o;
            if ("true".equalsIgnoreCase(s)) {
                return Boolean.TRUE;
            } else if ("false".equalsIgnoreCase(s)) {
                return Boolean.FALSE;
            } else if (s.equals("0")) {
                return Boolean.FALSE;
            } else {
                return Boolean.TRUE;
            }
        } else {
            return Boolean.TRUE;
        }
    }

    public static boolean toPrimitiveBoolean(Object o) {
        Boolean b = toBoolean(o);
        if (b != null) {
            return b.booleanValue();
        }
        return false;
    }

    // -----------------------------------------------------
    //                                               Integer
    //                                               -------
    public static Integer toInteger(Object o) {
        return toInteger(o, null);
    }

    public static Integer toInteger(Object o, String pattern) {
        if (o == null) {
            return null;
        } else if (o instanceof Integer) {
            return (Integer) o;
        } else if (o instanceof Number) {
            return new Integer(((Number) o).intValue());
        } else if (o instanceof String) {
            return toInteger((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return new Integer(new SimpleDateFormat(pattern).format(o));
            }
            return new Integer((int) ((java.util.Date) o).getTime());
        } else if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue() ? new Integer(1) : new Integer(0);
        } else {
            return toInteger(o.toString());
        }
    }

    protected static Integer toInteger(String s) {
        if (s == null || s.trim().length() == 0) {
            return null;
        }
        return new Integer(normalize(s));
    }

    public static int toPrimitiveInt(Object o) {
        return toPrimitiveInt(o, null);
    }

    public static int toPrimitiveInt(Object o, String pattern) {
        if (o == null) {
            return 0;
        } else if (o instanceof Number) {
            return ((Number) o).intValue();
        } else if (o instanceof String) {
            return toPrimitiveInt((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return Integer.parseInt(new SimpleDateFormat(pattern).format(o));
            }
            return (int) ((java.util.Date) o).getTime();
        } else if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue() ? 1 : 0;
        } else {
            return toPrimitiveInt(o.toString());
        }
    }

    protected static int toPrimitiveInt(String s) {
        if (s == null || s.trim().length() == 0) {
            return 0;
        }
        return Integer.parseInt(normalize(s));
    }

    // -----------------------------------------------------
    //                                                  Long
    //                                                  ----
    public static Long toLong(Object o) {
        return toLong(o, null);
    }

    public static Long toLong(Object o, String pattern) {
        if (o == null) {
            return null;
        } else if (o instanceof Long) {
            return (Long) o;
        } else if (o instanceof Number) {
            return new Long(((Number) o).longValue());
        } else if (o instanceof String) {
            return toLong((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return new Long(new SimpleDateFormat(pattern).format(o));
            }
            return new Long(((java.util.Date) o).getTime());
        } else if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue() ? new Long(1) : new Long(0);
        } else {
            return toLong(o.toString());
        }
    }

    protected static Long toLong(String s) {
        if (s == null || s.trim().length() == 0) {
            return null;
        }
        return new Long(normalize(s));
    }

    public static long toPrimitiveLong(Object o) {
        return toPrimitiveLong(o, null);
    }

    public static long toPrimitiveLong(Object o, String pattern) {
        if (o == null) {
            return 0;
        } else if (o instanceof Number) {
            return ((Number) o).longValue();
        } else if (o instanceof String) {
            return toPrimitiveLong((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return Long.parseLong(new SimpleDateFormat(pattern).format(o));
            }
            return ((java.util.Date) o).getTime();
        } else if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue() ? 1 : 0;
        } else {
            return toPrimitiveLong(o.toString());
        }
    }

    protected static long toPrimitiveLong(String s) {
        if (s == null || s.trim().length() == 0) {
            return 0;
        }
        return Long.parseLong(normalize(s));
    }

    // -----------------------------------------------------
    //                                            BigDecimal
    //                                            ----------
    public static BigDecimal toBigDecimal(Object o) {
        return toBigDecimal(o, null);
    }

    public static BigDecimal toBigDecimal(Object o, String pattern) {
        if (o == null) {
            return null;
        } else if (o instanceof BigDecimal) {
            return (BigDecimal) o;
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return new BigDecimal(new SimpleDateFormat(pattern).format(o));
            }
            return new BigDecimal(Long.toString(((java.util.Date) o).getTime()));
        } else if (o instanceof String) {
            String s = (String) o;
            if (s == null || s.trim().length() == 0) {
                return null;
            }
            return new BigDecimal(new BigDecimal(s).toPlainString());
        } else {
            return new BigDecimal(new BigDecimal(o.toString()).toPlainString());
        }
    }

    // -----------------------------------------------------
    //                                                Double
    //                                                ------
    public static Double toDouble(Object o) {
        return toDouble(o, null);
    }

    public static Double toDouble(Object o, String pattern) {
        if (o == null) {
            return null;
        } else if (o instanceof Double) {
            return (Double) o;
        } else if (o instanceof Number) {
            return new Double(((Number) o).doubleValue());
        } else if (o instanceof String) {
            return toDouble((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return new Double(new SimpleDateFormat(pattern).format(o));
            }
            return new Double(((java.util.Date) o).getTime());
        } else {
            return toDouble(o.toString());
        }
    }

    protected static Double toDouble(String s) {
        if (s == null || s.trim().length() == 0) {
            return null;
        }
        return new Double(normalize(s));
    }

    public static double toPrimitiveDouble(Object o) {
        return toPrimitiveDouble(o, null);
    }

    public static double toPrimitiveDouble(Object o, String pattern) {
        if (o == null) {
            return 0;
        } else if (o instanceof Number) {
            return ((Number) o).doubleValue();
        } else if (o instanceof String) {
            return toPrimitiveDouble((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return Double.parseDouble(new SimpleDateFormat(pattern).format(o));
            }
            return ((java.util.Date) o).getTime();
        } else {
            return toPrimitiveDouble(o.toString());
        }
    }

    private static double toPrimitiveDouble(String s) {
        if (DfStringUtil.isEmpty(s)) {
            return 0;
        }
        return Double.parseDouble(normalize(s));
    }

    // -----------------------------------------------------
    //                                                 Float
    //                                                 -----
    public static Float toFloat(Object o) {
        return toFloat(o, null);
    }

    public static Float toFloat(Object o, String pattern) {
        if (o == null) {
            return null;
        } else if (o instanceof Float) {
            return (Float) o;
        } else if (o instanceof Number) {
            return new Float(((Number) o).floatValue());
        } else if (o instanceof String) {
            return toFloat((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return new Float(new SimpleDateFormat(pattern).format(o));
            }
            return new Float(((java.util.Date) o).getTime());
        } else {
            return toFloat(o.toString());
        }
    }

    protected static Float toFloat(String s) {
        if (s == null || s.trim().length() == 0) {
            return null;
        }
        return new Float(normalize(s));
    }

    public static float toPrimitiveFloat(Object o) {
        return toPrimitiveFloat(o, null);
    }

    public static float toPrimitiveFloat(Object o, String pattern) {
        if (o == null) {
            return 0;
        } else if (o instanceof Number) {
            return ((Number) o).floatValue();
        } else if (o instanceof String) {
            return toPrimitiveFloat((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return Float.parseFloat(new SimpleDateFormat(pattern).format(o));
            }
            return ((java.util.Date) o).getTime();
        } else {
            return toPrimitiveFloat(o.toString());
        }
    }

    private static float toPrimitiveFloat(String s) {
        if (DfStringUtil.isEmpty(s)) {
            return 0;
        }
        return Float.parseFloat(normalize(s));
    }

    // -----------------------------------------------------
    //                                                 Short
    //                                                 -----
    public static Short toShort(Object o) {
        return toShort(o, null);
    }

    public static Short toShort(Object o, String pattern) {
        if (o == null) {
            return null;
        } else if (o instanceof Short) {
            return (Short) o;
        } else if (o instanceof Number) {
            return new Short(((Number) o).shortValue());
        } else if (o instanceof String) {
            return toShort((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return new Short(new SimpleDateFormat(pattern).format(o));
            }
            return new Short((short) ((java.util.Date) o).getTime());
        } else if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue() ? new Short((short) 1) : new Short((short) 0);
        } else {
            return toShort(o.toString());
        }
    }

    protected static Short toShort(String s) {
        if (s == null || s.trim().length() == 0) {
            return null;
        }
        return new Short(normalize(s));
    }

    public static short toPrimitiveShort(Object o) {
        return toPrimitiveShort(o, null);
    }

    public static short toPrimitiveShort(Object o, String pattern) {
        if (o == null) {
            return 0;
        } else if (o instanceof Number) {
            return ((Number) o).shortValue();
        } else if (o instanceof String) {
            return toPrimitiveShort((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return Short.parseShort(new SimpleDateFormat(pattern).format(o));
            }
            return (short) ((java.util.Date) o).getTime();
        } else if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue() ? (short) 1 : (short) 0;
        } else {
            return toPrimitiveShort(o.toString());
        }
    }

    private static short toPrimitiveShort(String s) {
        if (DfStringUtil.isEmpty(s)) {
            return 0;
        }
        return Short.parseShort(normalize(s));
    }

    // -----------------------------------------------------
    //                                            BigInteger
    //                                            ----------
    public static BigInteger toBigInteger(Object o) {
        return toBigInteger(o, null);
    }

    public static BigInteger toBigInteger(Object o, String pattern) {
        if (o == null) {
            return null;
        } else if (o instanceof BigInteger) {
            return (BigInteger) o;
        } else {
            Long l = toLong(o, pattern);
            if (l == null) {
                return null;
            }
            return BigInteger.valueOf(l.longValue());
        }
    }

    // -----------------------------------------------------
    //                                                  Byte
    //                                                  ----
    public static Byte toByte(Object o) {
        return toByte(o, null);
    }

    public static Byte toByte(Object o, String pattern) {
        if (o == null) {
            return null;
        } else if (o instanceof Byte) {
            return (Byte) o;
        } else if (o instanceof Number) {
            return new Byte(((Number) o).byteValue());
        } else if (o instanceof String) {
            return toByte((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return new Byte(new SimpleDateFormat(pattern).format(o));
            }
            return new Byte((byte) ((java.util.Date) o).getTime());
        } else if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue() ? new Byte((byte) 1) : new Byte((byte) 0);
        } else {
            return toByte(o.toString());
        }
    }

    protected static Byte toByte(String s) {
        if (s == null || s.trim().length() == 0) {
            return null;
        }
        return new Byte(normalize(s));
    }

    public static byte toPrimitiveByte(Object o) {
        return toPrimitiveByte(o, null);
    }

    public static byte toPrimitiveByte(Object o, String pattern) {
        if (o == null) {
            return 0;
        } else if (o instanceof Number) {
            return ((Number) o).byteValue();
        } else if (o instanceof String) {
            return toPrimitiveByte((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return Byte.parseByte(new SimpleDateFormat(pattern).format(o));
            }
            return (byte) ((java.util.Date) o).getTime();
        } else if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue() ? (byte) 1 : (byte) 0;
        } else {
            return toPrimitiveByte(o.toString());
        }
    }

    private static byte toPrimitiveByte(String s) {
        if (DfStringUtil.isEmpty(s)) {
            return 0;
        }
        return Byte.parseByte(normalize(s));
    }

    // -----------------------------------------------------
    //                                                  Date
    //                                                  ----
    public static Date toDate(Object o) {
        return toDate(o, null);
    }

    public static Date toDate(Object o, String pattern) {
        if (o == null) {
            return null;
        } else if (o instanceof String) {
            return toDate((String) o, pattern);
        } else if (o instanceof Date) {
            return (Date) o;
        } else if (o instanceof Calendar) {
            return ((Calendar) o).getTime();
        } else {
            return toDate(o.toString(), pattern);
        }
    }

    protected static Date toDate(String s, String pattern) {
        return toDate(s, pattern, Locale.getDefault());
    }

    protected static Date toDate(String s, String pattern, Locale locale) {
        if (s == null || s.trim().length() == 0) {
            return null;
        }
        SimpleDateFormat sdf = getDateFormat(s, pattern, locale);
        try {
            return sdf.parse(s);
        } catch (ParseException e) {
            String msg = "Failed to parse the string to date: ";
            msg = msg + " string=" + s + " format=" + sdf + " locale=" + locale;
            throw new IllegalStateException(msg, e);
        }
    }

    public static java.sql.Date toSqlDate(Object o) {
        return toSqlDate(o, null);
    }

    public static java.sql.Date toSqlDate(Object o, String pattern) {
        if (o instanceof java.sql.Date) {
            return (java.sql.Date) o;
        }
        java.util.Date date = toDate(o, pattern);
        if (date != null) {
            return new java.sql.Date(date.getTime());
        }
        return null;
    }

    public static String getDateY4Pattern(Locale locale) {
        String pattern = getDatePattern(locale);
        if (pattern.indexOf("yyyy") < 0) {
            pattern = DfStringUtil.replace(pattern, "yy", "yyyy");
        }
        return pattern;
    }

    public static String getDatePattern(Locale locale) {
        SimpleDateFormat df = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale);
        String pattern = df.toPattern();
        int index = pattern.indexOf(' ');
        if (index > 0) {
            pattern = pattern.substring(0, index);
        }
        if (pattern.indexOf("MM") < 0) {
            pattern = DfStringUtil.replace(pattern, "M", "MM");
        }
        if (pattern.indexOf("dd") < 0) {
            pattern = DfStringUtil.replace(pattern, "d", "dd");
        }
        return pattern;
    }

    // -----------------------------------------------------
    //                                             Timestamp
    //                                             ---------
    public static Timestamp toTimestamp(Object o) {
        return toTimestamp(o, null);
    }

    public static Timestamp toTimestamp(Object o, String pattern) {
        if (o instanceof Timestamp) {
            return (Timestamp) o;
        }
        Date date = toDate(o, pattern);
        if (date != null) {
            return new Timestamp(date.getTime());
        }
        return null;
    }

    public static String getPattern(Locale locale) {
        return getDateY4Pattern(locale) + " " + getTimePattern(locale);
    }

    // -----------------------------------------------------
    //                                                  Time
    //                                                  ----
    public static Time toTime(Object o) {
        return toTime(o, null);
    }

    public static Time toTime(Object o, String pattern) {
        if (o == null) {
            return null;
        } else if (o instanceof String) {
            return toTime((String) o, pattern);
        } else if (o instanceof Time) {
            return (Time) o;
        } else if (o instanceof Calendar) {
            return new Time(((Calendar) o).getTime().getTime());
        } else {
            return toTime(o.toString(), pattern);
        }
    }

    public static Time toTime(String s, String pattern) {
        return toTime(s, pattern, Locale.getDefault());
    }

    public static Time toTime(String s, String pattern, Locale locale) {
        if (DfStringUtil.isEmpty(s)) {
            return null;
        }
        SimpleDateFormat sdf = getTimeDateFormat(s, pattern, locale);
        try {
            return new Time(sdf.parse(s).getTime());
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    public static SimpleDateFormat getTimeDateFormat(String s, String pattern, Locale locale) {
        if (pattern != null) {
            return new SimpleDateFormat(pattern);
        }
        return getTimeDateFormat(s, locale);
    }

    public static SimpleDateFormat getTimeDateFormat(String s, Locale locale) {
        String pattern = getTimePattern(locale);
        if (s.length() == pattern.length()) {
            return new SimpleDateFormat(pattern);
        }
        String shortPattern = convertTimeShortPattern(pattern);
        if (s.length() == shortPattern.length()) {
            return new SimpleDateFormat(shortPattern);
        }
        return new SimpleDateFormat(pattern);
    }

    public static String getTimePattern(Locale locale) {
        return "HH:mm:ss";
    }

    public static String convertTimeShortPattern(String pattern) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < pattern.length(); ++i) {
            char c = pattern.charAt(i);
            if (c == 'h' || c == 'H' || c == 'm' || c == 's') {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    // -----------------------------------------------------
    //                                                Number
    //                                                ------
    public static Object toNumber(Class<?> type, Object o) {
        if (type == Integer.class) {
            return toInteger(o);
        } else if (type == BigDecimal.class) {
            return toBigDecimal(o);
        } else if (type == Double.class) {
            return toDouble(o);
        } else if (type == Long.class) {
            return toLong(o);
        } else if (type == Float.class) {
            return toFloat(o);
        } else if (type == Short.class) {
            return toShort(o);
        } else if (type == BigInteger.class) {
            return toBigInteger(o);
        } else if (type == Byte.class) {
            return toByte(o);
        }
        return o;
    }

    // -----------------------------------------------------
    //                                               Wrapper
    //                                               -------
    public static Object toWrapper(Class<?> type, Object o) {
        if (type == int.class) {
            Integer i = toInteger(o);
            if (i != null) {
                return i;
            }
            return new Integer(0);
        } else if (type == double.class) {
            Double d = toDouble(o);
            if (d != null) {
                return d;
            }
            return new Double(0);
        } else if (type == long.class) {
            Long l = toLong(o);
            if (l != null) {
                return l;
            }
            return new Long(0);
        } else if (type == float.class) {
            Float f = toFloat(o);
            if (f != null) {
                return f;
            }
            return new Float(0);
        } else if (type == short.class) {
            Short s = toShort(o);
            if (s != null) {
                return s;
            }
            return new Short((short) 0);
        } else if (type == boolean.class) {
            Boolean b = toBoolean(o);
            if (b != null) {
                return b;
            }
            return Boolean.FALSE;
        } else if (type == byte.class) {
            Byte b = toByte(o);
            if (b != null) {
                return b;
            }
            return new Byte((byte) 0);
        }
        return o;
    }

    public static Object convertPrimitiveWrapper(Class<?> type, Object o) {
        if (type == int.class) {
            Integer i = toInteger(o);
            if (i != null) {
                return i;
            }
            return new Integer(0);
        } else if (type == double.class) {
            Double d = toDouble(o);
            if (d != null) {
                return d;
            }
            return new Double(0);
        } else if (type == long.class) {
            Long l = toLong(o);
            if (l != null) {
                return l;
            }
            return new Long(0);
        } else if (type == float.class) {
            Float f = toFloat(o);
            if (f != null) {
                return f;
            }
            return new Float(0);
        } else if (type == short.class) {
            Short s = toShort(o);
            if (s != null) {
                return s;
            }
            return new Short((short) 0);
        } else if (type == boolean.class) {
            Boolean b = toBoolean(o);
            if (b != null) {
                return b;
            }
            return Boolean.FALSE;
        } else if (type == byte.class) {
            Byte b = toByte(o);
            if (b != null) {
                return b;
            }
            return new Byte((byte) 0);
        }
        return o;
    }

    // -----------------------------------------------------
    //                                              Calendar
    //                                              --------
    public static Calendar toCalendar(Object o) {
        return toCalendar(o, null);
    }

    public static Calendar toCalendar(Object o, String pattern) {
        if (o instanceof Calendar) {
            return (Calendar) o;
        }
        java.util.Date date = toDate(o, pattern);
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal;
        }
        return null;
    }

    public static Calendar localize(Calendar calendar) {
        if (calendar == null) {
            throw new NullPointerException("calendar");
        }
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.setTimeInMillis(calendar.getTimeInMillis());
        return localCalendar;
    }

    // ===================================================================================
    //                                                                           Normalize
    //                                                                           =========
    protected static String normalize(String s) {
        return normalize(s, Locale.getDefault());
    }

    protected static String normalize(String s, Locale locale) {
        if (s == null) {
            return null;
        }
        DecimalFormatSymbols symbols = getDecimalFormatSymbols(locale);
        char decimalSep = symbols.getDecimalSeparator();
        char groupingSep = symbols.getGroupingSeparator();
        StringBuilder sb = new StringBuilder(20);
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == groupingSep) {
                continue;
            } else if (c == decimalSep) {
                c = '.';
            }
            sb.append(c);
        }
        return sb.toString();
    }

    // ===================================================================================
    //                                                                 toString() Handling
    //                                                                 ===================
    public static String toString(Object value) {
        return toString(value, null);
    }

    public static String toString(Object value, String pattern) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return (String) value;
        } else if (value instanceof java.util.Date) {
            return toString((java.util.Date) value, pattern);
        } else if (value instanceof Number) {
            return toString((Number) value, pattern);
        } else if (value instanceof byte[]) {
            return DfBase64Util.encode((byte[]) value);
        } else {
            return value.toString();
        }
    }

    public static String toString(Number value, String pattern) {
        if (value != null) {
            if (pattern != null) {
                return new DecimalFormat(pattern).format(value);
            }
            return value.toString();
        }
        return null;
    }

    public static String toString(java.util.Date value, String pattern) {
        if (value != null) {
            if (pattern != null) {
                return new SimpleDateFormat(pattern).format(value);
            }
            return value.toString();
        }
        return null;
    }

    // ===================================================================================
    //                                                                   toText() Handling
    //                                                                   =================
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

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    // -----------------------------------------------------
    //                                            DateFormat
    //                                            ----------
    protected static SimpleDateFormat getDateFormat(String s, String pattern, Locale locale) {
        if (pattern != null) {
            return new SimpleDateFormat(pattern);
        }
        return getDateFormat(s, locale);
    }

    protected static SimpleDateFormat getDateFormat(String s, Locale locale) {
        String pattern = getDateFormatPattern(locale);
        String shortPattern = removeDateDelimiter(pattern);
        String delimitor = findDateDelimiter(s);
        if (delimitor == null) {
            if (s.length() == shortPattern.length()) {
                return new SimpleDateFormat(shortPattern);
            }
            if (s.length() == shortPattern.length() + 2) {
                return new SimpleDateFormat(replace(shortPattern, "yy", "yyyy"));
            }
        } else {
            String[] array = split(s, delimitor);
            for (int i = 0; i < array.length; ++i) {
                if (array[i].length() == 4) {
                    pattern = replace(pattern, "yy", "yyyy");
                    break;
                }
            }
            return new SimpleDateFormat(pattern);
        }
        return new SimpleDateFormat();
    }

    protected static String getDateFormatPattern(Locale locale) {
        SimpleDateFormat df = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale);
        String pattern = df.toPattern();
        int index = pattern.indexOf(' ');
        if (index > 0) {
            pattern = pattern.substring(0, index);
        }
        if (pattern.indexOf("MM") < 0) {
            pattern = replace(pattern, "M", "MM");
        }
        if (pattern.indexOf("dd") < 0) {
            pattern = replace(pattern, "d", "dd");
        }
        return pattern;
    }

    protected static String removeDateDelimiter(String pattern) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pattern.length(); ++i) {
            char c = pattern.charAt(i);
            if (c == 'y' || c == 'M' || c == 'd') {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    protected static String findDateDelimiter(String value) {
        for (int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);
            if (Character.isDigit(c)) {
                continue;
            }
            return Character.toString(c);
        }
        return null;
    }

    // -----------------------------------------------------
    //                                  DecimalFormatSymbols
    //                                  --------------------
    protected static Map<Locale, DecimalFormatSymbols> symbolsCache = new ConcurrentHashMap<Locale, DecimalFormatSymbols>();

    protected static DecimalFormatSymbols getDecimalFormatSymbols() {
        return getDecimalFormatSymbols(Locale.getDefault());
    }

    protected static DecimalFormatSymbols getDecimalFormatSymbols(Locale locale) {
        DecimalFormatSymbols symbols = (DecimalFormatSymbols) symbolsCache.get(locale);
        if (symbols == null) {
            symbols = new DecimalFormatSymbols(locale);
            symbolsCache.put(locale, symbols);
        }
        return symbols;
    }

    // -----------------------------------------------------
    //                                                String
    //                                                ------
    protected static String replace(String text, String fromText, String toText) {
        if (text == null || fromText == null || toText == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        int pos2 = 0;
        do {
            pos = text.indexOf(fromText, pos2);
            if (pos == 0) {
                sb.append(toText);
                pos2 = fromText.length();
            } else if (pos > 0) {
                sb.append(text.substring(pos2, pos));
                sb.append(toText);
                pos2 = pos + fromText.length();
            } else {
                sb.append(text.substring(pos2));
                return sb.toString();
            }
        } while (true);
    }

    protected static final String[] EMPTY_STRINGS = new String[0];

    protected static String[] split(final String str, final String delimiter) {
        if (str == null || str.trim().length() == 0) {
            return EMPTY_STRINGS;
        }
        final List<String> list = new ArrayList<String>();
        final StringTokenizer st = new StringTokenizer(str, delimiter);
        while (st.hasMoreElements()) {
            list.add(st.nextToken());
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    /**
     * Assert that the object is not null.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     * @exception IllegalArgumentException
     */
    protected static void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=" + variableName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }
}
