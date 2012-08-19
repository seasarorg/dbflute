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
        clearPreviousResource();
        serializeMainSchema();
        final DfSchemaXmlSerializer targetSerializer = serializeTargetSchema();
        _log.info("...Checking the schema synchronized");
        final DfSchemaDiff schemaDiff = targetSerializer.getSchemaDiff();
        if (schemaDiff.hasDiff()) {
            _log.info(" -> the schema has differences");
            throwSchemaSyncCheckTragedyResultException();
        } else { // synchronized
            _log.info(" -> the schema is synchronized");
            clearTemporaryResource();
        }
    }

    protected void clearPreviousResource() {
        final File schemaXml = new File(getSchemaXml());
        if (schemaXml.exists()) {
            schemaXml.delete();
        }
        final File historyFile = new File(getDiffMapFile());
        if (historyFile.exists()) {
            historyFile.delete();
        }
    }

    protected void clearTemporaryResource() {
        final File schemaXml = new File(getSchemaXml());
        if (schemaXml.exists()) {
            schemaXml.delete();
        }
    }

    protected void serializeMainSchema() {
        final DfSchemaXmlSerializer mainSerializer = createMainSerializer();
        mainSerializer.serialize();
    }

    protected DfSchemaXmlSerializer serializeTargetSchema() {
        final DataSource targetDs = prepareTargetDataSource();
        final DfSchemaXmlSerializer targetSerializer = createTargetSerializer(targetDs);
        targetSerializer.suppressUnifiedSchema(); // because of comparison with other schema
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
        br.addElement("You can confirm the result at 'output/doc/" + getDiffHtmlFileName() + "'.");
        br.addElement("'Previous' means the main schema, defined at databaseInfoMap.dfprop.");
        br.addElement("'Next' means the sync-check target schema, defined at schemaSyncCheckMap property.");
        final String msg = br.buildExceptionMessage();
        throw new DfSchemaSyncCheckTragedyResultException(msg);
    }

    // ===================================================================================
    //                                                                          Serializer
    //                                                                          ==========
    protected DfSchemaXmlSerializer createMainSerializer() {
        final UnifiedSchema mainSchema = getDatabaseProperties().getDatabaseSchema();
        return DfSchemaXmlSerializer.createAsManage(_mainDs, mainSchema, getSchemaXml(), getDiffMapFile());
    }

    protected DfSchemaXmlSerializer createTargetSerializer(DataSource targetDs) {
        final UnifiedSchema schema = getDocumentProperties().getSchemaSyncCheckDatabaseSchema();
        return DfSchemaXmlSerializer.createAsManage(targetDs, schema, getSchemaXml(), getDiffMapFile());
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

    protected String getDiffHtmlFileName() {
        return getDocumentProperties().getSchemaSyncCheckDiffHtmlFileName();
    }
}
