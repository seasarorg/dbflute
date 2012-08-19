package org.apache.torque.task;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.engine.database.model.Database;
import org.apache.velocity.anakia.Escape;
import org.apache.velocity.context.Context;
import org.seasar.dbflute.exception.DfRequiredPropertyNotFoundException;
import org.seasar.dbflute.exception.DfSchemaSyncCheckTragedyResultException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.logic.doc.lreverse.DfLReverseOutputHandler;
import org.seasar.dbflute.logic.doc.lreverse.DfLReverseProcess;
import org.seasar.dbflute.logic.doc.synccheck.DfSchemaSyncChecker;
import org.seasar.dbflute.logic.jdbc.schemaxml.DfSchemaXmlReader;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.task.DfDBFluteTaskStatus;
import org.seasar.dbflute.task.DfDBFluteTaskStatus.TaskType;
import org.seasar.dbflute.task.bs.DfAbstractDbMetaTexenTask;
import org.seasar.dbflute.util.Srl;

/**
 * The DBFlute task generating documentations, SchemaHTML, HistoryHTML and so on.
 * @author Modified by jflute
 */
public class TorqueDocumentationTask extends DfAbstractDbMetaTexenTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(TorqueDocumentationTask.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _varyingArg;

    // ===================================================================================
    //                                                                           Beginning
    //                                                                           =========
    @Override
    protected boolean begin() {
        {
            _log.info("+------------------------------------------+");
            _log.info("|                                          |");
            _log.info("|                   Doc                    |");
        }
        if (isLoadDataReverseOnly()) {
            _log.info("|            (LoadDataReverse)             |");
        } else if (isSchemaSyncCheckOnly()) {
            _log.info("|            (SchemaSyncCheck)             |");
        }
        {
            _log.info("|                                          |");
            _log.info("+------------------------------------------+");
        }
        DfDBFluteTaskStatus.getInstance().setTaskType(TaskType.Doc);
        return true;
    }

    // ===================================================================================
    //                                                                         Data Source
    //                                                                         ===========
    @Override
    protected boolean isUseDataSource() {
        // at old age, this is false, but after all, classification needs a connection 
        return true;
    }

    // ===================================================================================
    //                                                                          Schema XML
    //                                                                          ==========
    @Override
    protected DfSchemaXmlReader createSchemaXmlReader() {
        return createSchemaXmlReaderAsCoreToManage();
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected void doExecute() {
        if (processSubTask()) {
            return;
        }
        processSchemaHtml();
        // these processes are independent since 0.9.9.7B
        //processLoadDataReverse();
        //processSchemaSyncCheck();
        refreshResources();
    }

    protected boolean processSubTask() {
        if (isLoadDataReverseOnly()) {
            if (!isLoadDataReverseValid()) {
                throwLoadDataReversePropertyNotFoundException();
            }
            initializeSchemaData(); // needed
            processLoadDataReverse();
            return true;
        } else if (isSchemaSyncCheckOnly()) {
            if (!isSchemaSyncCheckValid()) {
                throwSchemaSyncCheckPropertyNotFoundException();
            }
            processSchemaSyncCheck();
            return true;
        }
        return false;
    }

    protected void processSchemaHtml() {
        _log.info("");
        _log.info("* * * * * * * * * * *");
        _log.info("*                   *");
        _log.info("*    Schema HTML    *");
        _log.info("*                   *");
        _log.info("* * * * * * * * * * *");
        _selector.selectSchemaHtml().selectHistoryHtml();
        fireVelocityProcess();
    }

    protected void processLoadDataReverse() {
        if (!isLoadDataReverseValid()) {
            return;
        }
        _log.info("");
        _log.info("* * * * * * * * * * *");
        _log.info("*                   *");
        _log.info("* Load Data Reverse *");
        _log.info("*                   *");
        _log.info("* * * * * * * * * * *");
        final Database database = _schemaData.getDatabase();
        outputLoadDataReverse(database);
        refreshResources();
    }

    protected void outputLoadDataReverse(Database database) {
        final DfLReverseOutputHandler handler = new DfLReverseOutputHandler(getDataSource());
        handler.setContainsCommonColumn(isLoadDataReverseContainsCommonColumn());
        handler.setManagedTableOnly(isLoadDataReverseManagedTableOnly());
        final Integer xlsLimit = getLoadDataReverseXlsLimit(); // if null, default limit
        if (xlsLimit != null) {
            handler.setXlsLimit(xlsLimit);
        }
        handler.setDelimiterDataDir(getLoadDataReverseDelimiterDataDir());
        // changes to TSV for compatibility of copy and paste to excel @since 0.9.8.3
        //handler.setDelimiterDataTypeCsv(true);
        final String xlsDataDir = getLoadDataReverseXlsDataDir();
        final String fileTitle = getLoadDataReverseFileTitle();
        final int limit = getLoadDataReverseRecordLimit(); // not null here
        final DfLReverseProcess process = new DfLReverseProcess(handler, xlsDataDir, fileTitle, limit);
        process.execute(database);
    }

    protected void throwLoadDataReversePropertyNotFoundException() {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Not found the property for LoadDataReverse.");
        br.addItem("Advice");
        br.addElement("You should set the property like this:");
        br.addElement("[documentDefinitionMap.dfprop]");
        br.addElement("  ; loadDataReverseMap = map:{");
        br.addElement("      ; recordLimit = -1");
        br.addElement("      ; isContainsCommonColumn = true");
        br.addElement("      ; isOutputToPlaySql = true");
        br.addElement("  }");
        final String msg = br.buildExceptionMessage();
        throw new DfRequiredPropertyNotFoundException(msg);
    }

    protected void processSchemaSyncCheck() {
        try {
            doProcessSchemaSyncCheck();
        } catch (DfSchemaSyncCheckTragedyResultException e) {
            refreshResources();
            throw e;
        }
    }

    protected void doProcessSchemaSyncCheck() {
        if (!isSchemaSyncCheckValid()) {
            return;
        }
        _log.info("");
        _log.info("* * * * * * * * * * *");
        _log.info("*                   *");
        _log.info("* Schema Sync Check *");
        _log.info("*                   *");
        _log.info("* * * * * * * * * * *");
        final DfSchemaSyncChecker checker = new DfSchemaSyncChecker(getDataSource());
        try {
            checker.checkSync();
        } catch (DfSchemaSyncCheckTragedyResultException e) {
            _selector.selectSyncCheckDiffHtml();
            fireVelocityProcess();
            throw e;
        }
    }

    protected void throwSchemaSyncCheckPropertyNotFoundException() {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Not found the property for SchemaSyncCheck.");
        br.addItem("Advice");
        br.addElement("You should set the property like this:");
        br.addElement("[documentDefinitionMap.dfprop]");
        br.addElement("  ; schemaSyncCheckMap = map:{");
        br.addElement("      ; url = jdbc:...");
        br.addElement("      ; schema = EXAMPLEDB");
        br.addElement("      ; user = exampuser");
        br.addElement("      ; password = exampword");
        br.addElement("  }");
        final String msg = br.buildExceptionMessage();
        throw new DfRequiredPropertyNotFoundException(msg);
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfDocumentProperties getDocumentProperties() {
        return getProperties().getDocumentProperties();
    }

    protected boolean isLoadDataReverseValid() {
        return getDocumentProperties().isLoadDataReverseValid();
    }

    protected Integer getLoadDataReverseRecordLimit() {
        return getDocumentProperties().getLoadDataReverseRecordLimit();
    }

    protected boolean isLoadDataReverseContainsCommonColumn() {
        return getDocumentProperties().isLoadDataReverseContainsCommonColumn();
    }

    protected boolean isLoadDataReverseManagedTableOnly() {
        return getDocumentProperties().isLoadDataReverseManagedTableOnly();
    }

    protected Integer getLoadDataReverseXlsLimit() {
        return getDocumentProperties().getLoadDataReverseXlsLimit();
    }

    protected String getLoadDataReverseXlsDataDir() {
        return getDocumentProperties().getLoadDataReverseXlsDataDir();
    }

    protected String getLoadDataReverseDelimiterDataDir() {
        return getDocumentProperties().getLoadDataReverseDelimiterDataDir();
    }

    protected String getLoadDataReverseFileTitle() {
        return getDocumentProperties().getLoadDataReverseFileTitle();
    }

    protected boolean isLoadDataReverseLoadDataReverse() {
        return getDocumentProperties().isLoadDataReverseOutputToPlaySql();
    }

    protected boolean isSchemaSyncCheckValid() {
        return getDocumentProperties().isSchemaSyncCheckValid();
    }

    // ===================================================================================
    //                                                                      Varying Option
    //                                                                      ==============
    protected boolean isLoadDataReverseOnly() {
        return _varyingArg != null && _varyingArg.equals("load-data-reverse");
    }

    protected boolean isSchemaSyncCheckOnly() {
        return _varyingArg != null && _varyingArg.equals("schema-sync-check");
    }

    // ===================================================================================
    //                                                                  Prepare Generation
    //                                                                  ==================
    @Override
    public Context initControlContext() throws Exception {
        final Context context = super.initControlContext();
        context.put("escape", new Escape());
        context.put("selector", _selector);
        return context;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setVaryingArg(String varyingArg) {
        if (Srl.is_Null_or_TrimmedEmpty(varyingArg)) {
            return;
        }
        _varyingArg = varyingArg;
    }
}
