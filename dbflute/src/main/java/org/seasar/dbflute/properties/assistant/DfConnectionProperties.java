package org.seasar.dbflute.properties.assistant;

import java.util.List;

/**
 * @author jflute
 * @since 0.9.6.2 (2009/12/08 Tuesday)
 */
public class DfConnectionProperties {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String OBJECT_TYPE_TABLE = "TABLE";
    public static final String OBJECT_TYPE_VIEW = "VIEW";
    public static final String OBJECT_TYPE_SYNONYM = "SYNONYM";
    public static final String OBJECT_TYPE_ALIAS = "ALIAS";

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public static boolean hasObjectTypeSynonym(List<String> objectTypeTargetList) {
        if (objectTypeTargetList == null || objectTypeTargetList.isEmpty()) {
            return false;
        }
        for (String objectType : objectTypeTargetList) {
            if (OBJECT_TYPE_SYNONYM.equalsIgnoreCase(objectType)) {
                return true;
            }
        }
        return false;
    }
}
