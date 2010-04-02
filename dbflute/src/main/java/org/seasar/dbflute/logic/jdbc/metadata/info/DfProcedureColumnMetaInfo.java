package org.seasar.dbflute.logic.jdbc.metadata.info;

import java.sql.Types;
import java.util.Map;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.logic.jdbc.handler.DfColumnHandler;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.DfStringUtil;

public class DfProcedureColumnMetaInfo {

    protected String columnName;
    protected int jdbcType;
    protected String dbTypeName;
    protected Integer columnSize;
    protected Integer decimalDigits;
    protected String columnComment;
    protected DfProcedureColumnType procedureColumnType;
    protected Map<String, DfColumnMetaInfo> columnMetaInfoMap = DfCollectionUtil.emptyMap(); // if result set

    public String getColumnDisplayNameForSchemaHtml() {
        final StringBuilder sb = new StringBuilder();
        if (DfStringUtil.isNotNullAndNotTrimmedEmpty(columnName)) {
            sb.append(columnName);
        } else {
            if (DfProcedureColumnType.procedureColumnReturn.equals(procedureColumnType)) {
                sb.append("(result)");
            } else {
                sb.append("(arg)");
            }
        }
        sb.append(" - ").append(dbTypeName);
        if (DfColumnHandler.isColumnSizeValid(columnSize)) {
            sb.append("(").append(columnSize);
            if (DfColumnHandler.isDecimalDigitsValid(decimalDigits)) {
                sb.append(", ").append(decimalDigits);
            }
            sb.append(")");
        }
        sb.append(" <span class=\"type\">(").append(procedureColumnType.alias()).append(")</span>");
        return sb.toString();
    }

    public boolean hasColumnMetaInfo() {
        return !columnMetaInfoMap.isEmpty();
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

    public boolean isPostgreSQLCursor(DfProcedureColumnMetaInfo column) {
        final String key = "cursor";
        final int jdbcType = column.getJdbcType();
        final String dbTypeName = column.getDbTypeName();
        return jdbcType == Types.OTHER && dbTypeName != null && dbTypeName.toLowerCase().contains(key);
    }

    public boolean isOracleCursor(DfProcedureColumnMetaInfo column) {
        final String key = "cursor";
        final int jdbcType = column.getJdbcType();
        final String dbTypeName = column.getDbTypeName();
        return jdbcType == Types.OTHER && dbTypeName != null && dbTypeName.toLowerCase().contains(key);
    }

    @Override
    public String toString() {
        return "{" + columnName + ", " + procedureColumnType + ", " + jdbcType + ", " + dbTypeName + "(" + columnSize
                + ", " + decimalDigits + ")" + columnComment + "}";
    }

    public enum DfProcedureColumnType {
        procedureColumnUnknown("Unknown"), procedureColumnIn("In"), procedureColumnInOut("InOut"), procedureColumnOut(
                "Out"), procedureColumnReturn("Return"), procedureColumnResult("Result");
        private final String _alias;

        private DfProcedureColumnType(String alias) {
            _alias = alias;
        }

        public String alias() {
            return _alias;
        }
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

    public Map<String, DfColumnMetaInfo> getColumnMetaInfoMap() {
        return columnMetaInfoMap;
    }

    public void setColumnMetaInfoMap(Map<String, DfColumnMetaInfo> columnMetaInfoMap) {
        this.columnMetaInfoMap = columnMetaInfoMap;
    }
}
