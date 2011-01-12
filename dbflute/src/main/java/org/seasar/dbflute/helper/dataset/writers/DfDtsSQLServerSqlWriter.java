package org.seasar.dbflute.helper.dataset.writers;

import javax.sql.DataSource;

import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.helper.dataset.DfDataSet;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDtsSQLServerSqlWriter extends DfDtsSqlWriter {

    public DfDtsSQLServerSqlWriter(final DataSource dataSource, UnifiedSchema unifiedSchema) {
        super(dataSource, unifiedSchema);
    }

    public void write(final DfDataSet dataSet) {
        final DfDtsTableWriter writer = new DfDtsSQLServerSqlTableWriter(getDataSource(), _unifiedSchema);
        for (int i = 0; i < dataSet.getTableSize(); ++i) {
            writer.write(dataSet.getTable(i));
        }
    }
}