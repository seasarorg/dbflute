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
public final class DfTypeUtil {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String NULL = "null";

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
                return new Integer(createSimpleDateFormat(pattern).format(o));
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
                return Integer.parseInt(createSimpleDateFormat(pattern).format(o));
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
                return new Long(createSimpleDateFormat(pattern).format(o));
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
                return Long.parseLong(createSimpleDateFormat(pattern).format(o));
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
                return new Double(createSimpleDateFormat(pattern).format(o));
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
                return Double.parseDouble(createSimpleDateFormat(pattern).format(o));
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
                return new Float(createSimpleDateFormat(pattern).format(o));
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
                return Float.parseFloat(createSimpleDateFormat(pattern).format(o));
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
                return Short.valueOf(createSimpleDateFormat(pattern).format(o));
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
                return Short.parseShort(createSimpleDateFormat(pattern).format(o));
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
                return new Byte(createSimpleDateFormat(pattern).format(o));
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
                return Byte.parseByte(createSimpleDateFormat(pattern).format(o));
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
                return new BigDecimal(createSimpleDateFormat(pattern).format(o));
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
     * This method uses default date pattern based on 'yyyy-MM-dd HH:mm:ss.SSS'
     * with flexible-parsing if the object is string type.
     * @param o The parsed object. (Nullable)
     * @return The instance of date. (Nullable)
     * @throws ToDateParseException When it failed to parse the string to date.
     * @throws ToDateNumberFormatException When it failed to format the elements as number.
     * @throws ToDateOutOfCalendarException When the date was out of calendar. (if BC, not thrown)
     */
    public static Date toDate(Object o) {
        return toDate(o, null);
    }

