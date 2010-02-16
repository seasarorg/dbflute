package org.seasar.dbflute.helper.dataset.writers;

import javax.sql.DataSource;

import org.seasar.dbflute.helper.dataset.DfDataSet;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDtsSqlServerSqlWriter extends DfDtsSqlWriter {

    public DfDtsSqlServerSqlWriter(final DataSource dataSource, String schemaName) {
        super(dataSource, schemaName);
    }

    public void write(final DfDataSet dataSet) {
        final DfDtsTableWriter writer = new DfDtsSqlServerSqlTableWriter(getDataSource(), _schemaName);
        for (int i = 0; i < dataSet.getTableSize(); ++i) {
            writer.write(dataSet.getTable(i));
        }
    }
}