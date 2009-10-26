package org.seasar.dbflute.helper.jdbc.metadata.info;

import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.properties.DfBasicProperties;
import org.seasar.dbflute.properties.DfDatabaseProperties;
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
        final DfBasicProperties basicProp = DfBuildProperties.getInstance().getBasicProperties();
        final DfDatabaseProperties databaseProp = DfBuildProperties.getInstance().getDatabaseProperties();
        if (basicProp.isDatabaseOracle()) {
            if (databaseProp.hasAdditionalSchema() && DfStringUtil.isNotNullAndNotTrimmedEmpty(procedureSchema)) {
                sb.append(procedureSchema).append(".");
            }
            if (DfStringUtil.isNotNullAndNotTrimmedEmpty(procedureCatalog)) {
                sb.append(procedureCatalog).append(".");
            }
        } else {
            if (DfStringUtil.isNotNullAndNotTrimmedEmpty(procedureCatalog)) {
                sb.append(procedureCatalog).append(".");
            }
            if (databaseProp.hasAdditionalSchema() && DfStringUtil.isNotNullAndNotTrimmedEmpty(procedureSchema)) {
                sb.append(procedureSchema).append(".");
            }
        }
        sb.append(procedureName);
        sb.append(" <span class=\"type\">(").append(procedureType.alias()).append(")</span>");
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
