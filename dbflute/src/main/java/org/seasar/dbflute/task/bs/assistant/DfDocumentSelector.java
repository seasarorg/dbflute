/*
 * Copyright 2004-2012 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.task.bs.assistant;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.logic.doc.historyhtml.DfSchemaHistory;
import org.seasar.dbflute.logic.jdbc.schemadiff.DfSchemaDiff;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.properties.DfReplaceSchemaProperties;

/**
 * @author jflute
 */
public class DfDocumentSelector {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfDocumentSelector.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _schemaHtml;
    protected boolean _historyHtml;
    protected boolean _schemaSyncCheckResultHtml;
    protected boolean _alterCheckResultHtml;
    protected DocumentType _currentDocumentType;
    protected DfSchemaHistory _schemaHistory;

    // ===================================================================================
    //                                                               Derived Determination
    //                                                               =====================
    public boolean needsInitializeProperties() {
        return _schemaHtml || _historyHtml;
    }

    // ===================================================================================
    //                                                                    Current Document
    //                                                                    ================
    public boolean isCurrentHistoryHtml() {
        return _currentDocumentType != null && _currentDocumentType.equals(DocumentType.HistoryHtml);
    }

    public void markSchemaHtml() {
        _currentDocumentType = DocumentType.SchemaHtml;
    }

    public void markHistoryHtml() {
        _currentDocumentType = DocumentType.HistoryHtml;
    }

    public void markSchemaSyncCheckResultHtml() {
        _currentDocumentType = DocumentType.SchemaSyncCheckResultHtml;
    }

    public void markAlterCheckResultHtml() {
        _currentDocumentType = DocumentType.AlterCheckResultHtml;
    }

    public enum DocumentType {
        SchemaHtml, HistoryHtml, SchemaSyncCheckResultHtml, AlterCheckResultHtml
    }

    // ===================================================================================
    //                                                                      Schema History
    //                                                                      ==============
    public void loadSchemaHistoryAsCore() { // for HistoryHtml
        doLoadSchemaHistory(DfSchemaHistory.createAsCore());
    }

    public void loadSchemaHistoryAsSchemaSyncCheck() { // for SchemaSyncCheck
        doLoadSchemaHistory(DfSchemaHistory.createAsPlain(getSchemaSyncCheckDiffMapFile()));
    }

    public void loadSchemaHistoryAsAlterCheck() { // for AlterCheck
        doLoadSchemaHistory(DfSchemaHistory.createAsPlain(getAlterCheckDiffMapFile()));
    }

    protected void doLoadSchemaHistory(DfSchemaHistory schemaHistory) {
        _log.info("...Loading schema history");
        _schemaHistory = schemaHistory;
        _schemaHistory.loadHistory();
        if (existsSchemaHistory()) {
            _log.info(" -> found history: count=" + getSchemaDiffList().size());
        } else {
            _log.info(" -> no history");
        }
    }

    public boolean existsSchemaHistory() {
        return _schemaHistory.existsHistory();
    }

    public List<DfSchemaDiff> getSchemaDiffList() {
        return _schemaHistory.getSchemaDiffList();
    }

    // ===================================================================================
    //                                                                           File Name
    //                                                                           =========
    public String getSchemaHtmlFileName() {
        final String projectName = getProjectName();
        return getDocumentProperties().getSchemaHtmlFileName(projectName);
    }

    public String getHistoryHtmlFileName() {
        final String projectName = getProjectName();
        return getDocumentProperties().getHistoryHtmlFileName(projectName);
    }

    public String getSchemaSyncCheckResultFileName() {
        return getDocumentProperties().getSchemaSyncCheckResultFileName();
    }

    public String getAlterCheckResultFileName() {
        return getReplaceSchemaProperties().getMigrationAlterCheckResultFileName();
    }

    // ===================================================================================
    //                                                                          Properties
    //                                                                          ==========
    protected DfBuildProperties getProperties() {
        return DfBuildProperties.getInstance();
    }

    protected DfBasicProperties getBasicProperties() {
        return getProperties().getBasicProperties();
    }

    protected String getProjectName() {
        return getBasicProperties().getProjectName();
    }

    protected DfDocumentProperties getDocumentProperties() {
        return getProperties().getDocumentProperties();
    }

    protected String getSchemaSyncCheckDiffMapFile() {
        return getDocumentProperties().getSchemaSyncCheckDiffMapFile();
    }

    protected DfReplaceSchemaProperties getReplaceSchemaProperties() {
        return getProperties().getReplaceSchemaProperties();
    }

    protected String getAlterCheckDiffMapFile() {
        return getReplaceSchemaProperties().getMigrationAlterCheckDiffMapFile();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public boolean isSchemaHtml() {
        return _schemaHtml;
    }

    public DfDocumentSelector selectSchemaHtml() {
        _schemaHtml = true;
        return this;
    }

    public boolean isHistoryHtml() {
        return _historyHtml;
    }

    public DfDocumentSelector selectHistoryHtml() {
        _historyHtml = true;
        return this;
    }

    public boolean isSchemaSyncCheckResultHtml() {
        return _schemaSyncCheckResultHtml;
    }

    public DfDocumentSelector selectSchemaSyncCheckResultHtml() {
        _schemaSyncCheckResultHtml = true;
        return this;
    }

    public boolean isAlterCheckResultHtml() {
        return _alterCheckResultHtml;
    }

    public DfDocumentSelector selectAlterCheckResultHtml() {
        _alterCheckResultHtml = true;
        return this;
    }
}
