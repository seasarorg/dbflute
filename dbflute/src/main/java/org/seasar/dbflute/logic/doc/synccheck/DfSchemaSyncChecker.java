package org.seasar.dbflute.logic.doc.synccheck;

import java.io.File;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.exception.DfSchemaSyncCheckTragedyResultException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.helper.jdbc.connection.DfDataSourceHandler;
import org.seasar.dbflute.helper.jdbc.connection.DfSimpleDataSource;
import org.seasar.dbflute.logic.jdbc.schemadiff.DfSchemaDiff;
import org.seasar.dbflute.logic.jdbc.schemaxml.DfSchemaXmlSerializer;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.8.4 (2011/05/29 Sunday)
 */
public class DfSchemaSyncChecker {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static Log _log = LogFactory.getLog(DfSchemaSyncChecker.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DataSource _mainDs;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DfSchemaSyncChecker(DataSource mainDs) {
        _mainDs = mainDs;
    }

    // ===================================================================================
    //                                                                          Check Sync
    //                                                                          ==========
    public void checkSync() {
        clearOutputResource();
        final DfSchemaXmlSerializer serializer = diffSchema();
        _log.info("...Checking the schema synchronized");
        final DfSchemaDiff schemaDiff = serializer.getSchemaDiff();
        if (schemaDiff.hasDiff()) {
            _log.info(" -> the schema has differences");
            throwSchemaSyncCheckTragedyResultException();
        } else { // synchronized
            _log.info(" -> the schema is synchronized");
            clearOutputResource();
        }
    }

    protected void clearOutputResource() {
        final File schemaXml = new File(getSchemaXml());
        if (schemaXml.exists()) {
            schemaXml.delete();
        }
        final File diffMapFile = new File(getDiffMapFile());
        if (diffMapFile.exists()) {
            diffMapFile.delete();
        }
        final File resultFile = new File(getResultFilePath());
        if (resultFile.exists()) {
            resultFile.delete();
        }
    }

    protected DfSchemaXmlSerializer diffSchema() {
        serializeTargetSchema(); // previous
        return serializeMainSchema(); // next
    }

    protected DfSchemaXmlSerializer serializeMainSchema() {
        final DfSchemaXmlSerializer mainSerializer = createMainSerializer();
        mainSerializer.suppressUnifiedSchema(); // because of comparison with other schema
        mainSerializer.serialize();
        return mainSerializer;
    }

    protected DfSchemaXmlSerializer serializeTargetSchema() {
        final DataSource targetDs = prepareTargetDataSource();
        final DfSchemaXmlSerializer targetSerializer = createTargetSerializer(targetDs);
        targetSerializer.suppressUnifiedSchema(); // same reason as main schema
        targetSerializer.serialize();
        return targetSerializer;
    }

    protected DataSource prepareTargetDataSource() {
        final DfDataSourceHandler handler = new DfDataSourceHandler();
        handler.setDriver(getDatabaseProperties().getDatabaseDriver()); // inherit
        handler.setUrl(getDocumentProperties().getSchemaSyncCheckDatabaseUrl()); // may inherit
        final String user = getDocumentProperties().getSchemaSyncCheckDatabaseUser();
        if (Srl.is_Null_or_TrimmedEmpty(user)) { // just in case
            String msg = "The user for sync target schema was not found: " + user;
            throw new IllegalStateException(msg);
        }
        handler.setUser(user);
        handler.setPassword(getDocumentProperties().getSchemaSyncCheckDatabasePassword());
        handler.setConnectionProperties(getDatabaseProperties().getConnectionProperties()); // inherit
        handler.setAutoCommit(true);
        return new DfSimpleDataSource(handler);
    }

    protected void throwSchemaSyncCheckTragedyResultException() {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("The schema was not synchronized with another schema.");
        br.addItem("Advice");
        br.addElement("You can see the details at");
        br.addElement(" '" + getResultFilePath() + "'.");
        br.addElement("");
        br.addElement("'Previous' means the sync-check schema, defined at schemaSyncCheckMap property.");
        br.addElement("'Next' means the main schema, defined at databaseInfoMap.dfprop.");
        br.addElement("");
        br.addElement("e.g. Add Table: FOO_TABLE");
        br.addElement("create the table on the sync-check schema to synchronize with main schema.");
        final String msg = br.buildExceptionMessage();
        throw new DfSchemaSyncCheckTragedyResultException(msg);
    }

    // ===================================================================================
    //                                                                          Serializer
    //                                                                          ==========
    protected DfSchemaXmlSerializer createMainSerializer() {
        final UnifiedSchema mainSchema = getDatabaseProperties().getDatabaseSchema();
        return doCreateSerializer(_mainDs, mainSchema);
    }

    protected DfSchemaXmlSerializer createTargetSerializer(DataSource targetDs) {
        final UnifiedSchema targetSchema = getDocumentProperties().getSchemaSyncCheckDatabaseSchema();
        return doCreateSerializer(targetDs, targetSchema);
    }

    protected DfSchemaXmlSerializer doCreateSerializer(DataSource dataSource, UnifiedSchema unifiedSchema) {
        final DfSchemaXmlSerializer serializer = DfSchemaXmlSerializer.createAsManage(dataSource, unifiedSchema,
                getSchemaXml(), getDiffMapFile());
        serializer.enableCraftDiff(dataSource, unifiedSchema, getSchemaSyncCheckCraftMetaDir());
        return serializer;
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfDatabaseProperties getDatabaseProperties() {
        return getProperties().getDatabaseProperties();
    }

    protected DfDocumentProperties getDocumentProperties() {
        return getProperties().getDocumentProperties();
    }

    protected String getSchemaXml() {
        return getDocumentProperties().getSchemaSyncCheckSchemaXml();
    }

    protected String getDiffMapFile() {
        return getDocumentProperties().getSchemaSyncCheckDiffMapFile();
    }

    protected String getResultFilePath() {
        return getDocumentProperties().getSchemaSyncCheckResultFilePath();
    }

    protected String getSchemaSyncCheckCraftMetaDir() {
        return getDocumentProperties().getSchemaSyncCheckCraftMetaDir();
    }
}
