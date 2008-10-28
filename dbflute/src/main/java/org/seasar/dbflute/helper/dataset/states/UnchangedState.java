package org.seasar.dbflute.helper.dataset.states;

import javax.sql.DataSource;

import org.seasar.dbflute.helper.dataset.DataRow;

/**
 * {Refer to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class UnchangedState implements RowState {

    public void update(DataSource dataSource, DataRow row) {
    }

    public String toString() {
        return "UNCHANGED";
    }
}