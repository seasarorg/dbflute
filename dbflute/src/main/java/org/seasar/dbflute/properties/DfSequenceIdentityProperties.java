package org.seasar.dbflute.properties;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.seasar.dbflute.exception.DfIllegalPropertyTypeException;
import org.seasar.dbflute.helper.collection.DfFlexibleMap;

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
    protected Map<String, String> _sequenceDefinitionMap;

    public Map<String, String> getSequenceDefinitionMap() {
        if (_sequenceDefinitionMap == null) {
            LinkedHashMap<String, String> tmpMap = new LinkedHashMap<String, String>();
            Map<String, Object> originalMap = mapProp("torque." + KEY_sequenceDefinitionMap, DEFAULT_EMPTY_MAP);
            Set<Entry<String, Object>> entrySet = originalMap.entrySet();
            for (Entry<String, Object> entry : entrySet) {
                String tableName = entry.getKey();
                Object sequenceValue = entry.getValue();
                if (!(sequenceValue instanceof String)) {
                    String msg = "The value of sequence map should be string:";
                    msg = msg + " sequenceValue=" + sequenceValue + " map=" + originalMap;
                    throw new DfIllegalPropertyTypeException(msg);
                }
                tmpMap.put(tableName, (String) sequenceValue);
            }
            _sequenceDefinitionMap = tmpMap;
        }
        return _sequenceDefinitionMap;
    }

    public String getSequenceDefinitionMapSequence(String flexibleTableName) {
        final DfFlexibleMap<String, String> flmap = new DfFlexibleMap<String, String>(getSequenceDefinitionMap());
        return flmap.get(flexibleTableName);
    }

    /**
     * @param checker The checker for call-back. (NotNull)
     */
    public void checkSequenceDefinitionMap(SequenceDefinitionMapChecker checker) {
        final Map<String, String> sequenceDefinitionMap = getSequenceDefinitionMap();
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
    //                                                                Sequence Return Type
    //                                                                ====================
    public String getSequenceReturnType() { // It's not property!
        return getBasicProperties().getLanguageDependencyInfo().getDefaultSequenceType();
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
        final DfFlexibleMap<String, Object> flmap = new DfFlexibleMap<String, Object>(getIdentityDefinitionMap());
        return (String) flmap.get(flexibleTableName);
    }
}