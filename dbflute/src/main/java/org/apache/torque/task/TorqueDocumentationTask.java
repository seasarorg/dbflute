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
import org.seasar.dbflute.exception.DfSchemaSyncCheckTragedyResultException;
import org.seasar.dbflute.logic.doc.lreverse.DfLReverseOutputHandler;
import org.seasar.dbflute.logic.doc.lreverse.DfLReverseProcess;
import org.seasar.dbflute.logic.doc.synccheck.DfSchemaSyncChecker;
import org.seasar.dbflute.logic.jdbc.schemaxml.DfSchemaXmlReader;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.task.bs.DfAbstractDbMetaTexenTask;

/**
 * The DBFlute task generating documentations, SchemaHTML, HistoryHTML and DataXlsTemplate.
 * @author Modified by jflute
 */
public class TorqueDocumentationTask extends DfAbstractDbMetaTexenTask {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(TorqueDocumentationTask.class);

    // ===================================================================================
    //                                                                           Beginning
    //                                                                           =========
    @Override
    protected void begin() {
        _log.info("+------------------------------------------+");
        _log.info("|                                          |");
        _log.info("|                   Doc                    |");
        _log.info("|                                          |");
        _log.info("+------------------------------------------+");
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
        processSchemaHtml();

        if (isLoadDataReverseValid()) {
            processLoadDataReverse();
        }

        if (isSchemaSyncCheckValid()) {
            try {
                processSchemaSyncCheck();
            } catch (DfSchemaSyncCheckTragedyResultException e) {
                refreshResources();
                throw e;
            }
        }

        refreshResources();
    }

    protected void processSchemaHtml() {
        _log.info("");
        _log.info("* * * * * * * * * * *");
        _log.info("*                   *");
        _log.info("*    Schema HTML    *");
        _log.info("*                   *");
        _log.info("* * * * * * * * * * *");
        super.doExecute();
        _log.info("");
    }

    protected void processLoadDataReverse() {
        _log.info("* * * * * * * * * * *");
        _log.info("*                   *");
        _log.info("* Load Data Reverse *");
        _log.info("*                   *");
        _log.info("* * * * * * * * * * *");
        final Database database = _schemaData.getDatabase();
        _log.info("...Outputting load data: tables=" + database.getTableList().size());
        outputLoadDataReverse(database);
        _log.info("");
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

    protected void processSchemaSyncCheck() {
        _log.info("* * * * * * * * * * *");
        _log.info("*                   *");
        _log.info("* Schema Sync Check *");
        _log.info("*                   *");
        _log.info("* * * * * * * * * * *");
        final DfSchemaSyncChecker checker = new DfSchemaSyncChecker(getDataSource());
        checker.checkSync();
        _log.info("");
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
    //                                                                       Task Override
    //                                                                       =============
    @Override
    public Context initControlContext() throws Exception {
        super.initControlContext();
        _context.put("escape", new Escape());
        return _context;
    }
}
