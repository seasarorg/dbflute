package org.seasar.dbflute.cbean.sqlclause.query;

import org.seasar.dbflute.cbean.sqlclause.join.InnerJoinNoWaySpeaker;

/**
 * @author jflute
 * @since 0.9.9.0A (2011/07/27 Wednesday)
 */
public class QueryUsedAliasInfo {

    protected final String _usedAliasName; // NotNull
    protected final InnerJoinNoWaySpeaker _innerJoinNoWaySpeaker; // NullAllowed

    /**
     * @param usedAliasName The alias name of joined table (or local) where it is used in query. (NotNull)
     * @param innerJoinNoWaySpeaker The no-way speaker for auto-detect of inner-join. (NullAllowed)
     */
    public QueryUsedAliasInfo(String usedAliasName, InnerJoinNoWaySpeaker innerJoinNoWaySpeaker) {
        _usedAliasName = usedAliasName;
        _innerJoinNoWaySpeaker = innerJoinNoWaySpeaker;
    }

    public String getUsedAliasName() {
        return _usedAliasName;
    }

    public InnerJoinNoWaySpeaker getInnerJoinAutoDetectNoWaySpeaker() {
        return _innerJoinNoWaySpeaker;
    }
}
