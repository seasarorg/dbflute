package org.seasar.dbflute.logic.jdbc.metadata.info;

import java.util.ArrayList;
import java.util.List;

import org.apache.torque.engine.database.model.UnifiedSchema;
import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.util.Srl;

/**
 * @author jflute
 */
public class DfProcedureMetaInfo {

    protected String procedureCatalog;
    protected UnifiedSchema procedureSchema;
    protected String procedureName;
    protected String procedureSqlName;
    protected String procedureDisplayName;
    protected String catalogSchemaProcedureName;
    protected String schemaProcedureName;
    protected DfProcedureType procedureType;
    protected String procedureUniqueName;
    protected String procedureComment;
    protected boolean procedureSynonym;

    protected List<DfProcedureColumnMetaInfo> procedureColumnList = new ArrayList<DfProcedureColumnMetaInfo>();
    protected List<DfProcedureNotParamResultMetaInfo> notParamResultList = new ArrayList<DfProcedureNotParamResultMetaInfo>();

    public String getProcedureDisplayNameForSchemaHtml() {
        final StringBuilder sb = new StringBuilder();
        sb.append(procedureSqlName);
        final String typeDisp = procedureType.alias() + (procedureSynonym ? ", Synonym" : "");
        sb.append(" <span class=\"type\">(").append(typeDisp).append(")</span>");
        return sb.toString();
    }

    public boolean hasProcedureComment() {
        return Srl.is_NotNull_and_NotTrimmedEmpty(procedureComment);
    }

    public String getProcedureCommentForSchemaHtml() {
        final DfDocumentProperties prop = DfBuildProperties.getInstance().getDocumentProperties();
        String comment = procedureComment;
        comment = prop.resolvePreTextForSchemaHtml(comment);
        return comment;
    }

    @Override
    public String toString() {
        return "{" + procedureSqlName + ", " + procedureType + ", " + procedureComment + ", " + procedureColumnList
                + ", notParamResult=" + notParamResultList.size() + "}";
    }

    public enum DfProcedureType {
        procedureResultUnknown("ResultUnknown"), procedureNoResult("NoResult"), procedureReturnsResult("ReturnsResult");
        private String _alias;

        private DfProcedureType(String alias) {
            _alias = alias;
        }

        public String alias() {
            return _alias;
        }
    }

    public String getProcedureCatalog() {
        return procedureCatalog;
    }

    public void setProcedureCatalog(String procedureCatalog) {
        this.procedureCatalog = procedureCatalog;
    }

    public UnifiedSchema getProcedureSchema() {
        return procedureSchema;
    }

    public void setProcedureSchema(UnifiedSchema procedureSchema) {
        this.procedureSchema = procedureSchema;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public String getProcedureSqlName() {
        return procedureSqlName;
    }

    public void setProcedureSqlName(String procedureSqlName) {
        this.procedureSqlName = procedureSqlName;
    }

    public String getProcedureDisplayName() {
        return procedureDisplayName;
    }

    public void setProcedureDisplayName(String procedureDisplayName) {
        this.procedureDisplayName = procedureDisplayName;
    }

    public String getCatalogSchemaProcedureName() {
        return catalogSchemaProcedureName;
    }

    public void setCatalogSchemaProcedureName(String catalogSchemaProcedureName) {
        this.catalogSchemaProcedureName = catalogSchemaProcedureName;
    }

    public String getSchemaProcedureName() {
        return schemaProcedureName;
    }

    public void setSchemaProcedureName(String schemaProcedureName) {
        this.schemaProcedureName = schemaProcedureName;
    }

    public String getProcedureUniqueName() {
        return procedureUniqueName;
    }

    public void setProcedureUniqueName(String procedureUniqueName) {
        this.procedureUniqueName = procedureUniqueName;
    }

    public DfProcedureType getProcedureType() {
        return procedureType;
    }

    public void setProcedureType(DfProcedureType procedureType) {
        this.procedureType = procedureType;
    }

    public String getProcedureComment() {
        return procedureComment;
    }

    public void setProcedureComment(String procedureComment) {
        this.procedureComment = procedureComment;
    }

    public boolean isProcedureSynonym() {
        return procedureSynonym;
    }

    public void setProcedureSynonym(boolean procedureSynonym) {
        this.procedureSynonym = procedureSynonym;
    }

    public List<DfProcedureColumnMetaInfo> getProcedureColumnList() {
        return procedureColumnList;
    }

    public void addProcedureColumnMetaInfo(DfProcedureColumnMetaInfo procedureColumn) {
        procedureColumnList.add(procedureColumn);
    }

    public List<DfProcedureNotParamResultMetaInfo> getNotParamResultList() {
        return notParamResultList;
    }

    public void addNotParamResult(DfProcedureNotParamResultMetaInfo notParamResult) {
        this.notParamResultList.add(notParamResult);
    }
}
