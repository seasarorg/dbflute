package org.seasar.dbflute.properties.assistant;

/**
 * @author jflute
 * @since 0.9.9.0D (2011/09/03 Saturday)
 */
public interface DfTableDeterminator {

    boolean hasTable(String tableName);

    boolean hasTableColumn(String tableName, String columnName);
}
