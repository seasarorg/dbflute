package org.seasar.dbflute.util.basic;

/**
 * @author jflute
 * @since 0.8.8 (2008/12/02 Tuesday)
 */
public abstract class DfJavaBeansUtil {

    // ===================================================================================
    //                                                                               Basic
    //                                                                               =====
    public static String decapitalizePropertyName(String javaName) {
        if (javaName == null || javaName.length() == 0) {
            return javaName;
        }
        if (javaName.length() > 1 && Character.isUpperCase(javaName.charAt(1))
                && Character.isUpperCase(javaName.charAt(0))) {
            return javaName;
        }
        char chars[] = javaName.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }
}