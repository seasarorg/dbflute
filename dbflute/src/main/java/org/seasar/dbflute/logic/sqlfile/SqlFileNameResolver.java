package org.seasar.dbflute.logic.sqlfile;

import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.util.DfStringUtil;

/**
 * @author jflute
 * @since 0.9.5 (2009/04/10 Friday)
 */
public class SqlFileNameResolver {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String ENTITY_MARK = "$entity";
    public static final String PMB_MARK = "$pmb";

    // ===================================================================================
    //                                                                             Resolve
    //                                                                             =======
    public String resolveObjectNameIfNeeds(String className, String fileName) {
        if (className == null || className.trim().length() == 0) {
            String msg = "The argument[className] should not be null or empty: " + className;
            throw new IllegalArgumentException(msg);
        }
        if (!className.equalsIgnoreCase(ENTITY_MARK) && !className.equalsIgnoreCase(PMB_MARK)) {
            return className;
        }
        if (fileName == null || fileName.trim().length() == 0) {
            String msg = "The argument[fileName] should not be null or empty: " + fileName;
            throw new IllegalArgumentException(msg);
        }
        fileName = DfStringUtil.replace(fileName, "\\", "/");
        if (fileName.contains("/")) {
            fileName = fileName.substring(fileName.lastIndexOf("/") + "/".length());
        }
        if (!fileName.contains("Bhv_")) {
            String msg = "The SQL file should be under BehaviorQueryPath if you use auto-naming:";
            msg = msg + " className=" + className + " fileName=" + fileName;
            throw new IllegalStateException(msg);
        }
        if (!fileName.endsWith(".sql")) {
            String msg = "The SQL file should ends '.sql' if you use auto-naming:";
            msg = msg + " className=" + className + " fileName=" + fileName;
            throw new IllegalStateException(msg);
        }
        final int beginIndex = fileName.indexOf("Bhv_") + "Bhv_".length();
        String tmp = fileName.substring(beginIndex);
        int endIndex = tmp.indexOf("_");
        if (endIndex < 0) {
            endIndex = tmp.indexOf(".sql");
        }
        if (endIndex < 0) { // basically no way because it has already been checked
            String msg = "The SQL file should ends '.sql' if you use auto-naming:";
            msg = msg + " className=" + className + " fileName=" + fileName;
            throw new IllegalStateException(msg);
        }
        tmp = tmp.substring(0, endIndex);
        final char[] charArray = tmp.toCharArray();
        final List<Character> charList = new ArrayList<Character>();
        boolean beginTarget = false;
        for (char c : charArray) {
            if (Character.isUpperCase(c)) {
                beginTarget = true;
            }
            if (beginTarget) {
                charList.add(c);
            }
        }
        if (charList.isEmpty()) {
            for (char c : charArray) {
                charList.add(c);
            }
        }
        final StringBuilder sb = new StringBuilder();
        for (Character c : charList) {
            sb.append(c);
        }
        if (className.equalsIgnoreCase(PMB_MARK)) {
            sb.append("Pmb");
        }
        return DfStringUtil.initCap(sb.toString());
    }
}
