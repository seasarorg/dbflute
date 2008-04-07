package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

/**
 * @author jflute
 */
public final class DfTransferEntityProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfTransferEntityProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                                      Definition Map
    //                                                                      ==============
    protected Map<String, Object> transferEntityDefinitionMap;
    protected Map<String, Object> getTransferEntityDefinitionMap() {
        if (transferEntityDefinitionMap == null) {
            transferEntityDefinitionMap = mapProp("torque.transferEntityDefinitionMap", DEFAULT_EMPTY_MAP);
        }
        return transferEntityDefinitionMap;
    }
    
    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasTransferEntityDefinition() {
        return !getTransferEntityDefinitionMap().isEmpty();
    }
    
    // ===================================================================================
    //                                                                     Detail Property
    //                                                                     ===============
    public String getBaseEntityOutputDirectory() {
        final String value = (String)getTransferEntityDefinitionMap().get("baseEntityOutputDirectory");
        if (value == null) {
            return getBasicProperties().getJavaDir();
        }
        // TODO: @jflute -- 調整をすること
        return getBasicProperties().getJavaDir() + "/" + value;
    }
    
    public String getExtendedEntityOutputDirectory() {
        final String value = (String)getTransferEntityDefinitionMap().get("extendedEntityOutputDirectory");
        if (value == null) {
            return getBasicProperties().getJavaDir();
        }
        // TODO: @jflute -- 調整をすること
        return getBasicProperties().getJavaDir() + "/" + value;
    }
    
    public String getPackageBase() {
        final String value = (String)getTransferEntityDefinitionMap().get("packageBase");
        
        // TODO: @jflute -- デフォルトをどうする？
        
        return value;
    }
    
    public boolean isTargetFrameworkFlex() {
        final String value = (String)getTransferEntityDefinitionMap().get("targetFramework");
        return value != null && value.trim().equalsIgnoreCase("flex");
    }
    
    public boolean isTargetFrameworkS2JDBC() {
        final String value = (String)getTransferEntityDefinitionMap().get("targetFramework");
        return value != null && value.trim().equalsIgnoreCase("s2jdbc");
    }
    
    public boolean isIndependent() {
        final String value = (String)getTransferEntityDefinitionMap().get("independent");
        return value != null && value.trim().equalsIgnoreCase("true");
    }
}