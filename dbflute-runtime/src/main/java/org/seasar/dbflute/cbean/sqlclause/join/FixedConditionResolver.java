package org.seasar.dbflute.cbean.sqlclause.join;

/**
 * @author jflute
 * @since 0.9.7.5 (2010/10/11 Monday)
 */
public interface FixedConditionResolver {

    /**
     * Resolve variables on fixed-condition.
     * @param fixedCondition The string of fixed-condition. (NotNull)
     * @return Resolved fixed-condition. (NotNull)
     */
    String resolveVariable(String fixedCondition);

    /**
     * Resolve fixed InlineView for fixed-condition.
     * @param foreignTable The SQL name of foreign table that has fixed-condition. (NotNull) 
     * @param treatedAsInnerJoin Does the join treated as inner-join?
     * @return Resolved foreign table expression. (NotNull)
     */
    String resolveFixedInlineView(String foreignTable, boolean treatedAsInnerJoin);

    /**
     * Does the fixed-condition have over-relation?
     * @param fixedCondition The string of fixed-condition. (NotNull)
     * @return Determination. (true or false)
     */
    boolean hasOverRelation(String fixedCondition);
}
