package org.seasar.dbflute.s2dao.metadata;

import java.util.Set;

/**
 * {Refers to S2Container's utility and Extends it}
 * @author jflute
 */
public interface TnModifiedPropertySupport {
    Set<String> getModifiedPropertyNames(Object obj);
}