    /**
     * Convert the object to the instance that is date. <br />
     * Even if it's the sub class type, it returns a new instance. <br />
     * This method uses specified date pattern when the pattern is not null
     * if the object is string type. If it's null, it uses default date pattern
     * with flexible-parsing based on 'yyyy-MM-dd HH:mm:ss.SSS'.
     * @param o The parsed object. (Nullable)
     * @param pattern The pattern format to parse. (Nullable)
     * @return The instance of date. (Nullable)
     * @throws ToDateParseException When it failed to parse the string to date.
     * @throws ToDateNumberFormatException When it failed to format the elements as number.
     * @throws ToDateOutOfCalendarException When the date was out of calendar. (if BC, not thrown)
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

    protected static Date toDateFromString(String value, String pattern) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }
        boolean strict;
        if (pattern == null || pattern.trim().length() == 0) { // flexibly
            final boolean includeMilli = true; // after all, includes when date too
            value = filterDateStringValueFlexibly(value, includeMilli);
            strict = !value.startsWith("-"); // not BC
            pattern = "yyyy-MM-dd HH:mm:ss.SSS";
        } else {
            strict = true;
        }
        final DateFormat df = createSimpleDateFormat(pattern, strict);
        try {
            return df.parse(value);
        } catch (ParseException e) {
            try {
                df.setLenient(true);
                df.parse(value); // no exception means illegal date
                String msg = "The date expression is out of calendar:";
                msg = msg + " string=" + value + " pattern=" + pattern;
                throw new ToDateOutOfCalendarException(msg, e);
            } catch (ParseException ignored) {
                String msg = "Failed to parse the string to date:";
                msg = msg + " string=" + value + " pattern=" + pattern;
                throw new ToDateParseException(msg, e);
            }
        }
    }

    protected static String filterDateStringValueFlexibly(final String pureValue, boolean includeMilli) {
        final String bcPrefix = "-";
        final String adLatinPrefix = "AD";
        final String adLatinDotPrefix = "A.D.";
        final String bcLatinPrefix = "BC";
        final String bcLatinDotPrefix = "B.C.";
        final String dateDlm = "-";
        final String dateTimeDlm = " ";
        final String timeDlm = ":";
        final String timeMilliDlm = ".";
        String value = pureValue;
        value = value.trim();

        // handling AD/BC prefix
        final boolean bc;
        {
            if (value.startsWith(bcLatinPrefix)) {
                value = value.substring(bcLatinPrefix.length());
                bc = true;
            } else if (value.startsWith(bcLatinDotPrefix)) {
                value = value.substring(bcLatinDotPrefix.length());
                bc = true;
            } else if (value.startsWith(adLatinPrefix)) {
                value = value.substring(adLatinPrefix.length());
                bc = false;
            } else if (value.startsWith(adLatinDotPrefix)) {
                value = value.substring(adLatinDotPrefix.length());
                bc = false;
            } else if (value.startsWith(bcPrefix)) {
                value = value.substring(bcPrefix.length());
                bc = true;
            } else {
                bc = false;
            }
        }

        // handling slash delimiter for yyyyMMdd
        value = value.replaceAll("/", "-");

        // handling '20090119' and '8631230' and so on
        if (value.length() <= 8 && !value.contains(dateDlm)) {
            if (value.length() >= 5) {
                value = resolveDateElementZeroPrefix(value, 8 - value.length());
                final String yyyy = value.substring(0, 4);
                final String mm = value.substring(4, 6);
                final String dd = value.substring(6, 8);
                value = yyyy + dateDlm + mm + dateDlm + dd;
            } else {
                return pureValue; // couldn't filter for example '1234'
            }
        }

        // check whether it can filter
        if (!value.contains("-") || (value.indexOf("-") == value.lastIndexOf("-"))) {
            return pureValue; // couldn't filter for example '123456789' and '1234-123'
        }

        // handling zero prefix
        final int yearEndIndex = value.indexOf(dateDlm);
        String yyyy = value.substring(0, yearEndIndex);
        yyyy = resolveDateElementZeroPrefix(yyyy, 4 - yyyy.length());
        if (bc) {
            final Integer yyyyInt = formatDateElementAsNumber(yyyy, "yyyy", pureValue);
            yyyy = String.valueOf(yyyyInt - 1); // because DateFormat treats '-2007' as 'BC2008'
            yyyy = resolveDateElementZeroPrefix(yyyy, 4 - yyyy.length());
        } else {
            formatDateElementAsNumber(yyyy, "yyyy", pureValue); // check only
        }

        final String startsMon = value.substring(yearEndIndex + dateDlm.length());
        final int monthEndIndex = startsMon.indexOf(dateDlm);
        String mm = startsMon.substring(0, monthEndIndex);
        mm = resolveDateElementZeroPrefix(mm, 2 - mm.length());
        formatDateElementAsNumber(mm, "MM", pureValue); // check only

        final String startsDay = startsMon.substring(monthEndIndex + dateDlm.length());
        final int dayEndIndex = startsDay.indexOf(dateTimeDlm);
        String dd = dayEndIndex >= 0 ? startsDay.substring(0, dayEndIndex) : startsDay;
        dd = resolveDateElementZeroPrefix(dd, 2 - dd.length());
        formatDateElementAsNumber(dd, "dd", pureValue); // check only
        final String yyyy_MM_dd = yyyy + dateDlm + mm + dateDlm + dd;

        if (dayEndIndex >= 0) { // has time parts
            final String time = startsDay.substring(dayEndIndex + dateTimeDlm.length());

            // check whether it can filter
            if (!time.contains(timeDlm) || (time.indexOf(timeDlm) == time.lastIndexOf(timeDlm))) {
                return pureValue; // couldn't filter for example '2009-12-12 123451' and '2009-12-12 123:451'
            }

            value = yyyy_MM_dd + dateTimeDlm + handleTimeZeroPrefix(time, pureValue, includeMilli);
        } else {
            value = yyyy_MM_dd + dateTimeDlm + "00:00:00";
            if (includeMilli) {
                value = value + timeMilliDlm + "000";
            }
        }
        return (bc ? bcPrefix : "") + value;
    }

    protected static String handleTimeZeroPrefix(String time, String pureValue, boolean includeMilli) {
        final String timeDlm = ":";
        final String timeMilliDlm = ".";

        final int hourEndIndex = time.indexOf(timeDlm);
        String hour = time.substring(0, hourEndIndex);
        hour = resolveDateElementZeroPrefix(hour, 2 - hour.length());
        formatDateElementAsNumber(hour, "HH", pureValue); // check only

        final String startsMin = time.substring(hourEndIndex + timeDlm.length());
        final int minEndIndex = startsMin.indexOf(timeDlm);
        String min = startsMin.substring(0, minEndIndex);
        min = resolveDateElementZeroPrefix(min, 2 - min.length());
        formatDateElementAsNumber(min, "mm", pureValue); // check only

        final String startsSec = startsMin.substring(minEndIndex + timeDlm.length());
        final int secEndIndex = startsSec.indexOf(timeMilliDlm);
        String sec = secEndIndex >= 0 ? startsSec.substring(0, secEndIndex) : startsSec;
        sec = resolveDateElementZeroPrefix(sec, 2 - sec.length());
        formatDateElementAsNumber(sec, "ss", pureValue); // check only

        String value = hour + timeDlm + min + timeDlm + sec;
        if (includeMilli) {
            if (secEndIndex >= 0) {
                final String milli = startsSec.substring(secEndIndex + timeMilliDlm.length());
                resolveDateElementZeroPrefix(milli, 3 - milli.length());
                formatDateElementAsNumber(milli, "SSS", pureValue); // check only
                value = value + timeMilliDlm + milli; // append millisecond
            } else {
                value = value + timeMilliDlm + "000";
            }
        }
        return value;
    }

    protected static Integer formatDateElementAsNumber(String value, String title, String pureValue) {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            String msg = "Failed to format " + title + " as number:";
            msg = msg + " " + title + "=" + value + " value=" + pureValue;
            throw new ToDateNumberFormatException(msg, e);
        }
    }

    protected static String resolveDateElementZeroPrefix(String value, int count) {
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

    public static void clearMilliseconds(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        date.setTime(cal.getTimeInMillis());
    }

    public static class ToDateParseException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ToDateParseException(String msg, Exception e) {
            super(msg, e);
        }
    }

    public static class ToDateNumberFormatException extends ToDateParseException {
        private static final long serialVersionUID = 1L;

        public ToDateNumberFormatException(String msg, Exception e) {
            super(msg, e);
        }
    }

    public static class ToDateOutOfCalendarException extends ToDateParseException {
        private static final long serialVersionUID = 1L;

        public ToDateOutOfCalendarException(String msg, Exception e) {
            super(msg, e);
        }
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
     * @throws ToTimestampNumberFormatException When it failed to format the elements as number.
     * @throws ToTimestampOutOfCalendarException When the timestamp was out of calendar. (if BC, not thrown)
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
     * @throws ToTimestampNumberFormatException When it failed to format the elements as number.
     * @throws ToTimestampOutOfCalendarException When the timestamp was out of calendar. (if BC, not thrown)
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

    protected static Timestamp toTimestampFromString(String value, String pattern) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }
        boolean strict;
        if (pattern == null || pattern.trim().length() == 0) { // flexibly
            value = filterTimestampStringValueFlexibly(value);
            strict = !value.startsWith("-"); // not BC
            pattern = "yyyy-MM-dd HH:mm:ss.SSS";
        } else {
            strict = true;
        }
        DateFormat df = createSimpleDateFormat(pattern, strict);
        try {
            return new Timestamp(df.parse(value).getTime());
        } catch (ParseException e) {
            try {
                df.setLenient(true);
                df.parse(value); // no exception means illegal date
                String msg = "The timestamp expression is out of calendar:";
                msg = msg + " string=" + value + " pattern=" + pattern;
                throw new ToTimestampOutOfCalendarException(msg, e);
            } catch (ParseException ignored) {
                String msg = "Failed to parse the string to timestamp:";
                msg = msg + " string=" + value + " pattern=" + pattern;
                throw new ToTimestampParseException(msg, e);
            }
        }
    }

    protected static String filterTimestampStringValueFlexibly(final String pureValue) {
        String value = pureValue;
        try {
            final boolean includeMilli = true;
            value = filterDateStringValueFlexibly(value, includeMilli); // based on date way
        } catch (ToDateNumberFormatException e) {
            String msg = "Failed to format the timestamp as number:";
            msg = msg + " value=" + pureValue;
            throw new ToTimestampNumberFormatException(msg, e);
        }
        return value;
    }

    public static class ToTimestampParseException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ToTimestampParseException(String msg, Exception e) {
            super(msg, e);
        }
    }

    public static class ToTimestampOutOfCalendarException extends ToTimestampParseException {
        private static final long serialVersionUID = 1L;

        public ToTimestampOutOfCalendarException(String msg, Exception e) {
            super(msg, e);
        }
    }

    public static class ToTimestampNumberFormatException extends ToTimestampParseException {
        private static final long serialVersionUID = 1L;

        public ToTimestampNumberFormatException(String msg, Exception e) {
            super(msg, e);
        }
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
     * @throws ToTimeNumberFormatException When it failed to format the elements as number.
     * @throws ToTimeOutOfCalendarException When the time is out of calendar.
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
     * @throws ToTimeNumberFormatException When it failed to format the elements as number.
     * @throws ToTimeOutOfCalendarException When the time is out of calendar.
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

    protected static Time toTimeFromString(String value, String pattern) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }
        if (pattern == null || pattern.trim().length() == 0) { // flexibly
            value = filterTimeStringValueFlexibly(value);
            pattern = "HH:mm:ss";
        }
        final DateFormat df = createSimpleDateFormat(pattern, true);
        try {
            return new Time(df.parse(value).getTime());
        } catch (ParseException e) {
            try {
                df.setLenient(true);
                df.parse(value); // no exception means illegal date
                String msg = "The time expression is out of calendar:";
                msg = msg + " string=" + value + " pattern=" + pattern;
                throw new ToTimeOutOfCalendarException(msg, e);
            } catch (ParseException ignored) {
                String msg = "Failed to parse the string to time:";
                msg = msg + " string=" + value + " pattern=" + pattern;
                throw new ToTimeParseException(msg, e);
            }
        }
    }

    protected static String filterTimeStringValueFlexibly(String pureValue) {
        String value = pureValue;
        value = value.trim();
        final int dateEndIndex = value.indexOf(" ");
        if (dateEndIndex >= 0) {
            // '2008-12-12 12:34:56' to '12:34:56'
            final String time = value.substring(dateEndIndex + " ".length());
            final boolean includeMilli = false;
            try {
                value = handleTimeZeroPrefix(time, pureValue, includeMilli);
            } catch (ToDateNumberFormatException e) {
                String msg = "Failed to format the time as number:";
                msg = msg + " value=" + pureValue;
                throw new ToTimeNumberFormatException(msg, e);
            }
        }
        return value;
    }

    public static class ToTimeParseException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ToTimeParseException(String msg, Exception e) {
            super(msg, e);
        }
    }

    public static class ToTimeNumberFormatException extends ToTimeParseException {
        private static final long serialVersionUID = 1L;

        public ToTimeNumberFormatException(String msg, Exception e) {
            super(msg, e);
        }
    }

    public static class ToTimeOutOfCalendarException extends ToTimeParseException {
        private static final long serialVersionUID = 1L;

        public ToTimeOutOfCalendarException(String msg, Exception e) {
            super(msg, e);
        }
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
     * @throws ToSqlDateParseException When it failed to parse the string to SQL date.
     * @throws ToSqlDateNumberFormatException When it failed to format the elements as number.
     * @throws ToSqlDateOutOfCalendarException When the time is out of calendar.
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
     * @throws ToSqlDateParseException When it failed to parse the string to SQL date.
     * @throws ToSqlDateNumberFormatException When it failed to format the elements as number.
     * @throws ToSqlDateOutOfCalendarException When the time is out of calendar.
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
        } catch (ToDateNumberFormatException e) {
            String msg = "Failed to format the time as number:";
            msg = msg + " obj=" + o + " pattern=" + pattern;
            throw new ToSqlDateNumberFormatException(msg, e);
        } catch (ToDateOutOfCalendarException e) {
            String msg = "The SQL-date expression is out of calendar:";
            msg = msg + " obj=" + o + " pattern=" + pattern;
            throw new ToSqlDateOutOfCalendarException(msg, e);
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

    public static class ToSqlDateNumberFormatException extends ToSqlDateParseException {
        private static final long serialVersionUID = 1L;

        public ToSqlDateNumberFormatException(String msg, Exception e) {
            super(msg, e);
        }
    }

    public static class ToSqlDateOutOfCalendarException extends ToSqlDateParseException {
        private static final long serialVersionUID = 1L;

        public ToSqlDateOutOfCalendarException(String msg, Exception e) {
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
            throw new IllegalArgumentException("The argument 'calendar' should not be null.");
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
        DateFormat format = createSimpleDateFormat(pattern);
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
        } else if (value instanceof Date) {
            return toString((Date) value, pattern);
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

    public static String toString(Date value, String pattern) {
        if (value != null) {
            if (pattern != null) {
                return createSimpleDateFormat(pattern).format(value);
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
        StringBuilder sb = new StringBuilder();
        addDate(sb, calendar);
        return quote(sb.toString());
    }

    public static String toText(Time value) {
        if (value == null) {
            return NULL;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);
        StringBuilder sb = new StringBuilder();
        addTime(sb, calendar);
        addTimeDecimalPart(sb, calendar.get(Calendar.MILLISECOND));
        return quote(sb.toString());
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

    // yyyy-MM-dd
    protected static void addDate(StringBuilder sb, Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        sb.append(year);
        sb.append('-');
        int month = calendar.get(Calendar.MONTH) + 1;
        if (month < 10) {
            sb.append('0');
        }
        sb.append(month);
        sb.append('-');
        int date = calendar.get(Calendar.DATE);
        if (date < 10) {
            sb.append('0');
        }
        sb.append(date);
    }

    // hh:mm:ss
    protected static void addTime(StringBuilder sb, Calendar calendar) {
        if (sb.length() > 0) {
            sb.append(' ');
        }
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour < 10) {
            sb.append('0');
        }
        sb.append(hour);
        sb.append(':');
        int minute = calendar.get(Calendar.MINUTE);
        if (minute < 10) {
            sb.append('0');
        }
        sb.append(minute);
        sb.append(':');
        int second = calendar.get(Calendar.SECOND);
        if (second < 10) {
            sb.append('0');
        }
        sb.append(second);
    }

    // .000
    protected static void addTimeDecimalPart(StringBuilder sb, int decimalPart) {
        if (decimalPart == 0) {
            return;
        }
        if (sb.length() > 0) {
            sb.append('.');
        }
        sb.append(decimalPart);
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
    protected static SimpleDateFormat createSimpleDateFormat(String pattern) {
        return createSimpleDateFormat(pattern, false);
    }

    protected static SimpleDateFormat createSimpleDateFormat(String pattern, boolean strict) {
        if (pattern == null) {
            String msg = "The argument 'pattern' should not be null!";
            throw new IllegalArgumentException(msg);
        }
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setLenient(!strict);
        return simpleDateFormat;
    }

    // -----------------------------------------------------
    //                                  DecimalFormatSymbols
    //                                  --------------------
    protected static Map<Locale, DecimalFormatSymbols> symbolsCache = new ConcurrentHashMap<Locale, DecimalFormatSymbols>();

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
