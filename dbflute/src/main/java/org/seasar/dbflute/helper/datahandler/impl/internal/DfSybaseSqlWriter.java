package org.seasar.dbflute.helper.datahandler.impl.internal;

import javax.sql.DataSource;

import org.seasar.extension.dataset.DataSet;
import org.seasar.extension.dataset.TableWriter;
import org.seasar.extension.dataset.impl.SqlWriter;

/**
 * @author jflute
 */
public class DfSybaseSqlWriter extends SqlWriter {
    public DfSybaseSqlWriter(DataSource dataSource) {
        super(dataSource);
    }

    public void write(DataSet dataSet) {
        TableWriter writer = new DfSybaseSqlTableWriter(getDataSource());
        for (int i = 0; i < dataSet.getTableSize(); i++)
            writer.write(dataSet.getTable(i));

    }
}
