package org.seasar.dbflute.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author jflute
 */
public final class DfBuriProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfBuriProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                                      Definition Map
    //                                                                      ==============
    protected Map<String, Object> buriDefinitionMap;

    protected Map<String, Object> getBuriDefinitionMap() {
        if (buriDefinitionMap == null) {
            buriDefinitionMap = mapProp("torque.buriDefinitionMap", DEFAULT_EMPTY_MAP);
        }
        return buriDefinitionMap;
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean isUseBuri() {
        if (hasBuriDefinitionMap()) {
            return true;
        }
        boolean isUseBuri = booleanProp("torque.isUseBuri", false);
        if (isUseBuri) {
            return true;
        }
        return booleanProp("torque.isUseS2Buri", false);
    }

    protected boolean hasBuriDefinitionMap() {
        return !getBuriDefinitionMap().isEmpty();
    }

    public boolean isGenerateBao() {
        if (!isUseBuri()) {
            return false;
        }
        final String baseBaoPackage = getBaseBaoPackage();
        return baseBaoPackage != null && baseBaoPackage.trim().length() > 0;
    }

    public boolean isTargetTable(String tableName) {
        if (!isUseBuri()) {
            return false;
        }
        final List<String> targetTableList = getTargetTableList();
        if (targetTableList.isEmpty()) {
            return true;
        }
        for (String tableNameHint : targetTableList) {
            if (isHitByTheHint(tableName, tableNameHint)) {
                return true;
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                                   Target Table List
    //                                                                   =================
    protected List<String> getTargetTableList() {
        final List<String> ls = getBuriPropertyAsList("targetTableList");
        if (ls == null) {
            return new ArrayList<String>();
        }
        return ls;
    }

    // ===================================================================================
    //                                                                    Output Directory
    //                                                                    ================
    /**
     * @return The directory for output. (NotNull)
     */
    public String getOutputDirectory() { // not required
        final String value = (String) getBuriDefinitionMap().get("outputDirectory");
        if (value == null) {
            return getBasicProperties().getOutputDirectory();
        }
        return getBasicProperties().getOutputDirectory() + "/" + value;
    }

    // ===================================================================================
    //                                                                 Activity Definition
    //                                                                 ===================
    public String getBuriPackage() { // required if generate status classes
        return getBuriPropertyIfNullEmpty("buriPackage");
    }
    
    // map:{
    //     ; activityDefinitionMap = map:{
    //         ; [package].[name] = list:{ abc, def }
    //     }
    // }

    public String getBaseBaoPackage() { // required if generate BAO
        return getBuriPropertyIfNullEmpty("baseBaoPackage");
    }

    public String getExtendedBaoPackage() { // required if generate BAO
        final String key = "extendedBaoPackage";
        final String baseBaoPackage = getBaseBaoPackage();
        if (baseBaoPackage != null && baseBaoPackage.trim().length() > 0) {
            return getBuriPropertyRequired(key);
        } else {
            return getBuriPropertyIfNullEmpty(key);
        }
    }

    // ===================================================================================
    //                                                              Buri Table Information
    //                                                              ======================
    public boolean isBuriInternalTable(String javaName) {
        if (javaName == null || javaName.trim().length() == 0) {
            return false;
        }
        final String str = javaName.trim().toLowerCase();
        return "buripath".equals(str) && "buridata".equals(str) && "buribranch".equals(str) && "buristate".equals(str)
                && "buripathdata".equals(str);
    }

    // ===================================================================================
    //                                                                     Property Helper
    //                                                                     ===============
    protected String getBuriPropertyRequired(String key) {
        final String value = getBuriProperty(key);
        if (value == null || value.trim().length() == 0) {
            String msg = "The property '" + key + "' should not be null or empty:";
            msg = msg + " buriDefinitionMap=" + getBuriDefinitionMap();
            throw new IllegalStateException(msg);
        }
        return value;
    }

    protected String getBuriPropertyIfNullEmpty(String key) {
        final String value = getBuriProperty(key);
        if (value == null) {
            return "";
        }
        return value;
    }

    protected String getBuriProperty(String key) {
        final String value = (String) getBuriDefinitionMap().get(key);
        return value;
    }

    protected boolean isBuriProperty(String key) {
        final String value = (String) getBuriDefinitionMap().get(key);
        return value != null && value.trim().equalsIgnoreCase("true");
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getBuriPropertyAsMap(String key) {
        return (Map<String, Object>) getBuriDefinitionMap().get(key);
    }

    @SuppressWarnings("unchecked")
    protected List<String> getBuriPropertyAsList(String key) {
        return (List<String>) getBuriDefinitionMap().get(key);
    }
}