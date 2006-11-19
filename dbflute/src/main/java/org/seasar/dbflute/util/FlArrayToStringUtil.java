package org.seasar.dbflute.util;

/**
 * Array to String Utility.
 * 
 * @author kubo
 * @version $Revision$ $Date$ 
 */
public class FlArrayToStringUtil {

    /**
     * 指定されたClass-Class配列をPackageの無いClass名で
     * Comma区切りに編集します．<p>
     * 
     * @param clazzArray Class配列
     * @return Packageの無いClass名のComma区切り文字列
     */
    final public String getShortClassArrayStr(Class[] clazzArray) {
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

    /**
     * 指定されたObject配列の値をComma区切りに編集します．<p>
     * 
     * @param objArray Object配列
     * @return 値のComma区切り区切り文字列
     */
    final public String getObjArrayStr(Object[] objArray) {
        if (objArray == null) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < objArray.length; i++) {
            if (i == 0) {
                sb.append(objArray[i]);
            } else {
                sb.append(", ").append(objArray[i]);
            }
        }
        return sb.toString();
    }

    /**
     * システム改行を取得する。
     * 
     * @return System line separator.
     */
    final protected static String newLine() {
        return System.getProperty("line.separator");
    }

    /**
     * 指定されたClassのPackageの無いClass名を取得する。
     * 
     * @param clazz Class.
     * @return Class name without package.
     */
    final protected static String getShortClassName(Class clazz) {
        if (clazz == null) {
            return "";
        }
        final String tmp = clazz.getName();
        return tmp.substring(tmp.lastIndexOf(".") + 1, tmp.length());
    }
}