package org.seasar.dbflute.task.replaceschema.migrate;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.AppData;
import org.apache.torque.engine.database.transform.XmlToAppData.XmlReadingTableFilter;
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
        try {
            _log.info("...Reading SchemaXML");
            schemaFileReader.read();
        } catch (IOException e) {
            String msg = "Failed to read the SchemaXML file.";
            throw new IllegalStateException(msg);
        }

        _log.info("...Outputting DataXlsTemplate for LoadData");
        final AppData schemaData = schemaFileReader.getSchemaData();
        final DfLoadDataMigration migration = new DfLoadDataMigration(getDataSource());
        migration.outputData(schemaData.getDatabase());
    }

    protected DfSchemaXmlReader createSchemaFileReader() {
        final XmlReadingTableFilter tableFilter = null; // all tables are dumped
        return DfSchemaXmlReader.createAsMain(getTargetDatabase(), tableFilter);
    }
}
