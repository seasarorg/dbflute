package org.seasar.dbflute.cbean.sqlclause.join;

/**
 * @author jflute
 * @since 0.9.9.0A (2011/07/27 Wednesday)
 */
public abstract class InnerJoinLazyReflectorBase implements InnerJoinLazyReflector {

    protected final InnerJoinNoWaySpeaker _noWaySpeaker;

    public InnerJoinLazyReflectorBase(InnerJoinNoWaySpeaker noWaySpeaker) {
        _noWaySpeaker = noWaySpeaker;
    }

    /**
     * {@inheritDoc}
     */
    public void reflect() {
        if (_noWaySpeaker == null || !_noWaySpeaker.isNoWayInner()) {
            doReflect();
        }
    }

    /**
     * Reflect inner-join to the corresponding join info lazily.
     */
    protected abstract void doReflect();
}
