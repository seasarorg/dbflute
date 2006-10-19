package org.apache.torque.helper.stateless;

public class NameHintUtil {

    public static final String PREFIX_MARK = "prefix:";
    public static final String SUFFIX_MARK = "suffix:";

    public static boolean isHitByTheHint(String target, String hint) {
        final String prefixMark = PREFIX_MARK;
        final String suffixMark = SUFFIX_MARK;

        if (hint.toLowerCase().startsWith(prefixMark.toLowerCase())) {
            final String pureTableHint = hint.substring(prefixMark.length(), hint.length());
            if (target.toLowerCase().startsWith(pureTableHint.toLowerCase())) {
                return true;
            }
        } else if (hint.toLowerCase().startsWith(suffixMark.toLowerCase())) {
            final String pureTableHint = hint.substring(suffixMark.length(), hint.length());
            if (target.toLowerCase().endsWith(pureTableHint.toLowerCase())) {
                return true;
            }
        } else {
            if (target.equalsIgnoreCase(hint)) {
                return true;
            }
        }
        return false;
    }
}