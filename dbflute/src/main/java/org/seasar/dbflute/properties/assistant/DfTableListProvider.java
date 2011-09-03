package org.seasar.dbflute.properties.assistant;

import java.util.List;

import org.apache.torque.engine.database.model.Table;

/**
 * @author jflute
 * @since 0.9.9.0D (2011/09/03 Saturday)
 */
public interface DfTableListProvider {

    List<Table> provideTableList();
}
