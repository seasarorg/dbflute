package org.seasar.dbflute.cbean.chelper;

import java.util.Map;

/**
 * @author jflute
 */
public interface HpCalcStatement {

    /**
     * Build the calculation statement of the column as SQL name. <br />
     * e.g. called by Update Calculation
     * @return The statement that has calculation. (NullAllowed: if null, means the column is not specified)
     */
    String buildStatementAsSqlName();

    /**
     * Build the calculation statement to the specified column. <br />
     * e.g. called by ColumnQuery Calculation
     * @param columnExp The expression of the column. (NotNull)
     * @return The statement that has calculation. (NullAllowed: if null, means the column is not specified)
     */
    String buildStatementToSpecifidName(String columnExp);

    /**
     * Build the calculation statement to the specified column. <br />
     * No cipher here because the column has already been handled cipher. <br />
     * e.g. called by ManualOrder Calculation
     * @param columnExp The expression of the column. (NotNull)
     * @param columnAliasMap The map of column alias. (NotNull)
     * @return The statement that has calculation. (NullAllowed: if null, means the column is not specified)
     */
    String buildStatementToSpecifidName(String columnExp, Map<String, String> columnAliasMap);
}
