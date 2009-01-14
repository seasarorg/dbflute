package org.dbflute.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormatSymbols;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Date;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {Refers to S2Container's utility and Extends it}
 * @author jflute
 */
public class DfTypeUtil {

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
            if (i != null) { return i; }
            return new Integer(0);
        } else if (type == double.class) {
            Double d = toDouble(o);
            if (d != null) { return d; }
            return new Double(0);
        } else if (type == long.class) {
            Long l = toLong(o);
            if (l != null) { return l; }
            return new Long(0);
        } else if (type == float.class) {
            Float f = toFloat(o);
            if (f != null) { return f; }
            return new Float(0);
        } else if (type == short.class) {
            Short s = toShort(o);
            if (s != null) { return s; }
            return new Short((short) 0);
        } else if (type == boolean.class) {
            Boolean b = toBoolean(o);
            if (b != null) { return b; }
            return Boolean.FALSE;
        } else if (type == byte.class) {
            Byte b = toByte(o);
            if (b != null) { return b; }
            return new Byte((byte) 0);
        }
        return o;
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
        if(text == null || fromText == null || toText == null) {
            return null;
		}
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        int pos2 = 0;
        do {
            pos = text.indexOf(fromText, pos2);
            if(pos == 0) {
                sb.append(toText);
                pos2 = fromText.length();
            } else
            if(pos > 0) {
                sb.append(text.substring(pos2, pos));
                sb.append(toText);
                pos2 = pos + fromText.length();
            } else {
                sb.append(text.substring(pos2));
                return sb.toString();
            }
        } while(true);
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
