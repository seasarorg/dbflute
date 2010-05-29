package org.seasar.dbflute.twowaysql.node;

import org.seasar.dbflute.twowaysql.context.CommandContext;

/**
 * @author jflute
 * @since 0.9.7.0 (2010/05/29 Saturday)
 */
public interface LoopAcceptable {

    void accept(CommandContext ctx, LoopInfo loopInfo);
}
