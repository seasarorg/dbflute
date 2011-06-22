package org.seasar.dbflute.cbean.chelper;

/**
 * @author jflute
 */
public interface HpCalcStatement {

    /**
     * Build the calculation statement of the column as SQL name.
     * @return The statement that has calculation. (NullAllowed: if null, means the column is not specified)
     */
    String buildStatementAsSqlName(); // e.g. called by Update Calculation

    /**
     * Build the calculation statement to the specified column.
     * @param columnExp The expression of the column. (NotNull)
     * @return The statement that has calculation. (NullAllowed: if null, means the column is not specified)
     */
    String buildStatementToSpecifidName(String columnExp); // e.g. called by ColumnQuery Calculation
}
