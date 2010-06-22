package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.dbmeta.name.ColumnSqlName;

/**
 * @author jflute
 */
public interface HpCalcStatement {

    /**
     * Build the calculation statement of the column.
     * @param columnSqlName The SQL name of column. (NotNull)
     * @return The statement as update value. (Nullable: if null, means the column is not specified)
     */
    String buildStatement(ColumnSqlName columnSqlName);
}
