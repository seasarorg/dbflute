package org.seasar.dbflute.properties.assistant;

import org.apache.torque.engine.database.model.Table;

/**
 * @author jflute
 * @since 0.9.5.2 (2009/07/06 Monday)
 */
public interface DfTableFinder {

    Table findTable(String tableName);
}
