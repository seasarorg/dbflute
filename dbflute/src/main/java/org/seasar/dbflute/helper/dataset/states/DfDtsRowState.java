package org.seasar.dbflute.helper.dataset.states;

import javax.sql.DataSource;

import org.seasar.dbflute.helper.dataset.DfDataRow;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public interface DfDtsRowState {

    void update(DataSource dataSource, DfDataRow row);
}