package org.seasar.dbflute.util;

import java.util.Iterator;
import java.util.List;

/**
 * @author jflute
 */
public class DfNameHintUtil {

    public static final String PREFIX_MARK = "prefix:";
    public static final String SUFFIX_MARK = "suffix:";
    public static final String CONTAIN_MARK = "contain:";

    public static boolean isTargetByHint(final String name, final List<String> targetList, final List<String> exceptList) {
        if (!targetList.isEmpty()) {
            return isHitByTargetList(name, targetList);
        }
        for (final Iterator<String> ite = exceptList.iterator(); ite.hasNext();) {
            final String tableHint = (String) ite.next();
            if (isHitByTheHint(name, tableHint)) {
                return false;
            }
        }
        return true;
    }

    protected static boolean isHitByTargetList(final String name, final List<String> targetList) {
        for (final Iterator<String> ite = targetList.iterator(); ite.hasNext();) {
            final String targetTableHint = (String) ite.next();
            if (isHitByTheHint(name, targetTableHint)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isHitByTheHint(String name, String hint) {
        final String prefixMark = PREFIX_MARK;
        final String suffixMark = SUFFIX_MARK;
        final String containMark = CONTAIN_MARK;

        if (hint.toLowerCase().startsWith(prefixMark.toLowerCase())) {
            final String pureHint = hint.substring(prefixMark.length(), hint.length());
            if (name.toLowerCase().startsWith(pureHint.toLowerCase())) {
                return true;
            }
        } else if (hint.toLowerCase().startsWith(suffixMark.toLowerCase())) {
            final String pureHint = hint.substring(suffixMark.length(), hint.length());
            if (name.toLowerCase().endsWith(pureHint.toLowerCase())) {
                return true;
            }
        } else if (hint.toLowerCase().startsWith(containMark.toLowerCase())) {
            final String pureHint = hint.substring(containMark.length(), hint.length());
            if (name.toLowerCase().contains(pureHint.toLowerCase())) {
                return true;
            }
        } else {
            if (name.equalsIgnoreCase(hint)) {
                return true;
            }
        }
        return false;
    }
}