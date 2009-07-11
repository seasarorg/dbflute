package org.seasar.dbflute.properties;

import java.util.Map;
import java.util.Properties;

/**
 * @author jflute
 */
public final class DfHibernateProperties extends DfAbstractHelperProperties {

    // 
    // ...Making (2009/07/11 Saturday)
    // 

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfHibernateProperties(Properties prop) {
        super(prop);
    }

    // ===================================================================================
    //                                                                      Definition Map
    //                                                                      ==============
    protected Map<String, Object> hibernateDefinitionMap;

    protected Map<String, Object> getHibernateDefinitionMap() { // It's closet!
        if (hibernateDefinitionMap == null) {
            hibernateDefinitionMap = mapProp("torque.hibernateDefinitionMap", DEFAULT_EMPTY_MAP);
        }
        return hibernateDefinitionMap;
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasHibernateDefinition() {
        return !getHibernateDefinitionMap().isEmpty();
    }

    // ===================================================================================
    //                                                                     Detail Property
    //                                                                     ===============
    public String getManyToOneFetch() {
        return getEntityPropertyIfNullEmpty("manyToOneFetch");
    }

    public String getOneToOneFetch() {
        return getEntityPropertyIfNullEmpty("oneToOneFetch");
    }

    public String getOneToManyFetch() {
        return getEntityPropertyIfNullEmpty("oneToManyFetch");
    }

    // ===================================================================================
    //                                                                     Property Helper
    //                                                                     ===============
    protected String getEntityPropertyRequired(String key) {
        final String value = getEntityProperty(key);
        if (value == null || value.trim().length() == 0) {
            String msg = "The property '" + key + "' should not be null or empty:";
            msg = msg + " hibernateDefinitionMap=" + getHibernateDefinitionMap();
            throw new IllegalStateException(msg);
        }
        return value;
    }

    protected String getEntityPropertyIfNullEmpty(String key) {
        final String value = getEntityProperty(key);
        if (value == null) {
            return "";
        }
        return value;
    }

    protected String getEntityProperty(String key) {
        final String value = (String) getHibernateDefinitionMap().get(key);
        return value;
    }
}