package org.seasar.dbflute.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.exception.DfIllegalPropertySettingException;
import org.seasar.dbflute.exception.DfIllegalPropertyTypeException;
import org.seasar.dbflute.helper.collection.DfFlexibleMap;
import org.seasar.dbflute.logic.factory.DfSequenceExtractorFactory;
import org.seasar.dbflute.logic.jdbc.metadata.info.DfSequenceMetaInfo;
import org.seasar.dbflute.logic.jdbc.metadata.sequence.DfSequenceExtractor;

/**
 * @author jflute
 */
public final class DfSequenceIdentityProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Log _log = LogFactory.getLog(DfSequenceIdentityProperties.class);

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

    protected Map<String, String> getSequenceDefinitionMap() {
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

    public Map<String, String> getTableSequenceMap() {
        final Map<String, String> sequenceDefinitionMap = getSequenceDefinitionMap();
        final Map<String, String> resultMap = new LinkedHashMap<String, String>();
        final Set<String> keySet = sequenceDefinitionMap.keySet();
        for (String tableName : keySet) {
            resultMap.put(tableName, getSequenceName(tableName));
        }
        return resultMap;
    }

    public String getSequenceName(String tableName) {
        final DfFlexibleMap<String, String> flmap = new DfFlexibleMap<String, String>(getSequenceDefinitionMap());
        final String sequence = flmap.get(tableName);
        if (sequence == null) {
            return null;
        }
        final String hintMark = ":";
        final int hintMarkIndex = sequence.lastIndexOf(hintMark);
        if (hintMarkIndex < 0) {
            return sequence;
        }
        return sequence.substring(0, hintMarkIndex);
    }

    public String getSequenceCacheSize(DataSource dataSource, String schemaName, String tableName) {
        final DfFlexibleMap<String, String> flmap = new DfFlexibleMap<String, String>(getSequenceDefinitionMap());
        final String sequenceProp = flmap.get(tableName);
        if (sequenceProp == null) {
            return null;
        }
        final String hintMark = ":";
        final int hintMarkIndex = sequenceProp.lastIndexOf(hintMark);
        if (hintMarkIndex < 0) {
            return null;
        }
        final String hint = sequenceProp.substring(hintMarkIndex + hintMark.length()).trim();
        final String incrementMark = "cache(";
        final int incrementMarkIndex = hint.indexOf(incrementMark);
        if (incrementMarkIndex < 0) {
            return null;
        }
        final String cacheValue = hint.substring(incrementMarkIndex + incrementMark.length()).trim();
        final String endMark = ")";
        final int endMarkIndex = cacheValue.indexOf(endMark);
        if (endMarkIndex < 0) {
            String msg = "The increment size setting needs end mark ')':";
            msg = msg + " sequence=" + sequenceProp;
            throw new IllegalStateException(msg);
        }
        final String cacheSize = cacheValue.substring(0, endMarkIndex).trim();
        if (cacheSize != null && cacheSize.trim().length() > 0) {
            return cacheSize;
        }
        final String sequenceName = getSequenceName(tableName);
        final Map<String, DfSequenceMetaInfo> sequenceMap = getSequenceMap(dataSource);
        final String incrementSize = getSequenceIncrementSize(dataSource, schemaName, sequenceName, sequenceMap);
        if (incrementSize != null) {
            return incrementSize;
        }
        String msg = "Failed to get the cache size of sequence:" + ln();
        msg = msg + " /- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -" + ln();
        msg = msg + " schema = " + schemaName + " table = " + tableName;
        msg = msg + " sequenceProp = " + sequenceProp + ln();
        msg = msg + " sequenceName = " + sequenceName + ln();
        msg = msg + " sequenceMap(" + sequenceMap.size() + "):" + ln();
        final Set<Entry<String, DfSequenceMetaInfo>> entrySet = sequenceMap.entrySet();
        for (Entry<String, DfSequenceMetaInfo> entry : entrySet) {
            msg = msg + "   " + entry.getKey() + " = " + entry.getValue() + ln();
        }
        msg = msg + " - - - - - - - - - -/";
        throw new DfIllegalPropertySettingException(msg);
    }

    protected String getSequenceIncrementSize(DataSource dataSource, String schemaName, String sequenceName,
            Map<String, DfSequenceMetaInfo> sequenceMap) {
        final String sequenceInfoKey;
        if (schemaName != null && schemaName.trim().length() > 0) {
            if (getBasicProperties().isDatabasePublicSchemaSupported() && schemaName.trim().equalsIgnoreCase("public")) {
                sequenceInfoKey = sequenceName;
            } else {
                sequenceInfoKey = schemaName + "." + sequenceName;
            }
        } else {
            sequenceInfoKey = sequenceName;
        }
        final DfSequenceMetaInfo info = sequenceMap.get(sequenceInfoKey);
        if (info != null) {
            final Integer incrementSize = info.getIncrementSize();
            if (incrementSize != null) {
                return incrementSize.toString();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    protected Map<String, DfSequenceMetaInfo> _sequenceMetaInfoMap;

    protected Map<String, DfSequenceMetaInfo> getSequenceMap(DataSource dataSource) {
        if (_sequenceMetaInfoMap != null) {
            return _sequenceMetaInfoMap;
        }
        final DfSequenceExtractorFactory factory = new DfSequenceExtractorFactory(dataSource, getBasicProperties(),
                getDatabaseProperties());
        final DfSequenceExtractor sequenceExtractor = factory.createSequenceExtractor();
        if (sequenceExtractor != null) {
            try {
                _sequenceMetaInfoMap = sequenceExtractor.getSequenceMap();
            } catch (RuntimeException continued) { // because of supplement
                _log.info("Failed to get sequence map: " + continued.getMessage());
                _sequenceMetaInfoMap = new HashMap<String, DfSequenceMetaInfo>();
            }
        } else {
            _sequenceMetaInfoMap = new HashMap<String, DfSequenceMetaInfo>();
        }
        return _sequenceMetaInfoMap;
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
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The table name was Not Found in the map of sequence definition!" + ln();
        msg = msg + ln();
        msg = msg + "[Not Found Table]" + ln();
        for (String tableName : notFoundTableNameList) {
            msg = msg + tableName + ln();
        }
        msg = msg + ln();
        msg = msg + "[Sequence Definition]" + ln() + _sequenceDefinitionMap + ln();
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

    protected String ln() {
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

    // # /---------------------------------------------------------------------------
    // # identityDefinitionMap: (Default 'map:{}')
    // # 
    // # The relation mappings between identity and column of table.
    // # Basically you don't need this property because DBFlute
    // # can get the information about identity from JDBC automatically.
    // # The table names and column names are treated as case insensitive.
    // # 
    // # Example:
    // # map:{
    // #     ; PURCHASE     = PURCHASE_ID
    // #     ; MEMBER       = MEMBER_ID
    // #     ; MEMBER_LOGIN = MEMBER_LOGIN_ID
    // #     ; PRODUCT      = PRODUCT_ID
    // # }
    // #
    // # *The line that starts with '#' means comment-out.
    // #
    // map:{
    //     #; PURCHASE     = PURCHASE_ID
    //     #; MEMBER       = MEMBER_ID
    //     #; MEMBER_LOGIN = MEMBER_LOGIN_ID
    //     #; PRODUCT      = PRODUCT_ID
    // }
    // # ----------------/

    protected Map<String, Object> getIdentityDefinitionMap() {
        if (_identityDefinitionMap == null) {
            _identityDefinitionMap = mapProp("torque." + KEY_identityDefinitionMap, DEFAULT_EMPTY_MAP);
        }
        return _identityDefinitionMap;
    }

    public String getIdentityColumnName(String flexibleTableName) {
        final DfFlexibleMap<String, Object> flmap = new DfFlexibleMap<String, Object>(getIdentityDefinitionMap());
        return (String) flmap.get(flexibleTableName);
    }
}