package org.apache.torque.helper.stateless;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Method-Utility.
 * <p>
 * Support to invoke method by reflection.
 * 
 * @author kubo
 */
public class FlMethodUtil {

    // ========================================================================================
    //                                                                Getter Setter Name Method
    //                                                                =========================

    /**
     * プロパティ名からGetter-Method名を取得する。
     * (単純にプロパティ名の一文字目を大文字にして先頭に'get'を付与しているだけ。)
     *
     * @param str Property名
     * @return Getter-Method名
     */
    public static String getterName(String str) {
        return "get" + str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * プロパティ名からSetter-Method名を取得する。
     * (単純にプロパティ名の一文字目を大文字にして先頭に'set'を付与しているだけ。)
     *
     * @param str Property名 (NotNull)
     * @return Stter-Method名 (NotNull)
     */
    public static String setterName(String str) {
        return "set" + str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    // ========================================================================================
    //                                                                               Get Method
    //                                                                               ==========
    /**
     * Methodを取得する。
     *
     * @param target 実行Instance
     * @param name Method名
     * @param type 引数のType
     * @return 指定のInstance
     */
    public static Method getMethod(Object target, String name, Class type) {
        try {
            Class[] argTypes = (type != null ? new Class[] { type } : null);
            return target.getClass().getMethod(name, argTypes);
        } catch (NoSuchMethodException e) {
            String msg = "name==[" + name + "] type==[" + type + "]";
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * Methodを取得する。
     *
     * @param target 実行Instance
     * @param name Method名
     * @param type 引数のType
     * @return 指定のInstance
     */
    public static Method getMethod(Object target, String name, Class[] type) {
        try {
            return target.getClass().getMethod(name, type);
        } catch (NoSuchMethodException e) {
            String msg = "name==[" + name + "] type==[" + getShortClassArrayStr(type) + "]";
            throw new RuntimeException(msg, e);
        }
    }

    // ========================================================================================
    //                                                                            Invoke Method
    //                                                                            =============

    /**
     * ObjectのGetter-Methodを実行する。
     *
     * @param name Getter-Method名 (NotNull)
     * @param obj 実行Instance (NotNull)
     * @return 実行したGetter-Methodの戻り値
     */
    public static Object invokeGet(String name, Object obj) {
        if (name == null) {
            throw new NullPointerException("Argument[name] must not be null.");
        }
        if (obj == null) {
            throw new NullPointerException("Argument[obj] must not be null.");
        }
        return invoke(name, (Class[]) null, obj, (Object[]) null);
    }

    /**
     * Methodを実行する．
     *
     * @param name Method名
     * @param type 引数の型
     * @param target 実行Instance
     * @param args 引数Object
     * @return 実行したMethod戻り値
     */
    public static Object invoke(String name, Class type, Object target, Object args) {
        return invoke(getMethod(target, name, type), target, args);
    }

    /**
     * Methodを実行する．
     *
     * @param name Method名
     * @param type 引数の型
     * @param target 実行Instance
     * @param args 引数Object
     * @return 実行したMethod戻り値
     */
    public static Object invoke(String name, Class[] type, Object target, Object[] args) {
        return invoke(getMethod(target, name, type), target, args);
    }

    /**
     * Methodを実行する．
     *
     * @param methoz Method
     * @param target 実行Instance
     * @param arg 引数の値
     * @return 実行したMethod戻り値
     */
    public static Object invoke(Method methoz, Object target, Object arg) {
        try {
            Object[] args = (arg != null ? new Object[] { arg } : null);
            return methoz.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getTargetException());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Methodを実行する．
     *
     * @param methoz Method
     * @param target 実行Instance
     * @param args 引数の値
     * @return 実行したMethod戻り値
     */
    public static Object invoke(Method methoz, Object target, Object[] args) {
        try {
            return methoz.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getTargetException());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // ========================================================================================
    //                                                                   Internal Helper Method
    //                                                                   ======================

    /**
     * 指定されたClassのPackageの無いClass名を取得する。
     * 
     * @param clazz Class.
     * @return Class name without package.
     */
    protected static String getShortClassName(Class clazz) {
        if (clazz == null) {
            return "";
        }
        final String tmp = clazz.getName();
        return tmp.substring(tmp.lastIndexOf(".") + 1, tmp.length());
    }

    /**
     * 指定されたClass-Class配列をPackageの無いClass名で
     * Comma区切りに編集します．<p>
     * 
     * @param clazzArray Class配列
     * @return Packageの無いClass名のComma区切り文字列
     */
    protected static String getShortClassArrayStr(Class[] clazzArray) {
        if (clazzArray == null) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < clazzArray.length; i++) {
            if (i == 0) {
                sb.append(getShortClassName(clazzArray[i]));
            } else {
                sb.append(", ").append(getShortClassName(clazzArray[i]));
            }
        }
        return sb.toString();
    }
}