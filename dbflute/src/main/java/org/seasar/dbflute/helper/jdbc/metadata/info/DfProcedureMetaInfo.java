package org.seasar.dbflute.helper.jdbc.metadata.info;

import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.util.DfStringUtil;

/**
 * @author jflute
 */
public class DfProcedureMetaInfo {

    protected String procedureCatalog;
    protected String procedureSchema;
    protected String procedureName;
    protected String procedureComment;
    protected DfProcedureType procedureType;
    protected List<DfProcedureColumnMetaInfo> procedureColumnMetaInfoList = new ArrayList<DfProcedureColumnMetaInfo>();

    public String getProcedureDisplayNameForSchemaHtml() {
        final StringBuilder sb = new StringBuilder();
        if (DfStringUtil.isNotNullAndNotTrimmedEmpty(procedureCatalog)) {
            sb.append(procedureCatalog).append(".");
        }
        if (DfStringUtil.isNotNullAndNotTrimmedEmpty(procedureSchema)) {
            sb.append(procedureSchema).append(".");
        }
        sb.append(procedureName);
        sb.append(" (").append(procedureType).append(")");
        return sb.toString();
    }

    public boolean hasProcedureComment() {
        return DfStringUtil.isNotNullAndNotTrimmedEmpty(procedureComment);
    }

    public String getProcedureCommentForSchemaHtml() {
        final DfDocumentProperties prop = DfBuildProperties.getInstance().getDocumentProperties();
        String comment = procedureComment;
        comment = prop.resolvePreTextForSchemaHtml(comment);
        return comment;
    }

    @Override
    public String toString() {
        return "{" + procedureName + ", " + procedureType + ", " + procedureComment + ", "
                + procedureColumnMetaInfoList + "}";
    }

    public enum DfProcedureType {
        procedureResultUnknown, procedureNoResult, procedureReturnsResult
    }

    public String getProcedureCatalog() {
        return procedureCatalog;
    }

    public void setProcedureCatalog(String procedureCatalog) {
        this.procedureCatalog = procedureCatalog;
    }

    public String getProcedureSchema() {
        return procedureSchema;
    }

    public void setProcedureSchema(String procedureSchema) {
        this.procedureSchema = procedureSchema;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
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

    public List<DfProcedureColumnMetaInfo> getProcedureColumnMetaInfoList() {
        return procedureColumnMetaInfoList;
    }

    public void addProcedureColumnMetaInfo(DfProcedureColumnMetaInfo procedureColumnMetaInfo) {
        procedureColumnMetaInfoList.add(procedureColumnMetaInfo);
    }
}
