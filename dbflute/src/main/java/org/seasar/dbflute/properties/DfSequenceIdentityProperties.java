package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

import org.seasar.dbflute.helper.flexiblename.DfFlexibleNameMap;

/**
 * Build properties for Torque.
 * 
 * @author jflute
 */
public final class DfSequenceIdentityProperties extends DfAbstractHelperProperties {

    //    private static final Log _log = LogFactory.getLog(GeneratedClassPackageProperties.class);

    /**
     * Constructor.
     */
    public DfSequenceIdentityProperties(Properties prop) {
        super(prop);
    }

    // ===============================================================================
    //                                              Properties - Sequence and Identity
    //                                              ==================================
    public static final String KEY_sequenceDefinitionMap = "sequenceDefinitionMap";
    protected Map<String, Object> _sequenceDefinitionMap;

    public Map<String, Object> getSequenceDefinitionMap() {
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

    public String getSequenceReturnType() {
        return stringProp("torque.sequenceReturnType", "java.math.BigDecimal");
    }

    public static final String KEY_identityDefinitionMap = "identityDefinitionMap";
    protected Map<String, Object> _identityDefinitionMap;

    public Map<String, Object> getIdentityDefinitionMap() {
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