package org.seasar.dbflute.cbean.sqlclause.union;

/**
 * @author jflute
 * @since 0.9.9.4C (2012/05/01 Tuesday)
 */
public interface UnionClauseProvider {

    /**
     * Provide the clause of union query.
     * @return The clause string. (NotNull)
     */
    String provide();
}
