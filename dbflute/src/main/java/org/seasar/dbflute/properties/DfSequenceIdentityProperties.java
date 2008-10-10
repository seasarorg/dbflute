package org.seasar.dbflute.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.seasar.dbflute.helper.flexiblename.DfFlexibleNameMap;

/**
 * @author jflute
 */
public final class DfSequenceIdentityProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfSequenceIdentityProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                             Sequence Definition Map
    //                                                             =======================
    protected static final String KEY_sequenceDefinitionMap = "sequenceDefinitionMap";
    protected Map<String, Object> _sequenceDefinitionMap;

    protected Map<String, Object> getSequenceDefinitionMap() {
        if (_sequenceDefinitionMap == null) {
            _sequenceDefinitionMap = mapProp("torque." + KEY_sequenceDefinitionMap, DEFAULT_EMPTY_MAP);
        }
        return _sequenceDefinitionMap;
    }

    public String getSequenceDefinitionMapSequence(String flexibleTableName) {
        final DfFlexibleNameMap<String, Object> flmap = new DfFlexibleNameMap<String, Object>(
                getSequenceDefinitionMap());
        return (String) flmap.get(flexibleTableName);
    }

    /**
     * @param checker The checker for call-back. (NotNull)
     */
    public void checkSequenceDefinitionMap(SequenceDefinitionMapChecker checker) {
        final Map<String, Object> sequenceDefinitionMap = getSequenceDefinitionMap();
        final Set<String> keySet = sequenceDefinitionMap.keySet();
        final List<String> notFoundTableNameList = new ArrayList<String>();
        for (String tableName : keySet) {
            if (!checker.hasTable(tableName)) {
                notFoundTableNameList.add(tableName);
            }
        }
        if (!notFoundTableNameList.isEmpty()) {
            throwSequenceDefinitionMapNotFoundTableException(notFoundTableNameList);
        }
    }

    protected void throwSequenceDefinitionMapNotFoundTableException(List<String> notFoundTableNameList) {
        String msg = "Look! Read the message below." + getLineSeparator();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
        msg = msg + "The table name was Not Found in the map of sequence definition!" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Not Found Table]" + getLineSeparator();
        for (String tableName : notFoundTableNameList) {
            msg = msg + tableName + getLineSeparator();
        }
        msg = msg + getLineSeparator();
        msg = msg + "[Sequence Definition]" + getLineSeparator() + _sequenceDefinitionMap + getLineSeparator();
        msg = msg + "* * * * * * * * * */";
        throw new SequenceDefinitionMapTableNotFoundException(msg);
    }

    public static interface SequenceDefinitionMapChecker {
        public boolean hasTable(String tableName);
    }

    public static class SequenceDefinitionMapTableNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public SequenceDefinitionMapTableNotFoundException(String msg) {
            super(msg);
        }
    }

    protected String getLineSeparator() {
        return System.getProperty("line.separator");
    }

    // ===================================================================================
    //                                                                  Sequence Injection
    //                                                                  ==================
    public boolean isAvailableBehaviorInsertSequenceInjection() {
        return booleanProp("torque.isAvailableBehaviorInsertSequenceInjection", true);
    }

    // ===================================================================================
    //                                                                   Sequence Assigned
    //                                                                   =================
    public boolean isAvailableSequenceAssignedIdAnnotation() {
        return booleanProp("torque.isAvailableSequenceAssignedIdAnnotation", false);
    }

    // ===================================================================================
    //                                                                Sequence Return Type
    //                                                                ====================
    // Deprecated at the future...
    public boolean hasSequenceReturnType() {
        final String value = stringProp("torque.sequenceReturnType", "");
        return value != null && value.trim().length() != 0;
    }

    public String getSequenceReturnType() {
        final String defaultSequenceType = getBasicProperties().getLanguageDependencyInfo().getDefaultSequenceType();
        return stringProp("torque.sequenceReturnType", defaultSequenceType);
    }

    // ===================================================================================
    //                                                             Identity Definition Map
    //                                                             =======================
    protected static final String KEY_identityDefinitionMap = "identityDefinitionMap";
    protected Map<String, Object> _identityDefinitionMap;

    protected Map<String, Object> getIdentityDefinitionMap() {
        if (_identityDefinitionMap == null) {
            _identityDefinitionMap = mapProp("torque." + KEY_identityDefinitionMap, DEFAULT_EMPTY_MAP);
        }
        return _identityDefinitionMap;
    }

    public String getIdentityDefinitionMapColumnName(String flexibleTableName) {
        final DfFlexibleNameMap<String, Object> flmap = new DfFlexibleNameMap<String, Object>(
                getIdentityDefinitionMap());
        return (String) flmap.get(flexibleTableName);
    }
}