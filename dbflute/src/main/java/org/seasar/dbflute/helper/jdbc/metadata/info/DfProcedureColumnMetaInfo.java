package org.seasar.dbflute.helper.jdbc.metadata.info;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.util.DfStringUtil;

public class DfProcedureColumnMetaInfo {

    protected String columnName;
    protected int jdbcType;
    protected String dbTypeName;
    protected Integer columnSize;
    protected Integer decimalDigits;
    protected String columnComment;
    protected DfProcedureColumnType procedureColumnType;

    public String getColumnDisplayNameForSchemaHtml() {
        final StringBuilder sb = new StringBuilder();
        if (DfStringUtil.isNotNullAndNotTrimmedEmpty(columnName)) {
            sb.append(columnName);
        } else {
            sb.append("arg");
        }
        sb.append(" ").append(dbTypeName);
        if (columnSize != null) {
            sb.append("(").append(columnSize);
            if (decimalDigits != null) {
                sb.append(", ").append(decimalDigits);
            }
            sb.append(")");
        }
        sb.append(" (").append(procedureColumnType).append(")");
        return sb.toString();
    }

    public boolean hasColumnComment() {
        return DfStringUtil.isNotNullAndNotTrimmedEmpty(getColumnComment());
    }

    public String getColumnCommentForSchemaHtml() {
        final DfDocumentProperties prop = DfBuildProperties.getInstance().getDocumentProperties();
        String comment = columnComment;
        comment = prop.resolvePreTextForSchemaHtml(comment);
        return comment;
    }

    @Override
    public String toString() {
        return "{" + columnName + ", " + procedureColumnType + ", " + jdbcType + ", " + dbTypeName + "(" + columnSize
                + ", " + decimalDigits + ")" + columnComment + "}";
    }

    public enum DfProcedureColumnType {
        procedureColumnUnknown, procedureColumnIn, procedureColumnInOut, procedureColumnOut, procedureColumnReturn, procedureColumnResult
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public DfProcedureColumnType getProcedureColumnType() {
        return procedureColumnType;
    }

    public void setProcedureColumnType(DfProcedureColumnType procedureColumnType) {
        this.procedureColumnType = procedureColumnType;
    }

    public int getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(int jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getDbTypeName() {
        return dbTypeName;
    }

    public void setDbTypeName(String dbTypeName) {
        this.dbTypeName = dbTypeName;
    }

    public Integer getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(Integer columnSize) {
        this.columnSize = columnSize;
    }

    public Integer getDecimalDigits() {
        return decimalDigits;
    }

    public void setDecimalDigits(Integer decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }
}
