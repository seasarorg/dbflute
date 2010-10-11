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
}
