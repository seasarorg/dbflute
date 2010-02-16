package org.seasar.dbflute.helper.dataset.writers;

import org.seasar.dbflute.helper.dataset.DfDataTable;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public interface DtsTableWriter {

    public void write(DfDataTable table);
}
