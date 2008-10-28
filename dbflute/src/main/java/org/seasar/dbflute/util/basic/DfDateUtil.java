package org.seasar.dbflute.util.basic;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.seasar.dbflute.util.DfStringUtil;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public abstract class DfDateUtil {

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

    public static Date toDate(String s, String pattern) {
        return toDate(s, pattern, Locale.getDefault());
    }

    public static Date toDate(String s, String pattern, Locale locale) {
        if (DfStringUtil.isEmpty(s)) {
            return null;
        }
        SimpleDateFormat sdf = getDateFormat(s, pattern, locale);
        try {
            return sdf.parse(s);
        } catch (ParseException e) {
            String msg = "Failed to parse the string: s=" + s + " pattern=" + pattern;
            throw new IllegalStateException(msg, e);
        }
    }

    public static SimpleDateFormat getDateFormat(String s, String pattern, Locale locale) {
        if (pattern != null) {
            return new SimpleDateFormat(pattern);
        }
        return getDateFormat(s, locale);
    }

    public static SimpleDateFormat getDateFormat(String s, Locale locale) {
        String pattern = getPattern(locale);
        String shortPattern = removeDelimiter(pattern);
        String delimitor = findDelimiter(s);
        if (delimitor == null) {
            if (s.length() == shortPattern.length()) {
                return new SimpleDateFormat(shortPattern);
            }
            if (s.length() == shortPattern.length() + 2) {
                return new SimpleDateFormat(DfStringUtil.replace(shortPattern, "yy", "yyyy"));
            }
        } else {
            String[] array = DfStringUtil.split(s, delimitor);
            for (int i = 0; i < array.length; ++i) {
                if (array[i].length() == 4) {
                    pattern = DfStringUtil.replace(pattern, "yy", "yyyy");
                    break;
                }
            }
            return new SimpleDateFormat(pattern);
        }
        return new SimpleDateFormat();
    }

    public static SimpleDateFormat getDateFormat(Locale locale) {
        return new SimpleDateFormat(getPattern(locale));
    }

    public static SimpleDateFormat getY4DateFormat(Locale locale) {
        return new SimpleDateFormat(getY4Pattern(locale));
    }

    public static String getY4Pattern(Locale locale) {
        String pattern = getPattern(locale);
        if (pattern.indexOf("yyyy") < 0) {
            pattern = DfStringUtil.replace(pattern, "yy", "yyyy");
        }
        return pattern;
    }

    public static String getPattern(Locale locale) {
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

    public static String findDelimiter(String value) {
        for (int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);
            if (Character.isDigit(c)) {
                continue;
            }
            return Character.toString(c);
        }
        return null;
    }

    public static String findDelimiterFromPattern(String pattern) {
        String ret = null;
        for (int i = 0; i < pattern.length(); ++i) {
            char c = pattern.charAt(i);
            if (c != 'y' && c != 'M' && c != 'd') {
                ret = String.valueOf(c);
                break;
            }
        }
        return ret;
    }

    public static String removeDelimiter(String pattern) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < pattern.length(); ++i) {
            char c = pattern.charAt(i);
            if (c == 'y' || c == 'M' || c == 'd') {
                buf.append(c);
            }
        }
        return buf.toString();
    }
}