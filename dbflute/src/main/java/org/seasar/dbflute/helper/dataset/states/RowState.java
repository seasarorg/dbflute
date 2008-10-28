package org.seasar.dbflute.helper.dataset.states;

import javax.sql.DataSource;

import org.seasar.dbflute.helper.dataset.DataRow;

/**
 * Row States. {Refer to S2Container}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public interface RowState {

    void update(DataSource dataSource, DataRow row);
}