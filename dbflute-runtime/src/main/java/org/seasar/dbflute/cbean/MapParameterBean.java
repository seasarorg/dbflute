package org.seasar.dbflute.cbean;

import java.util.Map;

/**
 * The bean of map parameter.
 * @author jflute
 */
public interface MapParameterBean {

    /**
     * Get the map of parameter.
     * @return The map of parameter. (Nullable)
     */
    public Map<String, Object> getParameterMap();
}
