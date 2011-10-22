package org.seasar.dbflute.logic.jdbc.schemadiff;

/**
 * @author jflute
 */
public class DfDiffAssist {

    // ===================================================================================
    //                                                                         Same Helper
    //                                                                         ===========
    public static boolean isSame(Object next, Object previous) {
        if (next == null && previous == null) {
            return true;
        }
        if (next == null || previous == null) {
            return false;
        }
        return next.equals(previous);
    }
}
