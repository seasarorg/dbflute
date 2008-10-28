package org.seasar.dbflute.helper.dataset.writers;

import org.seasar.dbflute.helper.dataset.DataTable;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public interface TableWriter {

    public void write(DataTable table);
}
