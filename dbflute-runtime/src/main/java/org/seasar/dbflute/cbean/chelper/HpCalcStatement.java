package org.seasar.dbflute.cbean.chelper;

import org.seasar.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.dbflute.dbmeta.name.ColumnSqlName;

/**
 * @author jflute
 */
public interface HpCalcStatement {

    /**
     * Build the calculation statement of the column.
     * @param columnSqlName The SQL name of column. (NotNull)
     * @return The statement that has calculation. (Nullable: if null, means the column is not specified)
     */
    String buildStatement(ColumnSqlName columnSqlName);

    /**
     * Build the calculation statement of the column.
     * @param columnRealName The real name of column. (NotNull)
     * @return The statement that has calculation. (Nullable: if null, means the column is not specified)
     */
    String buildStatement(ColumnRealName columnRealName);
}
