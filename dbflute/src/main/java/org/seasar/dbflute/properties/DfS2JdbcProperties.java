package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

/**
 * @author jflute
 */
public final class DfS2JdbcProperties extends DfAbstractHelperProperties {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfS2JdbcProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                                      Definition Map
    //                                                                      ==============
    protected Map<String, Object> s2jdbcDefinitionMap;

    protected Map<String, Object> getS2JdbcDefinitionMap() {
        if (s2jdbcDefinitionMap == null) {
            s2jdbcDefinitionMap = mapProp("torque.s2jdbcDefinitionMap", DEFAULT_EMPTY_MAP);
        }
        return s2jdbcDefinitionMap;
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasS2JdbcDefinition() {
        return !getS2JdbcDefinitionMap().isEmpty();
    }

    // ===================================================================================
    //                                                                     Detail Property
    //                                                                     ===============
    public String getBaseEntityPackage() {
        final String value = (String) getS2JdbcDefinitionMap().get("baseEntityPackage");

        // TODO: @jflute -- 必須チェック

        return value;
    }

    public String getExtendedEntityPackage() {
        final String value = (String) getS2JdbcDefinitionMap().get("extendedEntityPackage");

        // TODO: @jflute -- 必須チェック

        return value;
    }
}