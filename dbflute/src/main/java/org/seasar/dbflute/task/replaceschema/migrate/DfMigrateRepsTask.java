package org.seasar.dbflute.task.replaceschema.migrate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.AppData;
import org.seasar.dbflute.logic.jdbc.schemaxml.DfSchemaXmlReader;
import org.seasar.dbflute.logic.replaceschema.migratereps.DfLoadDataMigration;
import org.seasar.dbflute.task.bs.DfAbstractTask;

/**
 * @author jflute
 */
public class DfMigrateRepsTask extends DfAbstractTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfMigrateRepsTask.class);

    // ===================================================================================
    //                                                                          DataSource
    //                                                                          ==========
    @Override
    protected boolean isUseDataSource() {
        return true;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected void doExecute() {
        _log.info("");
        _log.info("* * * * * * * * * * * * * * * * * * * *");
        _log.info("*                                     *");
        _log.info("* Migrate from DB to ReplaceSchema    *");
        _log.info("* (Output test data from current DB)  *");
        _log.info("*                                     *");
        _log.info("* * * * * * * * * * * * * * * * * * * *");

        // JDBC task should be executed before
        final DfSchemaXmlReader schemaFileReader = createSchemaFileReader();
        _log.info("...Reading SchemaXML");
        final AppData schemaData = schemaFileReader.read();

        _log.info("...Outputting DataXlsTemplate for LoadData");
        final DfLoadDataMigration migration = new DfLoadDataMigration(getDataSource());
        migration.outputData(schemaData.getDatabase());
    }

    protected DfSchemaXmlReader createSchemaFileReader() {
        return DfSchemaXmlReader.createAsCoreToManage(); // all tables are dumped
    }
}
