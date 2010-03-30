package org.seasar.dbflute.logic.jdbc.metadata.info;

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
    protected String procedureFullName;
    protected String procedureSqlName;
    protected DfProcedureType procedureType;
    protected String procedureUniqueName;
    protected String procedureComment;
    protected boolean procedureSynonym;

    protected List<DfProcedureColumnMetaInfo> procedureColumnMetaInfoList = new ArrayList<DfProcedureColumnMetaInfo>();
    protected List<DfProcedureClosetResultMetaInfo> closetResultMetaInfoList = new ArrayList<DfProcedureClosetResultMetaInfo>();

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
        final String typeDisp = procedureType.alias() + (procedureSynonym ? ", Synonym" : "");
        sb.append(" <span class=\"type\">(").append(typeDisp).append(")</span>");
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
        return "{" + procedureFullName + ", " + procedureType + ", " + procedureComment + ", "
                + procedureColumnMetaInfoList + ", closet=" + closetResultMetaInfoList.size() + "}";
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

    public String getProcedureFullName() {
        return procedureFullName;
    }

    public void setProcedureFullName(String procedureFullName) {
        this.procedureFullName = procedureFullName;
    }

    public String getProcedureSqlName() {
        return procedureSqlName;
    }

    public void setProcedureSqlName(String procedureSqlName) {
        this.procedureSqlName = procedureSqlName;
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

    public List<DfProcedureColumnMetaInfo> getProcedureColumnMetaInfoList() {
        return procedureColumnMetaInfoList;
    }

    public void addProcedureColumnMetaInfo(DfProcedureColumnMetaInfo procedureColumnMetaInfo) {
        procedureColumnMetaInfoList.add(procedureColumnMetaInfo);
    }

    public List<DfProcedureClosetResultMetaInfo> getClosetResultMetaInfoList() {
        return closetResultMetaInfoList;
    }

    public void addClosetResultMetaInfo(DfProcedureClosetResultMetaInfo closetResultMetaInfo) {
        this.closetResultMetaInfoList.add(closetResultMetaInfo);
    }
}
