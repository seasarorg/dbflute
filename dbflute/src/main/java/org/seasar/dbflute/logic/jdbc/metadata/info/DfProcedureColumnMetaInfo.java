package org.seasar.dbflute.logic.jdbc.metadata.info;

import java.util.Map;

import org.seasar.dbflute.DfBuildProperties;
import org.seasar.dbflute.logic.jdbc.handler.DfColumnHandler;
import org.seasar.dbflute.properties.DfDocumentProperties;
import org.seasar.dbflute.util.DfCollectionUtil;
import org.seasar.dbflute.util.Srl;

public class DfProcedureColumnMetaInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _columnName;
    protected int _jdbcDefType;
    protected String _dbTypeName;
    protected Integer _columnSize;
    protected Integer _decimalDigits;
    protected Integer _overloadNo;
    protected String _columnComment;
    protected DfProcedureColumnType _procedureColumnType;
    protected Map<String, DfColumnMetaInfo> _resultSetColumnInfoMap = DfCollectionUtil.emptyMap(); // as default
    protected DfTypeArrayInfo _typeArrayInfo;

    protected final DfColumnHandler _columnHandler = new DfColumnHandler(); // for type determination

    // ===================================================================================
    //                                                                Status Determination
    //                                                                ====================
    public boolean hasResultSetColumnInfo() {
        return !_resultSetColumnInfoMap.isEmpty();
    }

    public boolean hasColumnComment() {
        return Srl.is_NotNull_and_NotTrimmedEmpty(getColumnComment());
    }

    public boolean hasTypeArrayTypeName() {
        return _typeArrayInfo != null && _typeArrayInfo.getTypeName() != null;
    }

    public boolean hasTypeArrayElementType() {
        return _typeArrayInfo != null && _typeArrayInfo.getElementType() != null;
    }

    // ===================================================================================
    //                                                                             Display
    //                                                                             =======
    public String getColumnDisplayName() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getColumnNameDisp());
        sb.append(": ").append(_dbTypeName);
        sb.append(getColumnSizeDisp());
        sb.append(" as ").append(_procedureColumnType.alias());
        return sb.toString();
    }

    public String getColumnDisplayNameForSchemaHtml() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getColumnNameDisp());
        sb.append(" - ").append(_dbTypeName);
        sb.append(getColumnSizeDisp());
        sb.append(" <span class=\"type\">(").append(_procedureColumnType.alias()).append(")</span>");
        return sb.toString();
    }

    public String getColumnNameDisp() {
        if (Srl.is_NotNull_and_NotTrimmedEmpty(_columnName)) {
            return _columnName;
        } else {
            if (DfProcedureColumnType.procedureColumnReturn.equals(_procedureColumnType)) {
                return "(return)";
            } else {
                return "(arg)";
            }
        }
    }

    public String getColumnSizeDisp() {
        final StringBuilder sb = new StringBuilder();
        if (DfColumnHandler.isColumnSizeValid(_columnSize)) {
            sb.append("(").append(_columnSize);
            if (DfColumnHandler.isDecimalDigitsValid(_decimalDigits)) {
                sb.append(", ").append(_decimalDigits);
            }
            sb.append(")");
        }
        return sb.toString();
    }

    public String getColumnDefinitionLineDisp() {
        final StringBuilder sb = new StringBuilder();
        sb.append(_dbTypeName);
        if (DfColumnHandler.isColumnSizeValid(_columnSize)) {
            sb.append("(").append(_columnSize);
            if (DfColumnHandler.isDecimalDigitsValid(_decimalDigits)) {
                sb.append(", ").append(_decimalDigits);
            }
            sb.append(")");
        }
        sb.append(" as ").append(_procedureColumnType.alias());
        return sb.toString();
    }

    public String getColumnCommentForSchemaHtml() {
        final DfDocumentProperties prop = DfBuildProperties.getInstance().getDocumentProperties();
        String comment = _columnComment;
        comment = prop.resolvePreTextForSchemaHtml(comment);
        return comment;
    }

    // ===================================================================================
    //                                                                  Type Determination
    //                                                                  ==================
    public boolean isConceptTypeStringClob() {
        final String dbTypeName = getDbTypeName();
        return _columnHandler.isConceptTypeStringClob(dbTypeName);
    }

    public boolean isConceptTypeBytesOid() {
        final String dbTypeName = getDbTypeName();
        return _columnHandler.isConceptTypeBytesOid(dbTypeName);
    }

    public boolean isConceptTypeFixedLengthString() {
        final String dbTypeName = getDbTypeName();
        return _columnHandler.isConceptTypeFixedLengthString(dbTypeName);
    }

    public boolean isConceptTypeObjectBindingBigDecimal() {
        final String dbTypeName = getDbTypeName();
        return _columnHandler.isConceptTypeObjectBindingBigDecimal(dbTypeName);
    }

    public boolean isPostgreSQLUuid() {
        final String dbTypeName = getDbTypeName();
        return _columnHandler.isPostgreSQLUuid(dbTypeName);
    }

    public boolean isPostgreSQLOid() {
        final String dbTypeName = getDbTypeName();
        return _columnHandler.isPostgreSQLOid(dbTypeName);
    }

    public boolean isPostgreSQLCursor() {
        final String dbTypeName = getDbTypeName();
        return _columnHandler.isPostgreSQLCursor(dbTypeName);
    }

    public boolean isOracleNCharOrNVarchar() {
        final String dbTypeName = getDbTypeName();
        return _columnHandler.isOracleNCharOrNVarchar(dbTypeName);
    }

    public boolean isOracleNumber() {
        final String dbTypeName = getDbTypeName();
        return _columnHandler.isOracleNumber(dbTypeName);
    }

    public boolean isOracleCursor() {
        final String dbTypeName = getDbTypeName();
        return _columnHandler.isOracleCursor(dbTypeName);
    }

    public boolean isOracleTreatedAsArray() {
        final String dbTypeName = getDbTypeName();
        return _columnHandler.isOracleTreatedAsArray(dbTypeName);
    }

    public boolean isSQLServerUniqueIdentifier() {
        final String dbTypeName = getDbTypeName();
        return _columnHandler.isSQLServerUniqueIdentifier(dbTypeName);
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

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "{" + _columnName + ", " + _procedureColumnType + ", " + _jdbcDefType + ", " + _dbTypeName + "("
                + _columnSize + ", " + _decimalDigits + ")" + _columnComment + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getColumnName() {
        return _columnName;
    }

    public void setColumnName(String columnName) {
        this._columnName = columnName;
    }

    public DfProcedureColumnType getProcedureColumnType() {
        return _procedureColumnType;
    }

    public void setProcedureColumnType(DfProcedureColumnType procedureColumnType) {
        this._procedureColumnType = procedureColumnType;
    }

    public int getJdbcDefType() {
        return _jdbcDefType;
    }

    public void setJdbcDefType(int jdbcDefType) {
        this._jdbcDefType = jdbcDefType;
    }

    public String getDbTypeName() {
        return _dbTypeName;
    }

    public void setDbTypeName(String dbTypeName) {
        this._dbTypeName = dbTypeName;
    }

    public Integer getColumnSize() {
        return _columnSize;
    }

    public void setColumnSize(Integer columnSize) {
        this._columnSize = columnSize;
    }

    public Integer getDecimalDigits() {
        return _decimalDigits;
    }

    public void setDecimalDigits(Integer decimalDigits) {
        this._decimalDigits = decimalDigits;
    }

    public Integer getOverloadNo() {
        return _overloadNo;
    }

    public void setOverloadNo(Integer overloadNo) {
        this._overloadNo = overloadNo;
    }

    public String getColumnComment() {
        return _columnComment;
    }

    public void setColumnComment(String columnComment) {
        this._columnComment = columnComment;
    }

    public Map<String, DfColumnMetaInfo> getResultSetColumnMetaInfoMap() {
        return _resultSetColumnInfoMap;
    }

    public void setResultSetColumnInfoMap(Map<String, DfColumnMetaInfo> resultSetColumnInfoMap) {
        this._resultSetColumnInfoMap = resultSetColumnInfoMap;
    }

    public DfTypeArrayInfo getTypeArrayInfo() {
        return _typeArrayInfo;
    }

    public void setTypeArrayInfo(DfTypeArrayInfo typeArrayInfo) {
        this._typeArrayInfo = typeArrayInfo;
    }
}
