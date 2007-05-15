package org.seasar.dbflute.helper.jdbc.schemainitializer;

import javax.sql.DataSource;

/**
 * The schema initializer for Sybase.
 * 
 * @author jflute
 */
public class DfSchemaInitializerSybase implements DfSchemaInitializer {

    protected DfSchemaInitializerJdbc _schemaInitializer = new DfSchemaInitializerJdbc();

    public void setDataSource(DataSource dataSource) {
        _schemaInitializer.setDataSource(dataSource);
    }

    public void initializeSchema() {
        _schemaInitializer.initializeSchema();
    }
}