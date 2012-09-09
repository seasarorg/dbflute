package org.seasar.dbflute.helper.dataset.writers;

import javax.sql.DataSource;

import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.helper.dataset.DfDataSet;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DfDtsSqlWriter {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource _dataSource;
    protected UnifiedSchema _unifiedSchema;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfDtsSqlWriter(DataSource dataSource, UnifiedSchema unifiedSchema) {
        _dataSource = dataSource;
        _unifiedSchema = unifiedSchema;
    }

    public DataSource getDataSource() {
        return _dataSource;
    }

    public void write(DfDataSet dataSet) {
        DfDtsTableWriter writer = new DfDtsSqlTableWriter(getDataSource(), _unifiedSchema);
        for (int i = 0; i < dataSet.getTableSize(); ++i) {
            writer.write(dataSet.getTable(i));
        }
    }
}