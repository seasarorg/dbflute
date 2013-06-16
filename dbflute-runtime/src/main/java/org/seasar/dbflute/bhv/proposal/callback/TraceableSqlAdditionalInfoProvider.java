package org.seasar.dbflute.bhv.proposal.callback;

/**
 * @author jflute
 */
public interface TraceableSqlAdditionalInfoProvider {

	/**
	 * Provide additional info for traceable SQL.
	 * @return The string expression of additional info. (NullAllowed: if null, no additional info)
	 */
	String provide();
}
