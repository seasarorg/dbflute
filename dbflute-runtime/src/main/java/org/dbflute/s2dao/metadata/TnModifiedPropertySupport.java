package org.dbflute.s2dao.metadata;

import java.util.Set;

public interface TnModifiedPropertySupport {
    Set<String> getModifiedPropertyNames(Object obj);
}
