package org.seasar.dbflute.s2dao.metadata;

import java.util.Set;

/**
 * {Refers to a S2Dao's class and Extends it}
 * @author jflute
 */
public interface TnModifiedPropertySupport {
    Set<String> getModifiedPropertyNames(Object obj);
}
