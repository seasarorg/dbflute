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
package org.seasar.dbflute.util;

import java.io.UnsupportedEncodingException;
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
 * {Refers to Seasar and Extends its class}
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
            return (Boolean) o;
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
            } else if (s.equalsIgnoreCase("1")) {
                return Boolean.TRUE;
            } else if (s.equalsIgnoreCase("0")) {
                return Boolean.FALSE;
            } else if (s.equalsIgnoreCase("t")) {
                return Boolean.TRUE;
            } else if (s.equalsIgnoreCase("f")) {
                return Boolean.FALSE;
            } else {
                String msg = "Failed to parse the boolean string:";
                msg = msg + " value=" + s;
                throw new ToBooleanParseException(msg);
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

    public static class ToBooleanParseException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ToBooleanParseException(String msg) {
            super(msg);
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
            return Integer.valueOf(((Number) o).intValue());
        } else if (o instanceof String) {
            return toInteger((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return new Integer(getDateFormat(pattern).format(o));
            }
            return Integer.valueOf((int) ((java.util.Date) o).getTime());
        } else if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue() ? Integer.valueOf(1) : Integer.valueOf(0);
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
                return Integer.parseInt(getDateFormat(pattern).format(o));
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
            return Long.valueOf(((Number) o).longValue());
        } else if (o instanceof String) {
            return toLong((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return new Long(getDateFormat(pattern).format(o));
            }
            return Long.valueOf(((java.util.Date) o).getTime());
        } else if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue() ? Long.valueOf(1) : Long.valueOf(0);
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
                return Long.parseLong(getDateFormat(pattern).format(o));
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
                return new Double(getDateFormat(pattern).format(o));
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
                return Double.parseDouble(getDateFormat(pattern).format(o));
            }
            return ((java.util.Date) o).getTime();
        } else {
            return toPrimitiveDouble(o.toString());
        }
    }

    private static double toPrimitiveDouble(String s) {
        if (DfStringUtil.isNullOrEmpty(s)) {
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
                return new Float(getDateFormat(pattern).format(o));
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
                return Float.parseFloat(getDateFormat(pattern).format(o));
            }
            return ((java.util.Date) o).getTime();
        } else {
            return toPrimitiveFloat(o.toString());
        }
    }

    private static float toPrimitiveFloat(String s) {
        if (DfStringUtil.isNullOrEmpty(s)) {
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
            return Short.valueOf(((Number) o).shortValue());
        } else if (o instanceof String) {
            return toShort((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return Short.valueOf(getDateFormat(pattern).format(o));
            }
            return Short.valueOf((short) ((java.util.Date) o).getTime());
        } else if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue() ? Short.valueOf((short) 1) : Short.valueOf((short) 0);
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
                return Short.parseShort(getDateFormat(pattern).format(o));
            }
            return (short) ((java.util.Date) o).getTime();
        } else if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue() ? (short) 1 : (short) 0;
        } else {
            return toPrimitiveShort(o.toString());
        }
    }

    private static short toPrimitiveShort(String s) {
        if (DfStringUtil.isNullOrEmpty(s)) {
            return 0;
        }
        return Short.parseShort(normalize(s));
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
            return Byte.valueOf(((Number) o).byteValue());
        } else if (o instanceof String) {
            return toByte((String) o);
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return new Byte(getDateFormat(pattern).format(o));
            }
            return Byte.valueOf((byte) ((java.util.Date) o).getTime());
        } else if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue() ? Byte.valueOf((byte) 1) : Byte.valueOf((byte) 0);
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
                return Byte.parseByte(getDateFormat(pattern).format(o));
            }
            return (byte) ((java.util.Date) o).getTime();
        } else if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue() ? (byte) 1 : (byte) 0;
        } else {
            return toPrimitiveByte(o.toString());
        }
    }

    private static byte toPrimitiveByte(String s) {
        if (DfStringUtil.isNullOrEmpty(s)) {
            return 0;
        }
        return Byte.parseByte(normalize(s));
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
            final BigDecimal paramBigDecimal = (BigDecimal) o;
            if (BigDecimal.class.equals(paramBigDecimal.getClass())) { // pure big-decimal
                return paramBigDecimal;
            } else { // sub class
                // because the big-decimal type is not final class.
                return new BigDecimal(paramBigDecimal.toPlainString());
            }
        } else if (o instanceof java.util.Date) {
            if (pattern != null) {
                return new BigDecimal(getDateFormat(pattern).format(o));
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
    //                                            BigInteger
    //                                            ----------
    public static BigInteger toBigInteger(Object o) {
        return toBigInteger(o, null);
    }

    public static BigInteger toBigInteger(Object o, String pattern) {
        if (o == null) {
            return null;
        } else if (o instanceof BigInteger) {
            final BigInteger paramBigInteger = (BigInteger) o;
            if (BigInteger.class.equals(paramBigInteger.getClass())) { // pure big-integer
                return paramBigInteger;
            } else { // sub class
                // because the big-integer type is not final class.
                return BigInteger.valueOf(paramBigInteger.longValue());
            }
        } else {
            Long l = toLong(o, pattern);
            if (l == null) {
                return null;
            }
            return BigInteger.valueOf(l.longValue());
        }
    }

    // -----------------------------------------------------
    //                                                  Date
    //                                                  ----
    /**
     * Convert the object to the instance that is date. <br />
     * Even if it's the sub class type, it returns a new instance. <br />
     * This method uses default date pattern based on 'yyyy-MM-dd HH:mm:ss'
     * with flexible-parsing if the object is string type.
     * @param o The parsed object. (Nullable)
     * @return The instance of date. (Nullable)
     * @throws ToDateParseException When it failed to parse the string to date.
     */
    public static Date toDate(Object o) {
        return toDate(o, null);
    }

    /**
     * Convert the object to the instance that is date. <br />
     * Even if it's the sub class type, it returns a new instance. <br />
     * This method uses specified date pattern when the pattern is not null
     * if the object is string type. If it's null, it uses default date pattern
     * with flexible-parsing based on 'yyyy-MM-dd HH:mm:ss'.
     * @param o The parsed object. (Nullable)
     * @param pattern The pattern format to parse. (Nullable)
     * @return The instance of date. (Nullable)
     * @throws ToDateParseException When it failed to parse the string to date.
     */
    public static Date toDate(Object o, String pattern) {
        if (o == null) {
            return null;
        } else if (o instanceof String) {
            return toDateFromString((String) o, pattern);
        } else if (o instanceof Date) {
            final Date paramDate = (Date) o;
            if (Date.class.equals(paramDate.getClass())) { // pure date
                return paramDate;
            } else { // sub class
                // because the Date is not final class.
                final Date date = new Date();
                date.setTime(paramDate.getTime());
                return date;
            }
        } else if (o instanceof Calendar) {
            return ((Calendar) o).getTime();
        } else {
            return toDateFromString(o.toString(), pattern);
        }
    }

    protected static Date toDateFromString(String s, String pattern) {
        if (s == null || s.trim().length() == 0) {
            return null;
        }
        final DateFormat df;
        if (pattern == null || pattern.trim().length() == 0) { // flexibly
            df = getDateFormat(s, "yyyy-MM-dd HH:mm:ss");
            s = filterDateStringValueFlexibly(s);
        } else {
            df = getDateFormat(s, pattern);
        }
        try {
            return df.parse(s);
        } catch (ParseException e) {
            String msg = "Failed to parse the string to date:";
            msg = msg + " string=" + s + " pattern=" + pattern;
            throw new ToDateParseException(msg, e);
        }
    }

    public static class ToDateParseException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ToDateParseException(String msg, ParseException e) {
            super(msg, e);
        }
    }

    protected static String filterDateStringValueFlexibly(String value) {
        // basic filter
        value = value.trim();
        value = value.replaceAll("/", "-");

        // handling '20090119'
        if (value.length() <= 8 && !value.contains("-")) {
            if (value.length() > 4) {
                value = resolveZeroPrefix(value, 8 - value.length());
            } else {
                return value; // if the value is '2009'
            }
            final String yyyy = value.substring(0, 4);
            final String mm = value.substring(4, 6);
            final String dd = value.substring(6, 8);
            value = yyyy + "-" + mm + "-" + dd;
        }

        // check whether it can filter
        if (!value.contains("-")) { // hyphen is not found
            return value;
        }
        if (value.indexOf("-") == value.lastIndexOf("-")) { // hyphen is only one
            return value;
        }

        // handling zero prefix
        final int yearEndIndex = value.indexOf("-");
        String yyyy = value.substring(0, yearEndIndex);
        yyyy = resolveZeroPrefix(yyyy, 4 - yyyy.length());

        final String startsMm = value.substring(yearEndIndex + "-".length());
        final int monthEndIndex = startsMm.indexOf("-");
        String mm = startsMm.substring(0, monthEndIndex);
        mm = resolveZeroPrefix(mm, 2 - mm.length());

        final String startsDd = startsMm.substring(monthEndIndex + "-".length());
        final int dayEndIndex = startsDd.indexOf(" ");
        String dd = dayEndIndex >= 0 ? startsDd.substring(0, dayEndIndex) : startsDd;
        dd = resolveZeroPrefix(dd, 2 - dd.length());

        String time = null;
        if (dayEndIndex >= 0) {
            time = startsDd.substring(dayEndIndex + " ".length());
        }

        value = yyyy + "-" + mm + "-" + dd + (time != null ? " " + time : "");

        // add HH:mm:dd if not exists
        if (value.indexOf("-") == 4 && value.lastIndexOf("-") == 7) {
            if (value.length() == "2007-07-09".length()) {
                value = value + " 00:00:00"; // for ' HH:mm:ss'
            }
        }
        return value;
    }

    protected static String resolveZeroPrefix(String value, int count) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append("0");
        }
        return sb.toString() + value;
    }

    public static void clearSeconds(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        date.setTime(cal.getTimeInMillis());
    }

    // -----------------------------------------------------
    //                                             Timestamp
    //                                             ---------
    /**
     * Convert the object to the instance that is time-stamp. <br />
     * Even if it's the sub class type, it returns a new instance. <br />
     * This method uses default date pattern based on 'yyyy-MM-dd HH:mm:ss.SSS'
     * with flexible-parsing if the object is string type.
     * @param o The parsed object. (Nullable)
     * @return The instance of time-stamp. (Nullable: If the value is null or empty, it returns null.)
     * @throws ToTimestampParseException When it failed to parse the string to time-stamp.
     */
    public static Timestamp toTimestamp(Object o) {
        return toTimestamp(o, null);
    }

    /**
     * Convert the object to the instance that is time-stamp. <br />
     * Even if it's the sub class type, it returns a new instance. <br />
     * This method uses specified timestamp pattern when the pattern is not null
     * if the object is string type. If it's null, it uses default timestamp pattern
     * with flexible-parsing based on 'yyyy-MM-dd HH:mm:ss.SSS'.
     * @param o The parsed object. (Nullable)
     * @param pattern The pattern format to parse. (Nullable)
     * @return The instance of time-stamp. (Nullable: If the value is null or empty, it returns null.)
     * @throws ToTimestampParseException When it failed to parse the string to time-stamp.
     */
    public static Timestamp toTimestamp(Object o, String pattern) {
        if (o == null) {
            return null;
        } else if (o instanceof Timestamp) {
            final Timestamp paramTimestamp = (Timestamp) o;
            if (Timestamp.class.equals(paramTimestamp.getClass())) { // pure time-stamp
                return paramTimestamp;
            } else { // sub class
                // because the time-stamp type is not final class.
                return new Timestamp(paramTimestamp.getTime());
            }
        } else if (o instanceof Date) {
            return new Timestamp(((Date) o).getTime());
        } else if (o instanceof String) {
            return toTimestampFromString((String) o, pattern);
        } else if (o instanceof Calendar) {
            return new Timestamp(((Calendar) o).getTime().getTime());
        } else {
            return toTimestampFromString(o.toString(), pattern);
        }
    }

    protected static Timestamp toTimestampFromString(String s, String pattern) {
        if (s == null || s.trim().length() == 0) {
            return null;
        }
        final DateFormat df;
        if (pattern == null || pattern.trim().length() == 0) { // flexibly
            df = getDateFormat(s, "yyyy-MM-dd HH:mm:ss.SSS");
            s = filterTimestampStringValueFlexibly(s);
        } else {
            df = getDateFormat(s, pattern);
        }
        try {
            return new Timestamp(df.parse(s).getTime());
        } catch (ParseException e) {
            String msg = "Failed to parse the string to timestamp:";
            msg = msg + " string=" + s + " pattern=" + pattern;
            throw new ToTimestampParseException(msg, e);
        }
    }

    public static class ToTimestampParseException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ToTimestampParseException(String msg, Exception e) {
            super(msg, e);
        }
    }

    protected static String filterTimestampStringValueFlexibly(String value) {
        value = filterDateStringValueFlexibly(value); // based on date way
        final int timeEndIndex = value.indexOf(".");
        if (timeEndIndex < 0) {
            value = value + ".000"; // for '.SSS'
        }
        return value;
    }

    // -----------------------------------------------------
    //                                                  Time
    //                                                  ----
    /**
     * Convert the object to the instance that is time. <br />
     * Even if it's the sub class type, it returns a new instance. <br />
     * This method uses default time pattern based on 'HH:mm:ss'
     * with flexible-parsing if the object is string type.
     * @param o The parsed object. (Nullable)
     * @return The instance of time. (Nullable: If the value is null or empty, it returns null.)
     * @throws ToTimeParseException When it failed to parse the string to time.
     */
    public static Time toTime(Object o) {
        return toTime(o, null);
    }

    /**
     * Convert the object to the instance that is time. <br />
     * Even if it's the sub class type, it returns a new instance. <br />
     * This method uses specified time pattern when the pattern is not null
     * if the object is string type. If it's null, it uses default time pattern
     * with flexible-parsing based on 'HH:mm:ss'.
     * @param o The parsed object. (Nullable)
     * @param pattern The pattern format to parse. (Nullable)
     * @return The instance of time. (Nullable: If the value is null or empty, it returns null.)
     * @throws ToTimeParseException When it failed to parse the string to time.
     */
    public static Time toTime(Object o, String pattern) {
        if (o == null) {
            return null;
        } else if (o instanceof String) {
            return toTimeFromString((String) o, pattern);
        } else if (o instanceof Time) {
            final Time paramTime = (Time) o;
            if (Time.class.equals(paramTime.getClass())) { // pure time
                return paramTime;
            } else { // sub class
                // because the time type is not final class.
                return new Time(paramTime.getTime());
            }
        } else if (o instanceof Date) {
            Date date = (Date) o;
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.YEAR, 1970);
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.DATE, 1);
            return new Time(cal.getTimeInMillis());
        } else if (o instanceof Calendar) {
            Calendar cal = (Calendar) o;
            cal.set(Calendar.YEAR, 1970);
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.DATE, 1);
            return new Time(cal.getTimeInMillis());
        } else {
            return toTimeFromString(o.toString(), pattern);
        }
    }

    protected static Time toTimeFromString(String s, String pattern) {
        if (s == null || s.trim().length() == 0) {
            return null;
        }
        final DateFormat df;
        if (pattern == null || pattern.trim().length() == 0) { // flexibly
            df = getDateFormat(s, "HH:mm:ss");
            s = filterTimeStringValueFlexibly(s);
        } else {
            df = getDateFormat(s, pattern);
        }
        try {
            return new Time(df.parse(s).getTime());
        } catch (ParseException e) {
            String msg = "Failed to parse the string to time:";
            msg = msg + " string=" + s + " pattern=" + pattern;
            throw new ToTimeParseException(msg, e);
        }
    }

    public static class ToTimeParseException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ToTimeParseException(String msg, Exception e) {
            super(msg, e);
        }
    }

    protected static String filterTimeStringValueFlexibly(String value) {
        value = value.trim();
        final int dateEndIndex = value.indexOf(" ");
        if (dateEndIndex >= 0) {
            // '2008-12-12 12:34:56' to '12:34:56' 
            value = value.substring(dateEndIndex + " ".length());
        }
        return value;
    }

    // -----------------------------------------------------
    //                                              SQL Date
    //                                              --------
    /**
     * Convert the object to the instance that is SQL-date. <br />
     * Even if it's the sub class type, it returns a new instance. <br />
     * This method uses default date pattern based on 'yyyy-MM-dd'
     * with flexible-parsing if the object is string type.
     * @param o The parsed object. (Nullable)
     * @return The instance of SQL date. (Nullable)
     * @throws ToDateParseException When it failed to parse the string to SQL date.
     */
    public static java.sql.Date toSqlDate(Object o) {
        return toSqlDate(o, null);
    }

    /**
     * Convert the object to the instance that is SQL-date cleared seconds. <br />
     * Even if it's the sub class type, it returns a new instance. <br />
     * This method uses specified SQL-date pattern when the pattern is not null
     * if the object is string type. If it's null, it uses default SQL-date pattern
     * with flexible-parsing based on 'yyyy-MM-dd'.
     * @param o The parsed object. (Nullable)
     * @param pattern The pattern format to parse. (Nullable)
     * @return The instance of SQL date. (Nullable)
     * @throws ToDateParseException When it failed to parse the string to SQL date.
     */
    public static java.sql.Date toSqlDate(Object o, String pattern) {
        if (o == null) {
            return null;
        }
        if (o instanceof java.sql.Date) {
            final java.sql.Date resultDate;
            final java.sql.Date paramSqlDate = (java.sql.Date) o;
            if (java.sql.Date.class.equals(paramSqlDate.getClass())) { // pure SQL-date
                resultDate = paramSqlDate;
            } else { // sub class
                // because the SQL-date type is not final class.
                resultDate = new java.sql.Date(paramSqlDate.getTime());
            }
            clearSeconds(resultDate);
            return resultDate;
        }
        java.util.Date date;
        try {
            date = toDate(o, pattern);
        } catch (ToDateParseException e) {
            String msg = "Failed to parse the object to SQL-date:";
            msg = msg + " obj=" + o + " pattern=" + pattern;
            throw new ToSqlDateParseException(msg, e);
        }
        if (date != null) {
            clearSeconds(date);
            return new java.sql.Date(date.getTime());
        }
        return null;
    }

    public static class ToSqlDateParseException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ToSqlDateParseException(String msg, Exception e) {
            super(msg, e);
        }
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
        Date date = toDate(o, pattern);
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
            return Integer.valueOf(0);
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
            return Long.valueOf(0);
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
            return Short.valueOf((short) 0);
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
            return Byte.valueOf((byte) 0);
        }
        return o;
    }

    public static Object convertPrimitiveWrapper(Class<?> type, Object o) {
        if (type == int.class) {
            Integer i = toInteger(o);
            if (i != null) {
                return i;
            }
            return Integer.valueOf(0);
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
            return Long.valueOf(0);
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
            return Short.valueOf((short) 0);
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
            return Byte.valueOf((byte) 0);
        }
        return o;
    }

    // -----------------------------------------------------
    //                                                Binary
    //                                                ------
    public static byte[] toBinary(String o, String encoding) {
        if (o == null) {
            return null;
        }
        try {
            return o.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            String msg = "The encoding is invalid: encoding=" + encoding + " o=" + o;
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                              Format
    //                                                                              ======
    /**
     * Format date as specified pattern.
     * @param date The value of date. (Nullable: If the value is null, it returns null.)
     * @param pattern The pattern of format for SimpleDateFormat. (NotNull)
     * @return The formatted string. (Nullable) 
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        DateFormat format = getDateFormat(pattern);
        return format.format(date);
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
                return getDateFormat(pattern).format(value);
            }
            return value.toString();
        }
        return null;
    }

    // ===================================================================================
    //                                                                   toText() Handling
    //                                                                   =================
    // /- - - - - - - - - - - - - - - - - - - - - - - - - - 
    // The text cannot be null.
    // If the value is null, it returns 'null' text.
    // - - - - - - - - - -/

    /**
     * @return The 'null' text as string. (NotNull)
     */
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

    /**
     * @param value The instance of Date. (Nullable: If the value is null, returns 'null'.)
     * @return The text for the argument. (NotNull)
     */
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
        return quote(value.getClass() + "(byteLength=" + Integer.toString(value.length) + ")");
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
    protected static DateFormat getDateFormat(String s, String pattern) {
        if (pattern != null) {
            return getDateFormat(pattern);
        }
        return getDateFormat("yyyy-MM-dd HH:mm:dd");
    }

    protected static DateFormat getTimestampFormat(String s, String pattern) {
        if (pattern != null) {
            return getDateFormat(pattern);
        }
        return getDateFormat("yyyy-MM-dd HH:mm:dd.SSS");
    }

    protected static DateFormat getDateFormat(String pattern) {
        if (pattern == null) {
            String msg = "The argument 'pattern' should not be null!";
            throw new IllegalArgumentException(msg);
        }
        return new SimpleDateFormat(pattern);
    }

    //
    //    protected static DateFormat getDateFormat(String s, Locale locale) {
    //        String pattern = getDateFormatPattern(locale);
    //        final String shortPattern = removeDateDelimiter(pattern);
    //        final String delimitor = findDateDelimiter(s);
    //        if (delimitor == null) {
    //            if (s.length() == shortPattern.length()) {
    //                return getDateFormat(shortPattern);
    //            }
    //            if (s.length() == shortPattern.length() + 2) {
    //                return getDateFormat(replace(shortPattern, "yy", "yyyy"));
    //            }
    //        } else {
    //            String[] array = split(s, delimitor);
    //            for (int i = 0; i < array.length; ++i) {
    //                if (array[i].length() == 4) {
    //                    pattern = replace(pattern, "yy", "yyyy");
    //                    break;
    //                }
    //            }
    //            return getDateFormat(pattern);
    //        }
    //        return new SimpleDateFormat();
    //    }
    //
    //    protected static String getDateFormatPattern(Locale locale) {
    //        final SimpleDateFormat df = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale);
    //        String pattern = df.toPattern();
    //        final int index = pattern.indexOf(' ');
    //        if (index > 0) {
    //            pattern = pattern.substring(0, index);
    //        }
    //        if (pattern.indexOf("MM") < 0) {
    //            pattern = replace(pattern, "M", "MM");
    //        }
    //        if (pattern.indexOf("dd") < 0) {
    //            pattern = replace(pattern, "d", "dd");
    //        }
    //        return pattern;
    //    }

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
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }
}
