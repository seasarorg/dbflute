package org.seasar.dbflute.cbean.sqlclause.join;

/**
 * @author jflute
 * @since 0.9.9.0A (2011/07/27 Wednesday)
 */
public abstract class InnerJoinAutoDetectReflectorBase implements InnerJoinAutoDetectReflector {

    protected final InnerJoinAutoDetectNoWaySpeaker _noWayDeterminator;

    public InnerJoinAutoDetectReflectorBase(InnerJoinAutoDetectNoWaySpeaker noWayDeterminator) {
        _noWayDeterminator = noWayDeterminator;
    }

    public void reflect() {
        if (_noWayDeterminator == null || !_noWayDeterminator.isNoWayInner()) {
            doReflect();
        }
    }

    protected abstract void doReflect();
}
