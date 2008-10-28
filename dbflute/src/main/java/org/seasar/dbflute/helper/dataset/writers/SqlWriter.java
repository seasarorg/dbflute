package org.seasar.dbflute.helper.dataset.writers;

import javax.sql.DataSource;

import org.seasar.dbflute.helper.dataset.DataSet;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public class SqlWriter {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private DataSource dataSource;
    protected String _schemaName;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public SqlWriter(DataSource dataSource, String schemaName) {
        this.dataSource = dataSource;
        _schemaName = schemaName;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void write(DataSet dataSet) {
        TableWriter writer = new SqlTableWriter(getDataSource(), _schemaName);
        for (int i = 0; i < dataSet.getTableSize(); ++i) {
            writer.write(dataSet.getTable(i));
        }
    }
}