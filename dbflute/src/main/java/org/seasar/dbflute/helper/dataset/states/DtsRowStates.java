package org.seasar.dbflute.helper.dataset.states;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public interface DtsRowStates {

    DtsRowState UNCHANGED = new DtsUnchangedState();

    DtsRowState CREATED = new DtsCreatedState();

    DtsRowState MODIFIED = new DtsModifiedState();

    DtsRowState REMOVED = new DtsRemovedState();
}
