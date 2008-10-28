package org.seasar.dbflute.helper.dataset.states;

/**
 * {Refer to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public interface RowStates {

    RowState UNCHANGED = new UnchangedState();

    RowState CREATED = new CreatedState();

    RowState MODIFIED = new ModifiedState();

    RowState REMOVED = new RemovedState();
}
