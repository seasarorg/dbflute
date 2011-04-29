package org.seasar.dbflute.logic.replaceschema.allmain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;
import org.seasar.dbflute.properties.DfLittleAdjustmentProperties;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;
import org.seasar.dbflute.properties.facade.DfDatabaseTypeFacadeProp;
import org.seasar.dbflute.properties.facade.DfLanguageTypeFacadeProp;

/**
 * @author jflute
 * @since 0.9.8.3 (2011/04/29 Friday)
 */
public class DfAbstractReplaceSchemaMain {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String CREATE_SCHEMA_LOG_PATH = "./log/create-schema.log";
    protected static final String LOAD_DATA_LOG_PATH = "./log/load-data.log";

    // ===================================================================================
    //                                                                         Result Dump
    //                                                                         ===========
    protected void dumpProcessResult(File dumpFile, String resultMessage, boolean failure, String detailMessage) {
        if (dumpFile.exists()) {
            boolean deleted = dumpFile.delete();
            if (!deleted) {
                return; // skip to dump!
            }
        }
        if (resultMessage == null || resultMessage.trim().length() == 0) {
            return; // nothing to dump!
        }
        BufferedWriter bw = null;
        try {
            final StringBuilder contentsSb = new StringBuilder();
            contentsSb.append(resultMessage).append(ln()).append(failure);
            if (detailMessage != null && detailMessage.trim().length() > 0) {
                contentsSb.append(ln()).append(detailMessage);
            }
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dumpFile), "UTF-8"));
            bw.write(contentsSb.toString());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    // ===================================================================================
    //                                                                      Various Common
    //                                                                      ==============
    protected String resolveTerminater4Tool() {
        return getDatabaseTypeFacadeProp().isDatabaseOracle() ? "/" : null;
    }

    protected boolean isDbCommentLineForIrregularPattern(String line) {
        // for irregular pattern
        line = line.trim().toLowerCase();
        if (getDatabaseTypeFacadeProp().isDatabaseMySQL()) {
            if (line.contains("comment='") || line.contains("comment = '") || line.contains(" comment '")) {
                return true;
            }
        }
        if (getDatabaseTypeFacadeProp().isDatabaseSQLServer()) {
            if (line.startsWith("exec sys.sp_addextendedproperty @name=n'ms_description'")) {
                return true;
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfReplaceSchemaProperties getReplaceSchemaProperties() {
        return DfBuildProperties.getInstance().getReplaceSchemaProperties();
    }

    protected DfBasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }

    protected DfDatabaseTypeFacadeProp getDatabaseTypeFacadeProp() {
        return getBasicProperties().getDatabaseTypeFacadeProp();
    }

    protected DfLanguageTypeFacadeProp getLanguageTypeFacadeProp() {
        return getBasicProperties().getLanguageTypeFacadeProp();
    }

    protected DfDatabaseProperties getDatabaseProperties() {
        return getProperties().getDatabaseProperties();
    }

    protected DfLittleAdjustmentProperties getLittleAdjustmentProperties() {
        return getProperties().getLittleAdjustmentProperties();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return "\n";
    }
}
