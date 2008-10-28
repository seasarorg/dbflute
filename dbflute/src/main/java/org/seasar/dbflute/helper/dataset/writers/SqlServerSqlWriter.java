package org.seasar.dbflute.helper.dataset.writers;

import javax.sql.DataSource;

import org.seasar.dbflute.helper.dataset.DataSet;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class SqlServerSqlWriter extends SqlWriter {

    public SqlServerSqlWriter(final DataSource dataSource, String schemaName) {
        super(dataSource, schemaName);
    }

    public void write(final DataSet dataSet) {
        final TableWriter writer = new SqlServerSqlTableWriter(getDataSource(), _schemaName);
        for (int i = 0; i < dataSet.getTableSize(); ++i) {
            writer.write(dataSet.getTable(i));
        }
    }
}