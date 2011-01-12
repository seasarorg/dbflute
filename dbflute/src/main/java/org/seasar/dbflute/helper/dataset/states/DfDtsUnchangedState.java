package org.seasar.dbflute.helper.dataset.states;

import javax.sql.DataSource;

import org.seasar.dbflute.helper.dataset.DfDataRow;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDtsUnchangedState implements DfDtsRowState {

    public void update(DataSource dataSource, DfDataRow row) {
    }

    public String toString() {
        return "UNCHANGED";
    }
}