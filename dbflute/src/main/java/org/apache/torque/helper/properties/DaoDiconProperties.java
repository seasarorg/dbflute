package org.apache.torque.helper.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class DaoDiconProperties extends AbstractHelperProperties {

    //    private static final Log _log = LogFactory.getLog(GeneratedClassPackageProperties.class);

    /**
     * Constructor.
     */
    public DaoDiconProperties(Properties prop) {
        super(prop);
    }

    // ===============================================================================
    //                                               Properties - DaoDicon Information
    //                                               =================================
    public String getDaoDiconNamespace() {
        return stringProp("torque.daoDiconNamespace", "dao");
    }

    public String getDaoDiconPackageName() {
        return stringProp("torque.daoDiconPackageName", "");
    }

    public String getDaoDiconFileName() {
        return stringProp("torque.daoDiconFileName", "dao.dicon");
    }

    public String getJ2eeDiconResourceName() {
        return stringProp("torque.j2eeDiconResourceName", "j2ee.dicon");
    }

    public String getRequiredTxComponentName() {
        String defaultValue = null;
        if (getBasicProperties().isTargetLanguageJava()) {
            defaultValue = "requiredTx";
        } else if (getBasicProperties().isTargetLanguageCSharp()) {
            defaultValue = "LocalRequiredTx";
        } else {
            String msg = "The language is unsupported: " + getBasicProperties().getTargetLanguage();
            throw new IllegalStateException(msg);
        }
        return stringProp("torque.requiredTxComponentName", defaultValue);
    }

    public String getRequiresNewTxComponentName() {
        String defaultValue = null;
        if (getBasicProperties().isTargetLanguageJava()) {
            defaultValue = "requiresNewTx";
        } else if (getBasicProperties().isTargetLanguageCSharp()) {
            defaultValue = "LocalRequiresNewTx";
        } else {
            String msg = "The language is unsupported: " + getBasicProperties().getTargetLanguage();
            throw new IllegalStateException(msg);
        }
        return stringProp("torque.requiresNewTxComponentName", defaultValue);
    }

    public static final String KEY_daoDiconOtherIncludeDefinitionMap = "daoDiconOtherIncludeDefinitionMap";
    protected Map<String, Object> _daoDiconOtherIncludeDefinitionMap;

    public Map<String, Object> getDaoDiconOtherIncludeDefinitionMap() {
        if (_daoDiconOtherIncludeDefinitionMap == null) {
            _daoDiconOtherIncludeDefinitionMap = mapProp("torque." + KEY_daoDiconOtherIncludeDefinitionMap,
                    DEFAULT_EMPTY_MAP);
        }
        return _daoDiconOtherIncludeDefinitionMap;
    }

    public List<String> getDaoDiconOtherIncludePathList() {
        return new ArrayList<String>(getDaoDiconOtherIncludeDefinitionMap().keySet());
    }
}