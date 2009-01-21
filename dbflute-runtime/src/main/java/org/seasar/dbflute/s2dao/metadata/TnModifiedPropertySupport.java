package org.seasar.dbflute.s2dao.metadata;

import java.util.Set;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public interface TnModifiedPropertySupport {
    Set<String> getModifiedPropertyNames(Object obj);
}
