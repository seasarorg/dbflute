package org.seasar.dbflute.helper.dataset.writers;

import javax.sql.DataSource;

import org.seasar.dbflute.helper.dataset.DfDataSet;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class DtsSqlWriter {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private DataSource dataSource;
    protected String _schemaName;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DtsSqlWriter(DataSource dataSource, String schemaName) {
        this.dataSource = dataSource;
        _schemaName = schemaName;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void write(DfDataSet dataSet) {
        DtsTableWriter writer = new DtsSqlTableWriter(getDataSource(), _schemaName);
        for (int i = 0; i < dataSet.getTableSize(); ++i) {
            writer.write(dataSet.getTable(i));
        }
    }
}